package com.github.gtexpert.gtbm.integration.gendustry.recipes;

import static com.github.gtexpert.gtbm.api.util.ModUtility.*;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

import net.minecraft.item.ItemStack;

import gregtech.api.recipes.ModHandler;

import com.github.gtexpert.gtbm.api.util.Mods;
import com.github.gtexpert.gtbm.integration.gendustry.GendustryConfigHolder;

public class GendustryCraftingRecipe {

    private static final String id = Mods.Names.GENDUSTRY;

    public static void init() {
        recipeRemoval();
    }

    public static void recipeRemoval() {
        Map<Supplier<Boolean>, String> recipes = new HashMap<>();
        recipes.put(() -> GendustryConfigHolder.industrialApiary, "industrial_apiary");

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
