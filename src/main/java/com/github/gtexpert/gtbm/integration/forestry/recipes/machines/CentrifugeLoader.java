package com.github.gtexpert.gtbm.integration.forestry.recipes.machines;

import static gregtech.api.unification.ore.OrePrefix.*;

import net.minecraft.item.ItemStack;

import gregtech.api.recipes.RecipeBuilder;
import gregtech.api.recipes.RecipeMaps;

import com.github.gtexpert.gtbm.api.util.Mods;

public class CentrifugeLoader {

    public static void init() {
        if (!Mods.ForestryApiculture.isModLoaded()) return;

        ItemStack wax = Mods.Forestry.getItem("beeswax");
        ItemStack drop = Mods.Forestry.getItem("honey_drop");
        // GenDustry Section
        if (Mods.Gendustry.isModLoaded()) {
            for (int i = 10; i < 26; i++) {
                registerCombRecipe(Mods.Gendustry.getItem("honey_comb", 1, i),
                        new ItemStack[] { Mods.Gendustry.getItem("honey_drop", 1, i), wax, drop },
                        new int[] { 10000, 5000, 3000 });
            }
        }
    }

    public static void registerCombRecipe(ItemStack comb, ItemStack[] output, int[] chance) {
        RecipeBuilder<?> builder = RecipeMaps.CENTRIFUGE_RECIPES.recipeBuilder()
                .inputs(comb)
                .EUt(5)
                .duration(128);

        int outputs = 0;
        for (int i = 0; i < output.length; i++) {
            if (output[i] == null || output[i] == ItemStack.EMPTY) continue;
            if (outputs < RecipeMaps.CENTRIFUGE_RECIPES.getMaxOutputs()) {
                if (chance[i] >= 10000) {
                    builder.outputs(output[i]);
                } else {
                    builder.chancedOutput(output[i], chance[i], 0);
                }
                outputs++;
            }
        }

        builder.buildAndRegister();
    }
}
