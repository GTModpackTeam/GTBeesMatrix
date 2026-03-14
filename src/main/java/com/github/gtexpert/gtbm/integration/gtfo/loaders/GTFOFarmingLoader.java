package com.github.gtexpert.gtbm.integration.gtfo.loaders;

import java.lang.reflect.Field;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;

import forestry.api.core.ForestryAPI;
import forestry.api.farming.IFarmRegistry;
import forestry.farming.logic.farmables.FarmableSapling;

public class GTFOFarmingLoader {

    @SuppressWarnings("unchecked")
    public static void init() {
        IFarmRegistry farmRegistry = ForestryAPI.farmRegistry;
        try {
            Class<?> metaBlocksClass = Class.forName("gregtechfoodoption.block.GTFOMetaBlocks");
            Field saplingsField = metaBlocksClass.getField("GTFO_SAPLINGS");
            List<Block> saplings = (List<Block>) saplingsField.get(null);
            for (Block sapling : saplings) {
                farmRegistry.registerFarmables("farmArboreal",
                        new FarmableSapling(new ItemStack(sapling), new ItemStack[0]));
            }
        } catch (ReflectiveOperationException e) {
            com.github.gtexpert.gtbm.api.util.ModLog.logger.error("Failed to register GTFO saplings with Forestry farm",
                    e);
        }
    }
}
