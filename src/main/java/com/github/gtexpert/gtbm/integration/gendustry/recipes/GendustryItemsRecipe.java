package com.github.gtexpert.gtbm.integration.gendustry.recipes;

import static gregtech.api.unification.ore.OrePrefix.*;

import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;

import gregtech.api.recipes.ModHandler;
import gregtech.api.unification.material.Materials;
import gregtech.api.unification.stack.UnificationEntry;

import com.github.gtexpert.gtbm.api.util.Mods;

public class GendustryItemsRecipe {

    public static void init() {
        components();
    }

    private static void components() {
        // Mutagen Tank
        ModHandler.removeRecipeByOutput(Mods.Gendustry.getItem("mutagen_tank"));
        ModHandler.addShapedRecipe(true, "gendustry_mutagen_tank",
                Mods.Gendustry.getItem("mutagen_tank"),
                "TPT", "TPT", "TPT",
                'T', new UnificationEntry(ingot, Materials.Tin),
                'P', new ItemStack(Blocks.GLASS_PANE));

        // Bee Receptacle
        ModHandler.removeRecipeByOutput(Mods.Gendustry.getItem("bee_receptacle"));
        ModHandler.addShapedRecipe(true, "gendustry_bee_receptacle",
                Mods.Gendustry.getItem("bee_receptacle"),
                "BBB", "BPB", "RZR",
                'B', new UnificationEntry(ingot, Materials.Bronze),
                'P', new ItemStack(Blocks.GLASS_PANE),
                'R', new UnificationEntry(dust, Materials.Redstone),
                'Z', new ItemStack(Blocks.LIGHT_WEIGHTED_PRESSURE_PLATE));

        // Power Module
        ModHandler.removeRecipeByOutput(Mods.Gendustry.getItem("power_module"));
        ModHandler.addShapedRecipe(true, "gendustry_power_module",
                Mods.Gendustry.getItem("power_module"),
                "AGA", "SRS", "AGA",
                'A', new UnificationEntry(gear, Materials.Bronze),
                'G', new UnificationEntry(ingot, Materials.Gold),
                'S', new ItemStack(Blocks.PISTON),
                'R', new UnificationEntry(dust, Materials.Redstone));

        // Genetics Processor
        ModHandler.removeRecipeByOutput(Mods.Gendustry.getItem("genetics_processor"));
        ModHandler.addShapedRecipe(true, "gendustry_genetics_processor",
                Mods.Gendustry.getItem("genetics_processor"),
                "DQD", "RYR", "DQD",
                'D', new UnificationEntry(gem, Materials.Diamond),
                'Q', new UnificationEntry(gem, Materials.NetherQuartz),
                'R', new UnificationEntry(dust, Materials.Redstone),
                'Y', new UnificationEntry(gem, Materials.EnderPearl));

        // Environmental Processor
        ModHandler.removeRecipeByOutput(Mods.Gendustry.getItem("env_processor"));
        ModHandler.addShapedRecipe(true, "gendustry_env_processor",
                Mods.Gendustry.getItem("env_processor"),
                "DLD", "LGL", "DLD",
                'D', new UnificationEntry(gem, Materials.Diamond),
                'L', new UnificationEntry(gem, Materials.Lapis),
                'G', new UnificationEntry(ingot, Materials.Gold));

        // Upgrade Frame
        ModHandler.removeRecipeByOutput(Mods.Gendustry.getItem("upgrade_frame"));
        ModHandler.addShapedRecipe(true, "gendustry_upgrade_frame",
                Mods.Gendustry.getItem("upgrade_frame"),
                "TgT", "R R", "TgT",
                'T', new UnificationEntry(ingot, Materials.Tin),
                'g', new UnificationEntry(nugget, Materials.Gold),
                'R', new UnificationEntry(dust, Materials.Redstone));

        // Climate Module
        ModHandler.removeRecipeByOutput(Mods.Gendustry.getItem("climate_module"));
        ModHandler.addShapedRecipe(true, "gendustry_climate_module",
                Mods.Gendustry.getItem("climate_module"),
                "BRB", "BAB", "BRB",
                'B', new UnificationEntry(ingot, Materials.Bronze),
                'R', new UnificationEntry(dust, Materials.Redstone),
                'A', new UnificationEntry(gear, Materials.Bronze));

        // Labware (output x16)
        ModHandler.removeRecipeByOutput(Mods.Gendustry.getItem("labware"));
        ModHandler.addShapedRecipe(true, "gendustry_labware",
                Mods.Gendustry.getItem("labware", 16),
                "P P", "P P", " D ",
                'P', new ItemStack(Blocks.GLASS_PANE),
                'D', new UnificationEntry(gem, Materials.Diamond));
    }
}
