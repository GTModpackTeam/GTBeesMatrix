package com.github.gtexpert.gtbm.integration.gendustry.metatileentities;

import java.util.function.Supplier;

import net.bdew.gendustry.api.ApiaryModifiers;
import net.bdew.gendustry.api.items.IApiaryUpgrade;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import org.jetbrains.annotations.NotNull;

import gregtech.api.capability.IEnergyContainer;
import gregtech.api.capability.impl.RecipeLogicEnergy;
import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.recipes.RecipeMap;

import com.github.gtexpert.gtbm.integration.forestry.ForestryUtility;

import forestry.api.apiculture.*;
import forestry.api.genetics.AlleleManager;
import forestry.core.errors.EnumErrorCode;

public class IndustrialApiaryLogic extends RecipeLogicEnergy {

    private static final int BASE_EU_PER_TICK = 80;
    private static final int PROGRESS_SYNC_TICKS = 20;
    private static final int BREEDING_TIME_TICKS = 100;

    private IBeeRoot beeRoot;
    private IBeekeepingLogic beekeepingLogic;
    private boolean needsMovePrincess;
    private int lastSyncedHealth = -1;
    private int progressSyncCounter;
    private int fxTickCounter;

    public IndustrialApiaryLogic(@NotNull MetaTileEntity metaTileEntity, RecipeMap<?> recipeMap,
                                 Supplier<IEnergyContainer> energyContainer) {
        super(metaTileEntity, recipeMap, energyContainer);
    }

    private MetaTileEntityIndustrialApiary getApiary() {
        return (MetaTileEntityIndustrialApiary) metaTileEntity;
    }

    // ---- Bee system initialization ----

    public IBeeRoot getBeeRoot() {
        if (beeRoot == null) {
            beeRoot = (IBeeRoot) AlleleManager.alleleRegistry.getSpeciesRoot("rootBees");
        }
        return beeRoot;
    }

    private void initBeekeepingLogic() {
        if (beekeepingLogic == null && metaTileEntity.getWorld() != null) {
            IBeeRoot root = getBeeRoot();
            if (root != null) {
                beekeepingLogic = root.createBeekeepingLogic(getApiary());
            }
        }
    }

    public IBeekeepingLogic getBeekeepingLogic() {
        return beekeepingLogic;
    }

    // ---- Modifier management ----

    public void updateModifiers() {
        MetaTileEntityIndustrialApiary apiary = getApiary();
        ApiaryModifiers mods = apiary.getModifiers();
        if (mods == null) return;

        var upgradeInventory = apiary.getUpgradeInventory();
        if (upgradeInventory == null) return;

        // Reset modifiers to defaults
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

        for (int i = 0; i < upgradeInventory.getSlots(); i++) {
            ItemStack stack = upgradeInventory.getStackInSlot(i);
            if (!stack.isEmpty() && stack.getItem() instanceof IApiaryUpgrade) {
                ((IApiaryUpgrade) stack.getItem()).applyModifiers(mods, stack);
            }
        }
    }

    // ---- Energy & timing ----

    public int getEUPerTick() {
        ApiaryModifiers mods = getApiary().getModifiers();
        float energyMod = (mods != null) ? mods.energy : 1.0F;
        return Math.max(1, Math.round(BASE_EU_PER_TICK * energyMod));
    }

    public int getEffectiveTicksPerHealth() {
        ApiaryModifiers mods = getApiary().getModifiers();
        float lifespanMod = (mods != null) ? mods.lifespan : 1.0F;
        return ForestryUtility.getEffectiveTicksPerHealth(lifespanMod);
    }

    public int getCycleTickCounter() {
        if (maxProgressTime <= 0) return 0;
        int effectiveTicks = getEffectiveTicksPerHealth();
        return effectiveTicks > 0 ? (progressTime % effectiveTicks) : 0;
    }

    // ---- Core tick logic ----

    @Override
    public void update() {
        if (metaTileEntity.getWorld() == null) return;

        initBeekeepingLogic();

        // Client-side: bee particle FX
        if (metaTileEntity.getWorld().isRemote) {
            updateClient();
            return;
        }

        // Server-side: bee processing
        updateServer();
    }

    private void updateClient() {
        if (wasActiveAndNeedsUpdate) {
            wasActiveAndNeedsUpdate = false;
            setActive(false);
        }
        if (beekeepingLogic != null && isActive) {
            fxTickCounter++;
            if (fxTickCounter % 2 == 0 && beekeepingLogic.canDoBeeFX()) {
                beekeepingLogic.doBeeFX();
            }
        }
    }

    private void updateServer() {
        if (beekeepingLogic == null) {
            if (isActive) setActive(false);
            return;
        }

        MetaTileEntityIndustrialApiary apiary = getApiary();
        needsMovePrincess = false;

        updateModifiers();

        int euPerTick = getEUPerTick();
        boolean hasPower = getEnergyStored() >= euPerTick;

        // canWork() clears errors internally, so set power error AFTER
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

        // GT energy indicator
        if (canWork && workingEnabled && !hasPower) {
            hasNotEnoughEnergy = true;
        } else if (hasNotEnoughEnergy && getEnergyInputPerSecond() > 19L * euPerTick) {
            hasNotEnoughEnergy = false;
        }

        // Auto-move princess
        if (needsMovePrincess && apiary.getQueen().isEmpty()) {
            movePrincessToQueenSlot();
        }

        // Active state + FX sync
        if (isActive != isWorking) {
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

    private void updateProgress(boolean isWorking, MetaTileEntityIndustrialApiary apiary) {
        if (isWorking && !apiary.getQueen().isEmpty()) {
            // Reset when progress completes (handles princess→queen transition)
            if (maxProgressTime > 0 && progressTime >= maxProgressTime) {
                progressTime = 0;
                maxProgressTime = 0;
                lastSyncedHealth = -1;
                apiary.syncBeeLogicToClient();
            }
            if (maxProgressTime <= 0) {
                syncProgressFromBee(apiary);
            }

            // Periodic resync with actual health
            progressSyncCounter++;
            if (progressSyncCounter >= PROGRESS_SYNC_TICKS && maxProgressTime > 0) {
                progressSyncCounter = 0;
                resyncIfHealthChanged(apiary);
            }

            if (maxProgressTime > 0 && progressTime < maxProgressTime) {
                progressTime++;
            }
        } else {
            progressTime = 0;
            maxProgressTime = 0;
            lastSyncedHealth = -1;
            progressSyncCounter = 0;
        }
    }

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

    public float getBeeProgress() {
        if (maxProgressTime <= 0) return 0;
        return (float) progressTime / maxProgressTime;
    }

    // ---- Princess auto-move ----

    public void setNeedsMovePrincess() {
        needsMovePrincess = true;
    }

    public void movePrincessToQueenSlot() {
        IBeeRoot root = getBeeRoot();
        if (root == null) return;
        MetaTileEntityIndustrialApiary apiary = getApiary();
        var output = getOutputInventory();
        for (int i = 0; i < output.getSlots(); i++) {
            ItemStack stack = output.getStackInSlot(i);
            if (!stack.isEmpty() && root.isMember(stack, EnumBeeType.PRINCESS)) {
                apiary.setQueen(stack);
                output.setStackInSlot(i, ItemStack.EMPTY);
                return;
            }
        }
    }

    // ---- Override recipe logic (no-op) ----

    @Override
    protected void trySearchNewRecipe() {}

    @Override
    protected void updateRecipeProgress() {}

    // ---- NBT ----

    @Override
    public NBTTagCompound serializeNBT() {
        if (itemOutputs == null) {
            itemOutputs = net.minecraft.util.NonNullList.create();
        }
        if (fluidOutputs == null) {
            fluidOutputs = java.util.Collections.emptyList();
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
            initBeekeepingLogic();
            if (beekeepingLogic != null) {
                beekeepingLogic.readFromNBT(compound.getCompoundTag("BeekeepingLogic"));
            }
        }
    }
}
