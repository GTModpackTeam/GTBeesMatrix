package com.github.gtexpert.gtbm.loaders.recipe;

import static gregtech.api.unification.ore.OrePrefix.*;

import gregtech.api.recipes.RecipeMaps;
import gregtech.api.unification.material.Materials;

public class CEuOverrideRecipe {

    public static void init() {
        material();
    }

    public static void material() {
        // Arsenic Block
        RecipeMaps.COMPRESSOR_RECIPES.recipeBuilder()
                .input(dust, Materials.Arsenic, 9)
                .output(block, Materials.Arsenic)
                .duration(300).EUt(2).buildAndRegister();

        // TricalciumPhosphate Block
        RecipeMaps.COMPRESSOR_RECIPES.recipeBuilder()
                .input(dust, Materials.TricalciumPhosphate, 9)
                .output(block, Materials.TricalciumPhosphate)
                .duration(300).EUt(2).buildAndRegister();
    }
}
