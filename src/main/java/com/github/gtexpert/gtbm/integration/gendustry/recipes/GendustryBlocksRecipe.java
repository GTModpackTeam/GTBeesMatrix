package com.github.gtexpert.gtbm.integration.gendustry.recipes;

import static gregtech.loaders.recipe.CraftingComponent.*;

import gregtech.api.GTValues;
import gregtech.api.recipes.ModHandler;
import gregtech.api.recipes.RecipeMaps;
import gregtech.api.unification.material.MarkerMaterials;
import gregtech.api.unification.material.Materials;
import gregtech.api.unification.ore.OrePrefix;
import gregtech.common.items.MetaItems;
import gregtech.loaders.recipe.MetaTileEntityLoader;

import com.github.gtexpert.gtbm.api.util.Mods;
import com.github.gtexpert.gtbm.integration.gendustry.metatileentities.GendustryMetaTileEntities;

import forestry.core.fluids.Fluids;

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

        // Mega Apiary
        RecipeMaps.ASSEMBLY_LINE_RECIPES.recipeBuilder()
                .input(GendustryMetaTileEntities.INDUSTRIAL_APIARY[GTValues.UV], 64)
                .inputs(Mods.Gendustry.getItem("apiary.upgrade", 64, 0))
                .inputs(Mods.Gendustry.getItem("apiary.upgrade", 64, 19))
                .input(MetaItems.ROBOT_ARM_UV, 16)
                .input(OrePrefix.circuit, MarkerMaterials.Tier.UV, 16)
                .fluidInputs(Materials.NaquadahAlloy.getFluid(144 * (3 * 64 + 8)))
                .fluidInputs(Fluids.FOR_HONEY.getFluid(20000))
                .stationResearch(b -> b
                        .researchStack(GendustryMetaTileEntities.INDUSTRIAL_APIARY[GTValues.UV].getStackForm())
                        .CWUt(16, 16 * 16 * (2 * 120 + 50) * 20)
                        .EUt(GTValues.VA[GTValues.ZPM]))
                .output(GendustryMetaTileEntities.MEGA_APIARY)
                .duration(5 * 60 * 20)
                .EUt(GTValues.VA[GTValues.UHV])
                .buildAndRegister();
    }
}
