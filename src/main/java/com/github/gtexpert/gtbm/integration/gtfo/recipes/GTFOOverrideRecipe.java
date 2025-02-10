package com.github.gtexpert.gtbm.integration.gtfo.recipes;

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

import gregtech.api.recipes.GTRecipeHandler;
import gregtech.api.recipes.RecipeMaps;

import com.github.gtexpert.gtbm.api.util.Mods;

import forestry.core.fluids.Fluids;

public class GTFOOverrideRecipe {

    public static void init() {
        GTRecipeHandler.removeRecipesByInputs(RecipeMaps.EXTRACTOR_RECIPES, new ItemStack(Items.APPLE));
        GTRecipeHandler.removeRecipesByInputs(RecipeMaps.EXTRACTOR_RECIPES, new ItemStack(Items.CARROT));

        // Forestry Recipe
        RecipeMaps.EXTRACTOR_RECIPES.recipeBuilder()
                .input(Items.APPLE)
                .circuitMeta(1)
                .chancedOutput(Mods.Forestry.getItem("mulch"), 2000, 0)
                .fluidOutputs(Fluids.JUICE.getFluid(200))
                .EUt(7).duration(32).buildAndRegister();

        RecipeMaps.EXTRACTOR_RECIPES.recipeBuilder()
                .input(Items.CARROT)
                .circuitMeta(1)
                .chancedOutput(Mods.Forestry.getItem("mulch"), 2000, 0)
                .fluidOutputs(Fluids.JUICE.getFluid(200))
                .EUt(7).duration(32).buildAndRegister();

        // GTFO Recipe
        RecipeMaps.EXTRACTOR_RECIPES.recipeBuilder().EUt(1920).duration(200)
                .input(Items.CARROT)
                .circuitMeta(2)
                .outputs(Mods.GregTechFoodOption.getItem("gtfo_meta_item", 1, 52))
                .buildAndRegister();
        RecipeMaps.EXTRACTOR_RECIPES.recipeBuilder().EUt(1920).duration(200)
                .input(Items.APPLE)
                .circuitMeta(2)
                .outputs(Mods.GregTechFoodOption.getItem("gtfo_meta_item", 1, 53))
                .buildAndRegister();
    }
}
