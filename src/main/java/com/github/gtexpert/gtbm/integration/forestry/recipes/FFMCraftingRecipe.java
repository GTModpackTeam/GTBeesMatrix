package com.github.gtexpert.gtbm.integration.forestry.recipes;

import static com.github.gtexpert.gtbm.api.util.ModUtility.*;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

import net.minecraft.item.ItemStack;

import gregtech.api.recipes.ModHandler;

import com.github.gtexpert.gtbm.api.util.Mods;
import com.github.gtexpert.gtbm.integration.forestry.ForestryConfigHolder;

public class FFMCraftingRecipe {

    private static String id = Mods.Names.FORESTRY;

    public static void init() {
        recipeRemoval();
    }

    public static void recipeRemoval() {
        Map<Supplier<Boolean>, String> recipes = new HashMap<>();
        recipes.put(() -> ForestryConfigHolder.Still, "still");
        recipes.put(() -> ForestryConfigHolder.Fabricator, "fabricator");
        recipes.put(() -> ForestryConfigHolder.Centrifuge, "centrifuge");
        recipes.put(() -> ForestryConfigHolder.Bottler, "bottler");
        recipes.put(() -> ForestryConfigHolder.Fermenter, "fermenter");
        recipes.put(() -> ForestryConfigHolder.Rainmaker, "Rainmaker");
        recipes.put(() -> ForestryConfigHolder.Carpenter, "carpenter");
        recipes.put(() -> ForestryConfigHolder.Moistener, "moistener");
        recipes.put(() -> ForestryConfigHolder.Raintank, "raintank");
        recipes.put(() -> ForestryConfigHolder.Squeezer, "squeezer");
        recipes.put(() -> ForestryConfigHolder.Fermenter, "fermenter");

        recipes.forEach((config, name) -> {
            if (config.get()) {
                removeRecipeWithTooltip(getModItem(id, name));
            }
        });
    }

    private static void removeRecipeWithTooltip(ItemStack stack) {
        disabledItems.add(stack);
        ModHandler.removeRecipeByOutput(stack);
    }
}
