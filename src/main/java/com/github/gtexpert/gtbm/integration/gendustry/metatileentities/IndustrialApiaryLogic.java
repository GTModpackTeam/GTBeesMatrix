package com.github.gtexpert.gtbm.integration.gendustry.metatileentities;

import java.util.Collections;
import java.util.function.Supplier;

import net.bdew.gendustry.api.ApiaryModifiers;
import net.bdew.gendustry.api.items.IApiaryUpgrade;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.NonNullList;

import org.jetbrains.annotations.NotNull;

import gregtech.api.capability.IEnergyContainer;
import gregtech.api.capability.impl.RecipeLogicEnergy;
import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.recipes.RecipeMap;

import com.github.gtexpert.gtbm.integration.forestry.util.ForestryBeeHelper;

import forestry.api.apiculture.*;
import forestry.core.errors.EnumErrorCode;

/**
 * Custom work logic for the Industrial Apiary that bridges GregTech's
 * {@link RecipeLogicEnergy} with Forestry's {@link IBeekeepingLogic}.
 *
 * <p>
 * Instead of using GT recipe matching, this logic delegates bee work
 * to Forestry's beekeeping system while consuming EU for power.
 * </p>
 */
public class IndustrialApiaryLogic extends RecipeLogicEnergy {

    private static final int BASE_EU_PER_TICK = 80;
    /** Interval (ticks) between health-change checks for progress resync. */
    private static final int PROGRESS_SYNC_TICKS = 20;
    /** Duration (ticks) for princess breeding. */
    private static final int BREEDING_TIME_TICKS = 100;

    private IBeeRoot beeRoot;
    private IBeekeepingLogic beekeepingLogic;
    private NBTTagCompound pendingBeeLogicNBT;
    private boolean needsMovePrincess;
    /**
     * Set when {@link #beekeepingLogic} is first created on the server.
     * Forces a client sync on the next tick so the client receives queen
     * genome data that was missed by {@code writeInitialSyncData()}.
     */
    private boolean needsInitialSync;
    private int lastSyncedHealth = -1;
    private int progressSyncCounter;
    private int fxTickCounter;

    public IndustrialApiaryLogic(@NotNull MetaTileEntity metaTileEntity, RecipeMap<?> recipeMap,
                                 Supplier<IEnergyContainer> energyContainer) {
        super(metaTileEntity, recipeMap, energyContainer);
    }

    // ---- Accessors ----

    private MetaTileEntityIndustrialApiary getApiary() {
        return (MetaTileEntityIndustrialApiary) metaTileEntity;
    }

    /** Returns the cached {@link IBeeRoot}, lazily fetched from {@link BeeManager}. */
    public IBeeRoot getBeeRoot() {
        if (beeRoot == null) {
            beeRoot = BeeManager.beeRoot;
        }
        return beeRoot;
    }

    public IBeekeepingLogic getBeekeepingLogic() {
        return beekeepingLogic;
    }

    /**
     * Returns the EU/t consumption, scaled by the Gendustry energy modifier.
     *
     * @return at least 1 EU/t
     */
    public int getEUPerTick() {
        ApiaryModifiers mods = getApiary().getModifiers();
        float energyMod = (mods != null) ? mods.energy : 1.0F;
        return Math.max(1, Math.round(BASE_EU_PER_TICK * energyMod));
    }

    /**
     * Returns the number of ticks per queen health point, adjusted by the
     * lifespan modifier from installed upgrades.
     */
    public int getEffectiveTicksPerHealth() {
        ApiaryModifiers mods = getApiary().getModifiers();
        float lifespanMod = (mods != null) ? mods.lifespan : 1.0F;
        return ForestryBeeHelper.getEffectiveTicksPerHealth(lifespanMod);
    }

    /** Returns the current tick offset within the current health-point cycle. */
    public int getCycleTickCounter() {
        if (maxProgressTime <= 0) return 0;
        int effectiveTicks = getEffectiveTicksPerHealth();
        return effectiveTicks > 0 ? (progressTime % effectiveTicks) : 0;
    }

    /** Returns the normalized bee work progress (0.0 to 1.0). */
    public float getBeeProgress() {
        if (maxProgressTime <= 0) return 0;
        return (float) progressTime / maxProgressTime;
    }

    // ---- Initialization ----

    /**
     * Lazily creates the {@link IBeekeepingLogic} once the world is available.
     * On the server side, sets {@link #needsInitialSync} so that the first
     * {@link #updateServer()} call will sync queen data to the client.
     */
    private void initBeekeepingLogic() {
        if (beekeepingLogic == null && metaTileEntity.getWorld() != null) {
            IBeeRoot root = getBeeRoot();
            if (root != null) {
                beekeepingLogic = root.createBeekeepingLogic(getApiary());
                if (pendingBeeLogicNBT != null) {
                    beekeepingLogic.readFromNBT(pendingBeeLogicNBT);
                    pendingBeeLogicNBT = null;
                }
                if (!metaTileEntity.getWorld().isRemote) {
                    needsInitialSync = true;
                }
            }
        }
    }

    /**
     * Called from the client-side network handler to ensure the beekeeping
     * logic exists before reading sync data.
     */
    public void initBeekeepingLogicClient() {
        initBeekeepingLogic();
    }

    // ---- Modifier management ----

    /**
     * Resets all {@link ApiaryModifiers} to defaults, then applies each
     * installed {@link IApiaryUpgrade} from the upgrade inventory.
     */
    public void updateModifiers() {
        MetaTileEntityIndustrialApiary apiary = getApiary();
        ApiaryModifiers mods = apiary.getModifiers();
        if (mods == null) return;

        var upgradeInventory = apiary.getUpgradeInventory();
        if (upgradeInventory == null) return;

        // Reset to defaults
        mods.territory = 1;
        mods.mutation = 1;
        mods.lifespan = 1;
        mods.production = 1;
        mods.flowering = 1;
        mods.geneticDecay = 1;
        mods.energy = 1;
        mods.temperature = 0;
        mods.humidity = 0;
        mods.isSealed = false;
        mods.isSelfLighted = false;
        mods.isSunlightSimulated = false;
        mods.isAutomated = false;
        mods.isCollectingPollen = false;
        mods.biomeOverride = null;

        // Apply installed upgrades
        for (int i = 0; i < upgradeInventory.getSlots(); i++) {
            ItemStack stack = upgradeInventory.getStackInSlot(i);
            if (!stack.isEmpty() && stack.getItem() instanceof IApiaryUpgrade) {
                ((IApiaryUpgrade) stack.getItem()).applyModifiers(mods, stack);
            }
        }
    }

    // ---- Tick updates ----

    @Override
    public void update() {
        if (metaTileEntity.getWorld() == null) return;

        initBeekeepingLogic();

        if (metaTileEntity.getWorld().isRemote) {
            updateClient();
            return;
        }

        updateServer();
    }

    /**
     * Client-side tick: applies any buffered bee logic data that arrived
     * before the logic was initialized, then renders bee particle FX.
     */
    private void updateClient() {
        if (wasActiveAndNeedsUpdate) {
            wasActiveAndNeedsUpdate = false;
            setActive(false);
        }
        if (beekeepingLogic != null) {
            getApiary().applyPendingBeeLogicData();
            if (isActive) {
                fxTickCounter++;
                if (fxTickCounter % 2 == 0 && beekeepingLogic.canDoBeeFX()) {
                    beekeepingLogic.doBeeFX();
                }
            }
        }
    }

    /**
     * Server-side tick: updates modifiers, delegates to Forestry's
     * {@link IBeekeepingLogic#canWork()}/{@link IBeekeepingLogic#doWork()},
     * draws energy, and syncs bee state to clients when needed.
     */
    private void updateServer() {
        updateModifiers();

        if (beekeepingLogic == null) {
            if (isActive) setActive(false);
            return;
        }

        MetaTileEntityIndustrialApiary apiary = getApiary();
        needsMovePrincess = false;

        int euPerTick = getEUPerTick();
        boolean hasPower = getEnergyStored() >= euPerTick;

        // canWork() populates BeekeepingLogic.queenStack from the inventory
        // and clears errorLogic internally, so power error must be set AFTER
        boolean canWork = beekeepingLogic.canWork();
        apiary.getErrorLogic().setCondition(!hasPower, EnumErrorCode.NO_POWER);

        boolean isWorking = hasPower && workingEnabled && canWork;

        if (isWorking) {
            beekeepingLogic.doWork();
            drawEnergy(euPerTick, false);
            recipeEUt = euPerTick;
        } else {
            recipeEUt = 0;
        }

        if (canWork && workingEnabled && !hasPower) {
            hasNotEnoughEnergy = true;
        } else if (hasNotEnoughEnergy && getEnergyInputPerSecond() > 19L * euPerTick) {
            hasNotEnoughEnergy = false;
        }

        if (needsMovePrincess && apiary.getQueen().isEmpty()) {
            movePrincessToQueenSlot();
        }
        if (apiary.getModifiers().isAutomated) {
            moveBeesFromOutput(apiary);
        }

        // Sync AFTER canWork() so BeekeepingLogic.queenStack has genome data.
        // needsInitialSync covers chunk-reload where isActive == isWorking (both true)
        // and the normal state-change check would not trigger a sync.
        if (needsInitialSync || isActive != isWorking) {
            needsInitialSync = false;
            setActive(isWorking);
            apiary.syncBeeLogicToClient();
        }
        if (wasActiveAndNeedsUpdate) {
            wasActiveAndNeedsUpdate = false;
            setActive(false);
        }

        updateProgress(isWorking, apiary);
    }

    // ---- Progress tracking ----

    /**
     * Tracks bee work progress and triggers re-syncs when a health point
     * is consumed or Forestry's probabilistic aging changes the queen's health.
     */
    private void updateProgress(boolean isWorking, MetaTileEntityIndustrialApiary apiary) {
        if (isWorking && !apiary.getQueen().isEmpty()) {
            // Health point consumed — reset for next cycle
            if (maxProgressTime > 0 && progressTime >= maxProgressTime) {
                resetProgress();
                apiary.syncBeeLogicToClient();
            }
            if (maxProgressTime <= 0) {
                syncProgressFromBee(apiary);
            }

            progressSyncCounter++;
            if (progressSyncCounter >= PROGRESS_SYNC_TICKS && maxProgressTime > 0) {
                progressSyncCounter = 0;
                resyncIfHealthChanged(apiary);
            }

            if (maxProgressTime > 0 && progressTime < maxProgressTime) {
                progressTime++;
            }
        } else {
            resetProgress();
            progressSyncCounter = 0;
        }
    }

    private void resetProgress() {
        progressTime = 0;
        maxProgressTime = 0;
        lastSyncedHealth = -1;
    }

    /**
     * Initializes progress tracking from the current queen or princess.
     * For queens, uses the effective ticks-per-health; for princesses,
     * uses a fixed breeding duration.
     */
    private void syncProgressFromBee(MetaTileEntityIndustrialApiary apiary) {
        IBeeRoot root = getBeeRoot();
        if (root == null) return;

        EnumBeeType type = root.getType(apiary.getQueen());
        if (type == EnumBeeType.QUEEN) {
            IBee bee = root.getMember(apiary.getQueen());
            if (bee != null && bee.getMaxHealth() > 0 && bee.getHealth() > 0) {
                maxProgressTime = getEffectiveTicksPerHealth();
                progressTime = 0;
                lastSyncedHealth = bee.getHealth();
            }
        } else if (type == EnumBeeType.PRINCESS) {
            maxProgressTime = BREEDING_TIME_TICKS;
            progressTime = beekeepingLogic.getBeeProgressPercent();
            lastSyncedHealth = -1;
        }
    }

    /**
     * Detects when Forestry's probabilistic aging causes a health change
     * that wasn't tracked by our tick-based progress, and resyncs.
     */
    private void resyncIfHealthChanged(MetaTileEntityIndustrialApiary apiary) {
        IBeeRoot root = getBeeRoot();
        if (root == null || lastSyncedHealth < 0) return;

        IBee bee = root.getMember(apiary.getQueen());
        if (bee == null) return;

        int currentHealth = bee.getHealth();
        if (currentHealth != lastSyncedHealth) {
            lastSyncedHealth = currentHealth;
            progressTime = 0;
            apiary.syncBeeLogicToClient();
        }
    }

    // ---- Auto-breeding ----

    /** Flags that the queen died and a princess should be moved next tick. */
    public void setNeedsMovePrincess() {
        needsMovePrincess = true;
    }

    /**
     * Searches the output inventory for a princess and moves it to the
     * queen slot for auto-breeding.
     */
    public void movePrincessToQueenSlot() {
        IBeeRoot root = getBeeRoot();
        if (root == null) return;
        MetaTileEntityIndustrialApiary apiary = getApiary();
        var output = getOutputInventory();
        for (int i = 0; i < output.getSlots(); i++) {
            ItemStack stack = output.getStackInSlot(i);
            if (!stack.isEmpty() && root.isMember(stack, EnumBeeType.PRINCESS)) {
                apiary.setQueen(stack.copy());
                output.setStackInSlot(i, ItemStack.EMPTY);
                return;
            }
        }
    }

    /**
     * Scans the output inventory for bee members and moves them into
     * the appropriate bee slots when the Automation Upgrade is active.
     */
    private void moveBeesFromOutput(MetaTileEntityIndustrialApiary apiary) {
        IBeeRoot root = getBeeRoot();
        if (root == null) return;
        var output = getOutputInventory();
        for (int i = 0; i < output.getSlots(); i++) {
            ItemStack stack = output.getStackInSlot(i);
            if (stack.isEmpty() || !root.isMember(stack)) continue;
            if (root.isMember(stack, EnumBeeType.PRINCESS) && apiary.getQueen().isEmpty()) {
                apiary.setQueen(stack.copy());
                output.setStackInSlot(i, ItemStack.EMPTY);
            } else if (root.isMember(stack, EnumBeeType.DRONE) && apiary.getDrone().isEmpty()) {
                apiary.setDrone(stack.copy());
                output.setStackInSlot(i, ItemStack.EMPTY);
            }
        }
    }

    // ---- Recipe overrides (unused — bee work replaces GT recipe system) ----

    @Override
    protected void trySearchNewRecipe() {}

    @Override
    protected void updateRecipeProgress() {}

    // ---- Serialization ----

    /**
     * Persists bee logic state alongside the parent's NBT.
     * Guards against null collections that the parent may not initialize
     * when no GT recipes have run.
     */
    @Override
    public NBTTagCompound serializeNBT() {
        if (itemOutputs == null) {
            itemOutputs = NonNullList.create();
        }
        if (fluidOutputs == null) {
            fluidOutputs = Collections.emptyList();
        }
        recipeEUt = getEUPerTick();

        NBTTagCompound tag = super.serializeNBT();
        if (beekeepingLogic != null) {
            NBTTagCompound beeTag = new NBTTagCompound();
            beekeepingLogic.writeToNBT(beeTag);
            tag.setTag("BeekeepingLogic", beeTag);
        }
        return tag;
    }

    @Override
    public void deserializeNBT(@NotNull NBTTagCompound compound) {
        super.deserializeNBT(compound);
        if (compound.hasKey("BeekeepingLogic")) {
            pendingBeeLogicNBT = compound.getCompoundTag("BeekeepingLogic");
            initBeekeepingLogic();
        }
    }
}
