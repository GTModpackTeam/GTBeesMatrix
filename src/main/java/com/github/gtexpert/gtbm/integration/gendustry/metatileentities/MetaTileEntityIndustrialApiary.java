package com.github.gtexpert.gtbm.integration.gendustry.metatileentities;

import java.util.function.Function;

import net.minecraft.util.ResourceLocation;

import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.metatileentity.interfaces.IGregTechTileEntity;
import gregtech.api.recipes.RecipeMap;
import gregtech.client.renderer.ICubeRenderer;

import com.github.gtexpert.gtbm.common.metatileentities.GTBMSimpleMachineMetaTileEntity;

public class MetaTileEntityIndustrialApiary extends GTBMSimpleMachineMetaTileEntity {

    public MetaTileEntityIndustrialApiary(ResourceLocation metaTileEntityId, RecipeMap<?> recipeMap,
                                          ICubeRenderer renderer, int tier, boolean hasFrontFacing,
                                          Function<Integer, Integer> tankScalingFunction) {
        super(metaTileEntityId, recipeMap, renderer, tier, hasFrontFacing, tankScalingFunction);
    }

    @Override
    public MetaTileEntity createMetaTileEntity(IGregTechTileEntity tileEntity) {
        return new MetaTileEntityIndustrialApiary(metaTileEntityId, workable.getRecipeMap(), renderer, getTier(),
                hasFrontFacing(), getTankScalingFunction());
    }

    private IndustrialApiaryLogic getLogic() {
        return (IndustrialApiaryLogic) workable;
    }
}
