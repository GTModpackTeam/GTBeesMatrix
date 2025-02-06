package com.github.gtexpert.gtbm.integration.forestry.recipes;

import static com.github.gtexpert.gtbm.api.util.ModUtility.*;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

import com.github.gtexpert.gtbm.api.util.Mods;
import com.github.gtexpert.gtbm.integration.forestry.ForestryConfigHolder;

public class FFMCraftingRecipe {

    private static String id = Mods.Names.FORESTRY;

    public static void init() {
        recipeRemoval();
    }

    public static void recipeRemoval() {
        Map<Supplier<Boolean>, String> recipes = new HashMap<>();
        recipes.put(() -> ForestryConfigHolder.still, "still");
        recipes.put(() -> ForestryConfigHolder.fabricator, "fabricator");
        recipes.put(() -> ForestryConfigHolder.centrifuge, "centrifuge");
        recipes.put(() -> ForestryConfigHolder.bottler, "bottler");
        recipes.put(() -> ForestryConfigHolder.fermenter, "fermenter");
        recipes.put(() -> ForestryConfigHolder.rainmaker, "rainmaker");
        recipes.put(() -> ForestryConfigHolder.carpenter, "carpenter");
        recipes.put(() -> ForestryConfigHolder.moistener, "moistener");
        recipes.put(() -> ForestryConfigHolder.raintank, "raintank");
        recipes.put(() -> ForestryConfigHolder.squeezer, "squeezer");
        recipes.put(() -> ForestryConfigHolder.fermenter, "fermenter");

        recipes.forEach((config, name) -> {
            if (config.get()) {
                removeRecipeWithTooltip(getModItem(id, name));
            }
        });
    }
}
