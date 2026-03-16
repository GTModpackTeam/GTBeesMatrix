package com.github.gtexpert.gtbm.integration.forestry.loaders;

import net.minecraft.item.ItemStack;

import gregtech.common.blocks.MetaBlocks;
import gregtech.common.items.MetaItems;

import com.github.gtexpert.gtbm.integration.forestry.farming.FarmLogicCEu;
import com.github.gtexpert.gtbm.integration.forestry.farming.FarmableGTCEuSapling;
import com.github.gtexpert.gtbm.integration.forestry.util.ForestryFarmHelper;

import forestry.api.core.ForestryAPI;
import forestry.api.farming.IFarmProperties;
import forestry.api.farming.IFarmRegistry;
import forestry.core.items.EnumElectronTube;

public class FFMFarmingLoader {

    public static void init() {
        IFarmRegistry farmRegistry = ForestryAPI.farmRegistry;
        if (farmRegistry == null) return;

        // GregTech Rubber tree farm
        IFarmProperties ceuFarm = ForestryFarmHelper.registerFarmType("farmCEu", FarmLogicCEu::new,
                EnumElectronTube.TIN);

        String rubberId = "farmCEu.rubber";
        ceuFarm.registerFarmables(rubberId);
        farmRegistry.registerFarmables(rubberId,
                new FarmableGTCEuSapling(MetaBlocks.RUBBER_SAPLING,
                        new ItemStack[] { MetaItems.STICKY_RESIN.getStackForm() }));

        // GregTech Fertilizer
        farmRegistry.registerFertilizer(MetaItems.FERTILIZER.getStackForm(), 500);
    }
}
