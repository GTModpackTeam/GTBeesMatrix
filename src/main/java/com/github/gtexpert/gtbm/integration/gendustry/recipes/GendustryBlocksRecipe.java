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
    }

    private static void machines() {
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
    }
}
