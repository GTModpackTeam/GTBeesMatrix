package com.github.gtexpert.gtbm.integration.binnies.recipes;

import gregtech.api.recipes.ModHandler;
import gregtech.api.unification.material.Materials;

import com.github.gtexpert.gtbm.api.util.Mods;
import com.github.gtexpert.gtbm.integration.forestry.ForestryConfigHolder;
import com.github.gtexpert.gtbm.integration.forestry.ForestryUtility;

import forestry.api.recipes.RecipeManagers;

public class BinniesItemsRecipe {

    public static void init() {
        Enum<ForestryUtility.recipeMode> recipeMode = ForestryUtility.recipeMode.safeValueOf(ForestryConfigHolder.gameMode);

        if (recipeMode == ForestryUtility.recipeMode.HARD) {
            // Registry
            ModHandler.removeRecipeByName(Mods.Genetics.getResource("registry"));
            RecipeManagers.carpenterManager.addRecipe(
                    40, Materials.Redstone.getFluid(4320),
                    Mods.Forestry.getItem("portable_alyzer"), Mods.Genetics.getItem("registry"),
                    "IAI", "BCP", "ILI",
                    'I', Mods.Genetics.getItem("misc", 1, 8),
                    'A', Mods.ExtraTrees.getItem("databasetree"),
                    'B', Mods.Botany.getItem("database"),
                    'C', Mods.Genetics.getItem("misc", 1, 9),
                    'P', Mods.ExtraBees.getItem("dictionary"),
                    'L', Mods.ExtraTrees.getItem("databaselepi"));
        }
    }
}
