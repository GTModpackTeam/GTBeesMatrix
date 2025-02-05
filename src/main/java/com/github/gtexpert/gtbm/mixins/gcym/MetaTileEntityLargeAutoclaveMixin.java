package com.github.gtexpert.gtbm.mixins.gcym;

import org.spongepowered.asm.mixin.Mixin;

import gregtech.integration.forestry.ForestryConfig;

import gregicality.multiblocks.common.metatileentities.multiblock.standard.MetaTileEntityLargeAutoclave;

import com.github.gtexpert.gtbm.api.capability.IMultiblockDistinctable;

@Mixin(value = MetaTileEntityLargeAutoclave.class, remap = false)
public class MetaTileEntityLargeAutoclaveMixin implements IMultiblockDistinctable {

    @Override
    public boolean canBeDistinct() {
        return ForestryConfig.enableGTBees;
    }
}
