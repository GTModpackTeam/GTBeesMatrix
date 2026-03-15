package com.github.gtexpert.gtbm.integration.gtfo.loaders;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;

import com.github.gtexpert.gtbm.integration.forestry.util.ForestryFarmHelper;
import com.github.gtexpert.gtbm.integration.gtfo.farming.FarmLogicGTFO;
import com.github.gtexpert.gtbm.integration.gtfo.farming.FarmableGTFOSapling;

import forestry.api.core.ForestryAPI;
import forestry.api.farming.IFarmProperties;
import forestry.api.farming.IFarmRegistry;
import forestry.core.items.EnumElectronTube;

public class GTFOFarmingLoader {

    public static final String FARM_ID = "farmGTFO";

    @SuppressWarnings("unchecked")
    public static void init() {
        IFarmRegistry registry = ForestryAPI.farmRegistry;

        // GTFO Fruit tree farm
        IFarmProperties fruitFarm = ForestryFarmHelper.registerFarmType(FARM_ID, FarmLogicGTFO::new,
                EnumElectronTube.COPPER);

        // Register each GTFO tree under its own identifier for separate JEI display
        try {
            Class<?> metaBlocksClass = Class.forName("gregtechfoodoption.block.GTFOMetaBlocks");
            Field saplingsField = metaBlocksClass.getField("GTFO_SAPLINGS");
            List<Block> saplings = (List<Block>) saplingsField.get(null);

            Class<?> treeClass = Class.forName("gregtechfoodoption.worldgen.trees.GTFOTree");
            Field treesField = treeClass.getField("TREES");
            Field nameField = treeClass.getField("name");
            List<?> trees = (List<?>) treesField.get(null);
            Method getAppleMethod = treeClass.getMethod("getApple");

            for (int i = 0; i < trees.size(); i++) {
                Block saplingBlock = saplings.get(i / 8);
                int itemDamage = (i % 8) << 1;

                Object tree = trees.get(i);
                String treeName = (String) nameField.get(tree);
                ItemStack apple = (ItemStack) getAppleMethod.invoke(tree);
                ItemStack[] windfall = apple.isEmpty() ? new ItemStack[0] : new ItemStack[] { apple };

                String subId = FARM_ID + "." + treeName;
                fruitFarm.registerFarmables(subId);
                registry.registerFarmables(subId,
                        new FarmableGTFOSapling(saplingBlock, itemDamage, windfall));
            }
        } catch (ReflectiveOperationException e) {
            com.github.gtexpert.gtbm.api.util.ModLog.logger.error(
                    "Failed to register GTFO saplings with Forestry farm", e);
        }
    }
}
