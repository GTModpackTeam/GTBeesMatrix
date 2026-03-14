package com.github.gtexpert.gtbm.integration.gendustry.recipes;

import static gregtech.api.unification.ore.OrePrefix.*;

import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;

import gregtech.api.recipes.ModHandler;
import gregtech.api.unification.material.Materials;
import gregtech.api.unification.stack.UnificationEntry;

import com.github.gtexpert.gtbm.api.util.Mods;
import com.github.gtexpert.gtbm.integration.gendustry.GendustryConfigHolder;

public class GendustryBlocksRecipe {

    public static void init() {
        machines();
        industrialApiaryRecipes();
    }

    private static void machines() {
        // Mutagen Producer
        if (GendustryConfigHolder.MutagenProducer) {
            ModHandler.addShapedRecipe(true, "gendustry_mutagen_producer",
                    Mods.Gendustry.getItem("mutagen_producer"),
                    "BHB", "POP", "AMA",
                    'B', new UnificationEntry(ingot, Materials.Bronze),
                    'H', new ItemStack(Blocks.HOPPER),
                    'P', Mods.Gendustry.getItem("power_module"),
                    'O', Mods.Forestry.getItem("sturdy_machine"),
                    'A', new UnificationEntry(gear, Materials.Bronze),
                    'M', Mods.Gendustry.getItem("mutagen_tank"));
        }

        // Mutatron
        if (GendustryConfigHolder.Mutatron) {
            ModHandler.addShapedRecipe(true, "gendustry_mutatron",
                    Mods.Gendustry.getItem("mutatron"),
                    "TZB", "POT", "TMB",
                    'T', Mods.Gendustry.getItem("bee_receptacle"),
                    'Z', Mods.Gendustry.getItem("genetics_processor"),
                    'B', new UnificationEntry(ingot, Materials.Bronze),
                    'P', Mods.Gendustry.getItem("power_module"),
                    'O', Mods.Forestry.getItem("sturdy_machine"),
                    'M', Mods.Gendustry.getItem("mutagen_tank"));
        }

        // Industrial Apiary
        if (GendustryConfigHolder.IndustrialApiary) {
            ModHandler.addShapedRecipe(true, "gendustry_industrial_apiary",
                    Mods.Gendustry.getItem("industrial_apiary"),
                    "LTL", "LOL", "ASA",
                    'L', "blockGlass",
                    'T', Mods.Gendustry.getItem("bee_receptacle"),
                    'O', Mods.Forestry.getItem("sturdy_machine"),
                    'A', new UnificationEntry(gear, Materials.Bronze),
                    'S', new ItemStack(Blocks.PISTON));
        }

        // Genetic Imprinter
        if (GendustryConfigHolder.GeneticImprinter) {
            ModHandler.addShapedRecipe(true, "gendustry_genetic_imprinter",
                    Mods.Gendustry.getItem("genetic_imprinter"),
                    "AZA", "TOT", "APA",
                    'A', new UnificationEntry(gear, Materials.Bronze),
                    'Z', Mods.Gendustry.getItem("genetics_processor"),
                    'T', Mods.Gendustry.getItem("bee_receptacle"),
                    'O', Mods.Forestry.getItem("sturdy_machine"),
                    'P', Mods.Gendustry.getItem("power_module"));
        }

        // Genetic Sampler
        if (GendustryConfigHolder.GeneticSampler) {
            ModHandler.addShapedRecipe(true, "gendustry_sampler",
                    Mods.Gendustry.getItem("sampler"),
                    "AZA", "TOD", "APA",
                    'A', new UnificationEntry(gear, Materials.Bronze),
                    'Z', Mods.Gendustry.getItem("genetics_processor"),
                    'T', Mods.Gendustry.getItem("bee_receptacle"),
                    'O', Mods.Forestry.getItem("sturdy_machine"),
                    'D', new UnificationEntry(gem, Materials.Diamond),
                    'P', Mods.Gendustry.getItem("power_module"));
        }

        // Advanced Mutatron
        if (GendustryConfigHolder.AdvancedMutagen) {
            ModHandler.addShapedRecipe(true, "gendustry_mutatron_advanced",
                    Mods.Gendustry.getItem("mutatron_advanced"),
                    "AQA", "ZMZ", "APA",
                    'A', new UnificationEntry(gear, Materials.Bronze),
                    'Q', new UnificationEntry(gem, Materials.NetherQuartz),
                    'Z', Mods.Gendustry.getItem("genetics_processor"),
                    'M', Mods.Gendustry.getItem("mutatron"),
                    'P', Mods.Gendustry.getItem("power_module"));
        }

        // Protein Liquifier
        if (GendustryConfigHolder.ProteinLiquifier) {
            ModHandler.addShapedRecipe(true, "gendustry_protein_liquifier",
                    Mods.Gendustry.getItem("protein_liquifier"),
                    "AHA", "SOS", "APA",
                    'A', new UnificationEntry(gear, Materials.Bronze),
                    'H', new ItemStack(Blocks.HOPPER),
                    'S', new ItemStack(Blocks.PISTON),
                    'O', Mods.Forestry.getItem("sturdy_machine"),
                    'P', Mods.Gendustry.getItem("power_module"));
        }

        // DNA Extractor
        if (GendustryConfigHolder.DNAExtractor) {
            ModHandler.addShapedRecipe(true, "gendustry_dna_extractor",
                    Mods.Gendustry.getItem("dna_extractor"),
                    "AHA", "ZOZ", "APA",
                    'A', new UnificationEntry(gear, Materials.Bronze),
                    'H', new ItemStack(Blocks.HOPPER),
                    'Z', Mods.Gendustry.getItem("genetics_processor"),
                    'O', Mods.Forestry.getItem("sturdy_machine"),
                    'P', Mods.Gendustry.getItem("power_module"));
        }

        // Genetic Transposer
        if (GendustryConfigHolder.GeneticTransposer) {
            ModHandler.addShapedRecipe(true, "gendustry_transposer",
                    Mods.Gendustry.getItem("transposer"),
                    "ABA", "ZOZ", "APA",
                    'A', new UnificationEntry(gear, Materials.Bronze),
                    'B', new UnificationEntry(ingot, Materials.Bronze),
                    'Z', Mods.Gendustry.getItem("genetics_processor"),
                    'O', Mods.Forestry.getItem("sturdy_machine"),
                    'P', Mods.Gendustry.getItem("power_module"));
        }

        // Genetic Replicator
        if (GendustryConfigHolder.GeneticReplicator) {
            ModHandler.addShapedRecipe(true, "gendustry_replicator",
                    Mods.Gendustry.getItem("replicator"),
                    "AZA", "POP", "AZA",
                    'A', new UnificationEntry(gear, Materials.Bronze),
                    'Z', Mods.Gendustry.getItem("genetics_processor"),
                    'P', Mods.Gendustry.getItem("power_module"),
                    'O', Mods.Forestry.getItem("sturdy_machine"));
        }
    }

    private static void industrialApiaryRecipes() {
        // TODO: Industrial Apiary レシピマップへのレシピ登録
        // GendustryRecipeMaps.INDUSTRIAL_APIARY_RECIPES.recipeBuilder()
        // .inputs(...)
        // .outputs(...)
        // .duration(...)
        // .EUt(...)
        // .buildAndRegister();
    }
}
