package com.github.gtexpert.gtbm.integration.gtfo.farming;

import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;

import forestry.api.farming.IFarmProperties;
import forestry.farming.logic.FarmLogicArboreal;

public class FarmLogicGTFO extends FarmLogicArboreal {

    public FarmLogicGTFO(IFarmProperties properties, boolean isManual) {
        super(properties, isManual);
    }

    @Override
    public String getUnlocalizedName() {
        return "gtbm.farm.gtfo";
    }

    @Override
    public ItemStack getIconItemStack() {
        return new ItemStack(Blocks.SAPLING);
    }
}
