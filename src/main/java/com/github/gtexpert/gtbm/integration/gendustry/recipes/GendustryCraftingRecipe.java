package com.github.gtexpert.gtbm.integration.gendustry.recipes;

import static com.github.gtexpert.gtbm.api.util.ModUtility.*;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

import com.github.gtexpert.gtbm.integration.gendustry.GendustryConfigHolder;
import net.minecraft.item.ItemStack;

import gregtech.api.recipes.ModHandler;

import com.github.gtexpert.gtbm.api.util.Mods;

public class GendustryCraftingRecipe {

    private static final String id = Mods.Names.GENDUSTRY;

    public static void init() {
        recipeRemoval();
    }

    public static void recipeRemoval() {
        Map<Supplier<Boolean>, String> recipes = new HashMap<>();
        recipes.put(() -> GendustryConfigHolder.MutagenProducer, "mutagen_producer");
        recipes.put(() -> GendustryConfigHolder.Mutatron, "mutatron");
        recipes.put(() -> GendustryConfigHolder.IndustrialApiary, "industrial_apiary");
        recipes.put(() -> GendustryConfigHolder.GeneticImprinter, "genetic_imprinter");
        recipes.put(() -> GendustryConfigHolder.GeneticSampler, "sampler");
        recipes.put(() -> GendustryConfigHolder.AdvancedMutagen, "mutatron_advanced");
        recipes.put(() -> GendustryConfigHolder.ProteinLiquifier, "protein_liquifier");
        recipes.put(() -> GendustryConfigHolder.DNAExtractor, "dna_extractor");
        recipes.put(() -> GendustryConfigHolder.GeneticTransposer, "transposer");
        recipes.put(() -> GendustryConfigHolder.GeneticReplicator, "replicator");

        recipes.forEach((config, name) -> {
            if (config.get()) {
                removeRecipeWithTooltip(Mods.Gendustry.getItem(name));
            }
        });
    }

    private static void removeRecipeWithTooltip(ItemStack stack) {
        disabledItems.add(stack);
        ModHandler.removeRecipeByOutput(stack);
    }
}
