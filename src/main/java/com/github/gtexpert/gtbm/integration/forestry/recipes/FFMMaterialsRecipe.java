package com.github.gtexpert.gtbm.integration.forestry.recipes;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

import gregtech.api.recipes.GTRecipeHandler;
import gregtech.api.recipes.ModHandler;
import gregtech.api.recipes.RecipeMaps;
import gregtech.api.unification.OreDictUnifier;
import gregtech.api.unification.material.Materials;
import gregtech.api.unification.ore.OrePrefix;
import gregtech.common.items.MetaItems;

import com.github.gtexpert.gtbm.api.util.Mods;

public class FFMMaterialsRecipe {

    public static void init() {
        if (Mods.ForestryCore.isModLoaded()) {
            materialsCore();
        }
        if (Mods.ForestryCharcoal.isModLoaded()) {
            materialCharcoal();
        }
    }

    public static void materialsCore() {
        // Copper Ingot
        ModHandler.removeRecipeByName(Mods.Forestry.getResource("block_to_copper"));

        // Copper Block
        ModHandler.removeRecipeByName(Mods.Forestry.getResource("copper_block"));

        // Copper Gear
        ModHandler.removeRecipeByName(Mods.Forestry.getResource("gear_copper"));

        // Tin Ingot
        ModHandler.removeRecipeByName(Mods.Forestry.getResource("block_to_tin"));

        // Tin Block
        ModHandler.removeRecipeByName(Mods.Forestry.getResource("tin_block"));

        // Tin Gear
        ModHandler.removeRecipeByName(Mods.Forestry.getResource("gear_tin"));

        // Bronze Ingot
        ModHandler.removeRecipeByName(Mods.Forestry.getResource("bronze_ingot"));
        ModHandler.removeRecipeByName(Mods.Forestry.getResource("block_to_bronze"));

        // Bronze Block
        ModHandler.removeRecipeByName(Mods.Forestry.getResource("bronze_block"));

        // Bronze Gear
        ModHandler.removeRecipeByName(Mods.Forestry.getResource("gear_bronze"));

        // Apatite
        ModHandler.removeRecipeByName(Mods.Forestry.getResource("block_to_apatite"));

        // Apatite Block
        ModHandler.removeRecipeByName(Mods.Forestry.getResource("apatite_block"));

        // Remove GT Fertilizer -> Forestry fertilizer_compound bulk recipes
        GTRecipeHandler.removeRecipesByInputs(RecipeMaps.MIXER_RECIPES,
                new ItemStack[] {
                        MetaItems.FERTILIZER.getStackForm(8),
                        OreDictUnifier.get(OrePrefix.dust, Materials.Apatite)
                },
                new FluidStack[] { Materials.Water.getFluid(1000) });

        if (Mods.MagicBees.isModLoaded()) {
            GTRecipeHandler.removeRecipesByInputs(RecipeMaps.MIXER_RECIPES,
                    new ItemStack[] {
                            MetaItems.FERTILIZER.getStackForm(8),
                            Mods.MagicBees.getItem("resource", 1, 2)
                    },
                    new FluidStack[] { Materials.Water.getFluid(1000) });
        }
    }

    public static void materialCharcoal() {
        // Charcoal
        ModHandler.removeRecipeByName(Mods.Forestry.getResource("charcoal"));

        // Charcoal Block
        ModHandler.removeRecipeByName(Mods.Forestry.getResource("charcoal_block"));
    }
}
