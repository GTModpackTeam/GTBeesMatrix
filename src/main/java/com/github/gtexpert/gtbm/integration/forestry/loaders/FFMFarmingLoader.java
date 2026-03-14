package com.github.gtexpert.gtbm.integration.forestry.loaders;

import net.minecraft.item.ItemStack;

import gregtech.common.blocks.MetaBlocks;
import gregtech.common.items.MetaItems;

import forestry.api.core.ForestryAPI;
import forestry.api.farming.IFarmRegistry;
import forestry.farming.logic.farmables.FarmableSapling;

public class FFMFarmingLoader {

    public static void init() {
        IFarmRegistry farmRegistry = ForestryAPI.farmRegistry;

        // GregTech Rubber Sapling
        farmRegistry.registerFarmables("farmArboreal",
                new FarmableSapling(new ItemStack(MetaBlocks.RUBBER_SAPLING), new ItemStack[0]));

        // GregTech Fertilizer
        farmRegistry.registerFertilizer(MetaItems.FERTILIZER.getStackForm(), 500);
    }
}
