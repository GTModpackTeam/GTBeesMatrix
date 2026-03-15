package com.github.gtexpert.gtbm.integration.gendustry.recipes;

import static gregtech.loaders.recipe.CraftingComponent.*;

import gregtech.api.GTValues;
import gregtech.api.recipes.ModHandler;
import gregtech.loaders.recipe.MetaTileEntityLoader;

import com.github.gtexpert.gtbm.api.util.Mods;
import com.github.gtexpert.gtbm.integration.gendustry.metatileentities.GendustryMetaTileEntities;

public class GendustryBlocksRecipe {

    public static void init() {
        machines();
    }

    private static void machines() {
        // Industrial Apiary
        MetaTileEntityLoader.registerMachineRecipe(true, GendustryMetaTileEntities.INDUSTRIAL_APIARY,
                "ACA", "RHR", "ASA",
                'A', Mods.Forestry.getItem("alveary.plain"),
                'C', Mods.Forestry.getItem("chipsets", 1, 1),
                'R', ROBOT_ARM,
                'H', HULL,
                'S', Mods.Forestry.getItem("alveary.sieve"));
        ModHandler.addShapelessRecipe("gendustry_to_gtbm_industrial_apiary",
                GendustryMetaTileEntities.INDUSTRIAL_APIARY[GTValues.HV].getStackForm(),
                Mods.Gendustry.getItem("industrial_apiary"));
    }
}
