package com.github.gtexpert.gtbm.integration.gendustry;

import static com.github.gtexpert.gtbm.api.util.ModUtility.getModItem;
import static com.github.gtexpert.gtbm.api.util.ModUtility.removeRecipeWithTooltip;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

import com.github.gtexpert.gtbm.api.util.Mods;

public class GendustryCraftingRecipe {

    private static final String id = Mods.Names.GENDUSTRY;

    public static void init() {
        recipeRemoval();
    }

    public static void recipeRemoval() {
        Map<Supplier<Boolean>, String> recipes = new HashMap<>();
        recipes.put(() -> GendustryConfigHolder.mutagen_producer, "mutagen_producer");
        recipes.put(() -> GendustryConfigHolder.mutatron, "mutatron");
        recipes.put(() -> GendustryConfigHolder.industrial_apiary, "industrial_apiary");
        recipes.put(() -> GendustryConfigHolder.imprinter, "imprinter");
        recipes.put(() -> GendustryConfigHolder.sampler, "sampler");
        recipes.put(() -> GendustryConfigHolder.advanced_mutagen, "mutatron_advanced");
        recipes.put(() -> GendustryConfigHolder.liquifier, "liquifier");
        recipes.put(() -> GendustryConfigHolder.extractor, "extractor");
        recipes.put(() -> GendustryConfigHolder.transposer, "transposer");
        recipes.put(() -> GendustryConfigHolder.replicator, "replicator");

        recipes.forEach((config, name) -> {
            if (config.get()) {
                removeRecipeWithTooltip(getModItem(id, name));
            }
        });
    }
}
