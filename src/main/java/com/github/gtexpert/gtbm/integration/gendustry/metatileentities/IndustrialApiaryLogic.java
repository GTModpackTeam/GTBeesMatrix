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

import forestry.api.apiculture.*;
import forestry.api.genetics.AlleleManager;
import forestry.core.errors.EnumErrorCode;

public class IndustrialApiaryLogic extends RecipeLogicEnergy {

    private IBeeRoot beeRoot;
    private IBeekeepingLogic beekeepingLogic;
    private boolean needsMovePrincess;

    public int getCycleTickCounter() {
        // Estimate current cycle position from progressTime
        if (maxProgressTime <= 0) return 0;
        int effectiveTicks = getEffectiveTicksPerHealth();
        return effectiveTicks > 0 ? (progressTime % effectiveTicks) : 0;
    }

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

        var upgradeInventory = apiary.getUpgradeInventory();
        if (upgradeInventory == null) return;

        for (int i = 0; i < upgradeInventory.getSlots(); i++) {
            ItemStack stack = upgradeInventory.getStackInSlot(i);
            if (!stack.isEmpty() && stack.getItem() instanceof IApiaryUpgrade) {
                ((IApiaryUpgrade) stack.getItem()).applyModifiers(mods, stack);
            }
        }
    }

    /**
     * Base power consumption matching Gendustry:
     * Gendustry uses 2 MJ/t internally = 20 RF/t displayed.
     * We use the displayed RF value directly as EU/t.
     */
    private static final int BASE_EU_PER_TICK = 20;

    public int getEUPerTick() {
        ApiaryModifiers mods = getApiary().getModifiers();
        float energyMod = (mods != null) ? mods.energy : 1.0F;
        return Math.max(1, Math.round(BASE_EU_PER_TICK * energyMod));
    }

    /**
     * Get the actual ticks per work cycle from Forestry's config.
     * Default: 550. Configurable in Forestry between 250-850.
     */
    private int getTicksPerWorkCycle() {
        return forestry.apiculture.ModuleApiculture.ticksPerBeeWorkCycle;
    }

    /**
     * Get the effective ticks per health point, accounting for lifespan modifier.
     * Higher lifespan modifier = queen lives longer = more ticks per health.
     * Forestry's age() logic: ageModifier = 1/lifespanModifier, health decreases by ageModifier per cycle.
     * So effective ticks per health = ticksPerWorkCycle * lifespanModifier.
     */
    private int getEffectiveTicksPerHealth() {
        int baseCycle = getTicksPerWorkCycle();
        ApiaryModifiers mods = getApiary().getModifiers();
        float lifespanMod = (mods != null) ? mods.lifespan : 1.0F;
        return Math.round(baseCycle * lifespanMod);
    }

    // ---- Core tick logic ----

    @Override
    public void update() {
        if (metaTileEntity.getWorld() == null || metaTileEntity.getWorld().isRemote) {
            // Handle wasActiveAndNeedsUpdate on client too
            if (wasActiveAndNeedsUpdate) {
                wasActiveAndNeedsUpdate = false;
                setActive(false);
            }
            return;
        }

        initBeekeepingLogic();
        if (beekeepingLogic == null) {
            if (isActive) setActive(false);
            return;
        }

        MetaTileEntityIndustrialApiary apiary = getApiary();
        needsMovePrincess = false;

        // Update modifiers from upgrades
        updateModifiers();

        // Power check
        int euPerTick = getEUPerTick();
        boolean hasPower = getEnergyStored() >= euPerTick;

        // canWork() calls errorLogic.clearErrors() internally, then sets bee-related errors.
        // We must set power error AFTER canWork() to avoid it being cleared.
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

        // Auto-move princess to queen slot
        if (needsMovePrincess && apiary.getQueen().isEmpty()) {
            doMovePrincess();
        }

        // Update GT active state: only active when actually working
        if (isActive != isWorking) {
            setActive(isWorking);
        }
        if (wasActiveAndNeedsUpdate) {
            wasActiveAndNeedsUpdate = false;
            setActive(false);
        }

        // Progress (smooth tick counter for TOP/GUI)
        if (isWorking && !apiary.getQueen().isEmpty()) {
            // Initialize max progress (only once per queen lifecycle)
            if (maxProgressTime <= 0) {
                IBeeRoot root = getBeeRoot();
                if (root != null) {
                    EnumBeeType type = root.getType(apiary.getQueen());
                    if (type == EnumBeeType.QUEEN) {
                        IBee bee = root.getMember(apiary.getQueen());
                        if (bee != null && bee.getMaxHealth() > 0 && bee.getHealth() > 0) {
                            int effectiveTicks = getEffectiveTicksPerHealth();
                            maxProgressTime = bee.getMaxHealth() * effectiveTicks;
                            progressTime = (bee.getMaxHealth() - bee.getHealth()) * effectiveTicks;
                        }
                    } else if (type == EnumBeeType.PRINCESS) {
                        maxProgressTime = 100;
                        progressTime = beekeepingLogic.getBeeProgressPercent();
                    }
                }
            }

            // Smooth increment every tick, stop at max
            if (maxProgressTime > 0 && progressTime < maxProgressTime) {
                progressTime++;
            }
        } else {
            progressTime = 0;
            maxProgressTime = 0;
        }
    }

    /**
     * Progress for GUI ProgressWidget (0.0 → 1.0).
     * Uses the same smooth tick-based values as TOP.
     */
    public float getBeeProgress() {
        if (maxProgressTime <= 0) return 0;
        return (float) progressTime / maxProgressTime;
    }

    // ---- Princess auto-move ----

    public void setNeedsMovePrincess() {
        needsMovePrincess = true;
    }

    private void doMovePrincess() {
        if (beeRoot == null) return;
        MetaTileEntityIndustrialApiary apiary = getApiary();
        for (int i = 0; i < apiary.getExportItems().getSlots(); i++) {
            ItemStack stack = apiary.getExportItems().getStackInSlot(i);
            if (!stack.isEmpty() && beeRoot.isMember(stack, EnumBeeType.PRINCESS)) {
                apiary.setQueen(stack);
                apiary.getExportItems().setStackInSlot(i, ItemStack.EMPTY);
                return;
            }
        }
    }

    // ---- Override recipe logic (no-op) ----

    @Override
    protected void trySearchNewRecipe() {}

    @Override
    protected void updateRecipeProgress() {}

    // ---- NBT: save/load beekeeping state ----

    @Override
    public NBTTagCompound serializeNBT() {
        // Parent serialization accesses itemOutputs/fluidOutputs when progressTime > 0.
        // Initialize them to empty to prevent NPE while preserving progress for TOP.
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
