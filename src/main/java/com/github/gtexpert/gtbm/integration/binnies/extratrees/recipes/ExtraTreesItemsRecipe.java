package com.github.gtexpert.gtbm.integration.binnies.extratrees.recipes;

import static gregtech.api.unification.ore.OrePrefix.*;

import gregtech.api.recipes.ModHandler;
import gregtech.api.unification.material.MarkerMaterials;
import gregtech.api.unification.material.Materials;
import gregtech.api.unification.stack.UnificationEntry;

import com.github.gtexpert.gtbm.api.util.Mods;
import com.github.gtexpert.gtbm.integration.forestry.ForestryConfigHolder;
import com.github.gtexpert.gtbm.integration.forestry.ForestryUtility;
import com.github.gtexpert.gtbm.integration.forestry.recipes.machines.CarpenterLoader;

import forestry.api.recipes.RecipeManagers;

public class ExtraTreesItemsRecipe {

    public static void init() {
        Enum<ForestryUtility.recipeMode> recipeMode = ForestryUtility.recipeMode
                .safeValueOf(ForestryConfigHolder.gameMode);

        // Arborist Database
        ModHandler.addShapelessNBTClearingRecipe("arborist_database_nbt",
                Mods.ExtraTrees.getItem("databasetree"),
                Mods.ExtraTrees.getItem("databasetree"));

        // Lepidopterist Database
        ModHandler.addShapelessNBTClearingRecipe("lepidopterist_database_nbt",
                Mods.ExtraTrees.getItem("databaselepi"),
                Mods.ExtraTrees.getItem("databaselepi"));

        if (recipeMode == ForestryUtility.recipeMode.HARD) {
            // Arborist Database
            CarpenterLoader.removeCarpenterRecipe(Mods.ExtraTrees.getItem("databasetree"));
            RecipeManagers.carpenterManager.addRecipe(
                    20, Materials.Redstone.getFluid(1440),
                    Mods.Forestry.getItem("portable_alyzer"), Mods.ExtraTrees.getItem("databasetree"),
                    "SPS", "DCE", "SPS",
                    'S', new UnificationEntry(screw, Materials.Copper).toString(),
                    'D', new UnificationEntry(plate, Materials.Diamond).toString(),
                    'E', new UnificationEntry(plate, Materials.Emerald).toString(),
                    'P', new UnificationEntry(plate, Materials.Gold).toString(),
                    'C', new UnificationEntry(circuit, MarkerMaterials.Tier.HV).toString());

            // Lepidopterist Database
            CarpenterLoader.removeCarpenterRecipe(Mods.ExtraTrees.getItem("databaselepi"));
            RecipeManagers.carpenterManager.addRecipe(
                    20, Materials.Redstone.getFluid(1440),
                    Mods.Forestry.getItem("portable_alyzer"), Mods.ExtraTrees.getItem("databaselepi"),
                    "SPS", "DCE", "SPS",
                    'S', new UnificationEntry(screw, Materials.Tin).toString(),
                    'D', new UnificationEntry(plate, Materials.Diamond).toString(),
                    'E', new UnificationEntry(plate, Materials.Emerald).toString(),
                    'P', new UnificationEntry(plate, Materials.Gold).toString(),
                    'C', new UnificationEntry(circuit, MarkerMaterials.Tier.HV).toString());
        }
    }
}
