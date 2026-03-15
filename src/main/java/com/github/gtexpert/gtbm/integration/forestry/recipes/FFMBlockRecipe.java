package com.github.gtexpert.gtbm.integration.forestry.recipes;

import static com.github.gtexpert.gtbm.integration.forestry.util.ForestryRecipeHelper.feToEu;
import static com.github.gtexpert.gtbm.integration.forestry.util.ForestryRecipeHelper.timeCarpenter;
import static forestry.farming.ModuleFarming.getBlocks;
import static gregtech.api.unification.ore.OrePrefix.*;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import gregtech.api.recipes.ModHandler;
import gregtech.api.recipes.RecipeMaps;
import gregtech.api.unification.material.MarkerMaterials;
import gregtech.api.unification.material.Materials;
import gregtech.api.util.GTUtility;
import gregtech.common.ConfigHolder;
import gregtech.common.items.MetaItems;

import com.github.gtexpert.gtbm.api.util.Mods;
import com.github.gtexpert.gtbm.integration.forestry.ForestryConfigHolder;

import forestry.core.ModuleCore;
import forestry.core.items.EnumElectronTube;
import forestry.farming.blocks.BlockRegistryFarming;
import forestry.farming.blocks.EnumFarmBlockType;
import forestry.farming.models.EnumFarmBlockTexture;

public class FFMBlockRecipe {

    public static void init() {
        if (Mods.ForestryCore.isModLoaded()) {
            blockCore();
        }
        if (Mods.ForestryCharcoal.isModLoaded()) {
            blockCharcoal();
        }
        if (Mods.ForestryFarming.isModLoaded()) {
            farm();
        }
    }

    public static void blockCore() {
        // Ash Brick
        if (ConfigHolder.recipes.harderBrickRecipes) {
            ModHandler.removeRecipeByName(Mods.Forestry.getResource("ash_brick"));
            RecipeMaps.ALLOY_SMELTER_RECIPES.recipeBuilder()
                    .input(dust, Materials.Ash, 4)
                    .inputs(new ItemStack(Items.BRICK, 4))
                    .outputs(Mods.Forestry.getItem("ash_brick"))
                    .duration(400).EUt(2).buildAndRegister();
            RecipeMaps.ALLOY_SMELTER_RECIPES.recipeBuilder()
                    .input(dust, Materials.Ash, 4)
                    .inputs(new ItemStack(Blocks.BRICK_BLOCK))
                    .outputs(Mods.Forestry.getItem("ash_brick"))
                    .duration(100).EUt(2).buildAndRegister();
        }
    }

    public static void blockCharcoal() {
        // Loam
        ModHandler.removeRecipeByName(Mods.Forestry.getResource("loam"));
        RecipeMaps.MIXER_RECIPES.recipeBuilder()
                .inputs(new ItemStack(Items.CLAY_BALL, 4))
                .input("sand", 2)
                .inputs(Mods.Forestry.getItem("fertilizer_bio", 2))
                .outputs(Mods.Forestry.getItem("loam", 4))
                .duration(200).EUt(16).buildAndRegister();
    }

    public static void farm() {
        if (!ForestryConfigHolder.farmBlock) return;

        BlockRegistryFarming blocks = getBlocks();

        ItemStack basic = blocks.farm.get(EnumFarmBlockType.PLAIN, 1);
        ItemStack gearbox = blocks.farm.get(EnumFarmBlockType.GEARBOX, 1);
        ItemStack hatch = blocks.farm.get(EnumFarmBlockType.HATCH, 1);
        ItemStack valve = blocks.farm.get(EnumFarmBlockType.VALVE, 1);
        ItemStack control = blocks.farm.get(EnumFarmBlockType.CONTROL, 1);

        for (EnumFarmBlockTexture block : EnumFarmBlockTexture.values()) {
            NBTTagCompound compound = new NBTTagCompound();
            block.saveToCompound(compound);

            basic.setTagCompound(compound);
            gearbox.setTagCompound(compound);
            hatch.setTagCompound(compound);
            valve.setTagCompound(compound);
            control.setTagCompound(compound);

            // == REMOVE ==
            ModHandler.removeRecipeByOutput(basic);
            ModHandler.removeRecipeByOutput(gearbox);
            ModHandler.removeRecipeByOutput(hatch);
            ModHandler.removeRecipeByOutput(valve);
            ModHandler.removeRecipeByOutput(control);

            // == ADD ==
            // Farm Block
            RecipeMaps.ASSEMBLER_RECIPES.recipeBuilder()
                    .inputs(block.getBase())
                    .input(screw, Materials.Steel, 4)
                    .input(plate, Materials.Copper, 4)
                    .inputs(ModuleCore.getItems().tubes.get(EnumElectronTube.APATITE, 1))
                    .circuitMeta(10)
                    .fluidInputs(Materials.Creosote.getFluid(500))
                    .outputs(GTUtility.copy(4, basic))
                    .EUt(20)
                    .duration(timeCarpenter(20 * feToEu))
                    .buildAndRegister();

            // Farm Control
            RecipeMaps.ASSEMBLER_RECIPES.recipeBuilder()
                    .inputs(GTUtility.copy(2, basic))
                    .input(circuit, MarkerMaterials.Tier.LV, 2)
                    .input(MetaItems.ELECTRIC_MOTOR_LV)
                    .input(cableGtSingle, Materials.Tin)
                    .input(gear, Materials.Steel)
                    .inputs(ModuleCore.getItems().tubes.get(EnumElectronTube.GOLD, 4))
                    .circuitMeta(11)
                    .fluidInputs(Materials.Creosote.getFluid(1000))
                    .outputs(GTUtility.copy(2, control))
                    .EUt(30)
                    .duration(timeCarpenter(2 * 30 * feToEu))
                    .buildAndRegister();

            // Gear Box
            RecipeMaps.ASSEMBLER_RECIPES.recipeBuilder()
                    .inputs(GTUtility.copy(2, basic))
                    .input(MetaItems.ELECTRIC_MOTOR_LV)
                    .input(gear, Materials.Steel, 4)
                    .inputs(ModuleCore.getItems().tubes.get(EnumElectronTube.BRONZE, 4))
                    .circuitMeta(12)
                    .fluidInputs(Materials.Creosote.getFluid(1000))
                    .outputs(GTUtility.copy(2, gearbox))
                    .EUt(30)
                    .duration(timeCarpenter(2 * 30 * feToEu))
                    .buildAndRegister();

            // Valve
            RecipeMaps.ASSEMBLER_RECIPES.recipeBuilder()
                    .inputs(GTUtility.copy(2, basic))
                    .input(MetaItems.ELECTRIC_MOTOR_LV)
                    .input(MetaItems.ELECTRIC_PUMP_LV)
                    .input(gear, Materials.Steel, 1)
                    .input(ring, Materials.Rubber)
                    .inputs(ModuleCore.getItems().tubes.get(EnumElectronTube.LAPIS, 4))
                    .circuitMeta(13)
                    .fluidInputs(Materials.Creosote.getFluid(1000))
                    .outputs(GTUtility.copy(2, valve))
                    .EUt(30)
                    .duration(timeCarpenter(2 * 30 * feToEu))
                    .buildAndRegister();

            // Hatch
            RecipeMaps.ASSEMBLER_RECIPES.recipeBuilder()
                    .inputs(GTUtility.copy(2, basic))
                    .input(MetaItems.ELECTRIC_MOTOR_LV)
                    .input(MetaItems.CONVEYOR_MODULE_LV, 2)
                    .input(gear, Materials.Steel)
                    .input(Blocks.HOPPER)
                    .inputs(ModuleCore.getItems().tubes.get(EnumElectronTube.TIN, 4))
                    .circuitMeta(14)
                    .fluidInputs(Materials.Creosote.getFluid(1000))
                    .outputs(GTUtility.copy(2, hatch))
                    .EUt(30)
                    .duration(timeCarpenter(2 * 30 * feToEu))
                    .buildAndRegister();
        }
    }
}
