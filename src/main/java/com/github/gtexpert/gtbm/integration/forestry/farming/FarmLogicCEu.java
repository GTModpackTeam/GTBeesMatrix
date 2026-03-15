package com.github.gtexpert.gtbm.integration.forestry.farming;

import net.minecraft.item.ItemStack;

import gregtech.common.blocks.MetaBlocks;

import forestry.api.farming.IFarmProperties;
import forestry.farming.logic.FarmLogicArboreal;

public class FarmLogicCEu extends FarmLogicArboreal {

    public FarmLogicCEu(IFarmProperties properties, boolean isManual) {
        super(properties, isManual);
    }

    @Override
    public String getUnlocalizedName() {
        return "gtbm.farm.ceu";
    }

    @Override
    public ItemStack getIconItemStack() {
        return new ItemStack(MetaBlocks.RUBBER_SAPLING);
    }
}
