package com.github.gtexpert.gtbm.mixins.forestry;

import java.util.Map;

import net.minecraft.block.Block;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import com.google.common.collect.HashMultimap;

import forestry.apiculture.flowers.FlowerRegistry;
import forestry.core.utils.BlockStateSet;

/**
 * Exposes internal flower registration data from {@link FlowerRegistry}
 * for tooltip display of accepted flower blocks per flower type.
 */
@Mixin(value = FlowerRegistry.class, remap = false)
public interface FlowerRegistryAccessor {

    @Accessor("acceptableBlocks")
    HashMultimap<String, Block> gtbm$getAcceptableBlocks();

    @Accessor("acceptableBlockStates")
    Map<String, BlockStateSet> gtbm$getAcceptableBlockStates();
}
