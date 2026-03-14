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

    private static final int BASE_EU_PER_TICK = 32;

    private IBeeRoot beeRoot;
    private IBeekeepingLogic beekeepingLogic;
    private boolean needsMovePrincess;

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

    public int getEUPerTick() {
        ApiaryModifiers mods = getApiary().getModifiers();
        if (mods == null) return BASE_EU_PER_TICK;
        return (int) (BASE_EU_PER_TICK * mods.energy);
    }

    // ---- Core tick logic ----

    @Override
    public void update() {
        if (metaTileEntity.getWorld() == null || metaTileEntity.getWorld().isRemote) return;

        initBeekeepingLogic();
        if (beekeepingLogic == null) return;

        MetaTileEntityIndustrialApiary apiary = getApiary();
        needsMovePrincess = false;

        // Update modifiers from upgrades
        updateModifiers();

        // Power check
        int euPerTick = getEUPerTick();
        boolean hasPower = getEnergyStored() >= euPerTick;

        apiary.getErrorLogic().setCondition(!hasPower, EnumErrorCode.NO_POWER);

        // Beekeeping tick
        boolean canWork = beekeepingLogic.canWork();
        boolean isWorking = hasPower && workingEnabled && canWork;

        if (isWorking) {
            beekeepingLogic.doWork();
            drawEnergy(euPerTick, false);
        }

        // GT energy indicator: only show when actively working but can't draw power
        // (matches GT default: only during active recipe processing)
        if (canWork && workingEnabled && !hasPower) {
            hasNotEnoughEnergy = true;
        } else if (hasNotEnoughEnergy && getEnergyInputPerSecond() > 19L * euPerTick) {
            hasNotEnoughEnergy = false;
        }

        // Auto-move princess to queen slot
        if (needsMovePrincess && apiary.getQueen().isEmpty()) {
            doMovePrincess();
        }

        // Update GT active state
        boolean hasQueen = !apiary.getQueen().isEmpty();
        boolean shouldBeActive = isWorking || (hasQueen && canWork && workingEnabled);

        if (isActive != shouldBeActive) {
            setActive(shouldBeActive);
        }

        // Progress for GUI
        if (hasQueen && beekeepingLogic.getBeeProgressPercent() > 0) {
            progressTime = beekeepingLogic.getBeeProgressPercent();
            maxProgressTime = 100;
        } else {
            progressTime = 0;
            maxProgressTime = 0;
        }
    }

    public float getBeeProgress() {
        if (beekeepingLogic == null || getApiary().getQueen().isEmpty()) return 0;
        return beekeepingLogic.getBeeProgressPercent() / 100F;
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
