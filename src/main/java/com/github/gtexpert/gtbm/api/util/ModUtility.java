package com.github.gtexpert.gtbm.api.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.OreDictionary;

import org.jetbrains.annotations.NotNull;

import gregtech.api.GTValues;
import gregtech.api.items.metaitem.MetaItem;
import gregtech.api.unification.OreDictUnifier;
import gregtech.api.unification.material.Material;
import gregtech.api.unification.stack.ItemMaterialInfo;
import gregtech.api.unification.stack.MaterialStack;
import gregtech.common.items.MetaItems;

import com.github.gtexpert.gtbm.api.ModValues;

public class ModUtility {

    public static List<ItemStack> disabledItems = new ArrayList<>();

    public static @NotNull ItemStack getModItem(String modID, String itemName) {
        return GameRegistry.makeItemStack(modID + ":" + itemName, 0, 1, null);
    }

    public static @NotNull ItemStack getModItem(String modID, String itemName, int amount) {
        return GameRegistry.makeItemStack(modID + ":" + itemName, 0, amount, null);
    }

    public static @NotNull ItemStack getModItem(String modID, String itemName, int amount, int meta) {
        return GameRegistry.makeItemStack(modID + ":" + itemName, meta, amount, null);
    }

    public static @NotNull ItemStack getModItem(String modID, String itemName, int amount, int meta,
                                                NBTTagCompound nbt) {
        return GameRegistry.makeItemStack(modID + ":" + itemName, meta, amount, nbt != null ? nbt.toString() : null);
    }

    public static @NotNull FluidStack getModFluid(String fluidName) {
        return Objects.requireNonNull(FluidRegistry.getFluidStack(fluidName, 1000));
    }

    public static @NotNull FluidStack getModFluid(String fluidName, int amount) {
        return Objects.requireNonNull(FluidRegistry.getFluidStack(fluidName, amount));
    }

    public static @NotNull ResourceLocation gtbm(String path) {
        return new ResourceLocation(ModValues.MODID, path);
    }

    public static void registerOre(String dictName, ItemStack... itemStacks) {
        for (ItemStack stack : itemStacks) {
            OreDictionary.registerOre(dictName, stack);
        }
    }

    public static String generateRandomString(int length) {
        Random random = new Random();
        StringBuilder stringBuilder = new StringBuilder(length);

        for (int i = 0; i < length; i++) {
            stringBuilder.append(random.nextInt(10));
        }

        return stringBuilder.toString();
    }

    public static MetaItem<?>.MetaValueItem motor(int voltage) {
        return switch (voltage) {
            case 1 -> MetaItems.ELECTRIC_MOTOR_LV;
            case 2 -> MetaItems.ELECTRIC_MOTOR_MV;
            case 3 -> MetaItems.ELECTRIC_MOTOR_HV;
            case 4 -> MetaItems.ELECTRIC_MOTOR_EV;
            case 5 -> MetaItems.ELECTRIC_MOTOR_IV;
            case 6 -> MetaItems.ELECTRIC_MOTOR_LuV;
            case 7 -> MetaItems.ELECTRIC_MOTOR_ZPM;
            case 8 -> MetaItems.ELECTRIC_MOTOR_UV;
            case 9 -> MetaItems.ELECTRIC_MOTOR_UHV;
            case 10 -> MetaItems.ELECTRIC_MOTOR_UEV;
            case 11 -> MetaItems.ELECTRIC_MOTOR_UIV;
            case 12 -> MetaItems.ELECTRIC_MOTOR_UXV;
            case 13 -> MetaItems.ELECTRIC_MOTOR_OpV;
            default -> throw new IllegalStateException("Out of Voltage: " + GTValues.VN[voltage]);
        };
    }

    public static MetaItem<?>.MetaValueItem pump(int voltage) {
        return switch (voltage) {
            case 1 -> MetaItems.ELECTRIC_PUMP_LV;
            case 2 -> MetaItems.ELECTRIC_PUMP_MV;
            case 3 -> MetaItems.ELECTRIC_PUMP_HV;
            case 4 -> MetaItems.ELECTRIC_PUMP_EV;
            case 5 -> MetaItems.ELECTRIC_PUMP_IV;
            case 6 -> MetaItems.ELECTRIC_PUMP_LuV;
            case 7 -> MetaItems.ELECTRIC_PUMP_ZPM;
            case 8 -> MetaItems.ELECTRIC_PUMP_UV;
            case 9 -> MetaItems.ELECTRIC_PUMP_UHV;
            case 10 -> MetaItems.ELECTRIC_PUMP_UEV;
            case 11 -> MetaItems.ELECTRIC_PUMP_UIV;
            case 12 -> MetaItems.ELECTRIC_PUMP_UXV;
            case 13 -> MetaItems.ELECTRIC_PUMP_OpV;
            default -> throw new IllegalStateException("Out of Voltage: " + GTValues.VN[voltage]);
        };
    }

    public static MetaItem<?>.MetaValueItem regulator(int voltage) {
        return switch (voltage) {
            case 1 -> MetaItems.FLUID_REGULATOR_LV;
            case 2 -> MetaItems.FLUID_REGULATOR_MV;
            case 3 -> MetaItems.FLUID_REGULATOR_HV;
            case 4 -> MetaItems.FLUID_REGULATOR_EV;
            case 5 -> MetaItems.FLUID_REGULATOR_IV;
            case 6 -> MetaItems.FLUID_REGULATOR_LUV;
            case 7 -> MetaItems.FLUID_REGULATOR_ZPM;
            case 8 -> MetaItems.FLUID_REGULATOR_UV;
            default -> throw new IllegalStateException("Out of Voltage: " + GTValues.VN[voltage]);
        };
    }

    public static MetaItem<?>.MetaValueItem piston(int voltage) {
        return switch (voltage) {
            case 1 -> MetaItems.ELECTRIC_PISTON_LV;
            case 2 -> MetaItems.ELECTRIC_PISTON_MV;
            case 3 -> MetaItems.ELECTRIC_PISTON_HV;
            case 4 -> MetaItems.ELECTRIC_PISTON_EV;
            case 5 -> MetaItems.ELECTRIC_PISTON_IV;
            case 6 -> MetaItems.ELECTRIC_PISTON_LUV;
            case 7 -> MetaItems.ELECTRIC_PISTON_ZPM;
            case 8 -> MetaItems.ELECTRIC_PISTON_UV;
            case 9 -> MetaItems.ELECTRIC_PISTON_UHV;
            case 10 -> MetaItems.ELECTRIC_PISTON_UEV;
            case 11 -> MetaItems.ELECTRIC_PISTON_UIV;
            case 12 -> MetaItems.ELECTRIC_PISTON_UXV;
            case 13 -> MetaItems.ELECTRIC_PISTON_OpV;
            default -> throw new IllegalStateException("Out of Voltage: " + GTValues.VN[voltage]);
        };
    }

    public static MetaItem<?>.MetaValueItem robotArm(int voltage) {
        return switch (voltage) {
            case 1 -> MetaItems.ROBOT_ARM_LV;
            case 2 -> MetaItems.ROBOT_ARM_MV;
            case 3 -> MetaItems.ROBOT_ARM_HV;
            case 4 -> MetaItems.ROBOT_ARM_EV;
            case 5 -> MetaItems.ROBOT_ARM_IV;
            case 6 -> MetaItems.ROBOT_ARM_LuV;
            case 7 -> MetaItems.ROBOT_ARM_ZPM;
            case 8 -> MetaItems.ROBOT_ARM_UV;
            case 9 -> MetaItems.ROBOT_ARM_UHV;
            case 10 -> MetaItems.ROBOT_ARM_UEV;
            case 11 -> MetaItems.ROBOT_ARM_UIV;
            case 12 -> MetaItems.ROBOT_ARM_UXV;
            case 13 -> MetaItems.ROBOT_ARM_OpV;
            default -> throw new IllegalStateException("Out of Voltage: " + GTValues.VN[voltage]);
        };
    }

    public static MetaItem<?>.MetaValueItem fieldGenerator(int voltage) {
        return switch (voltage) {
            case 1 -> MetaItems.FIELD_GENERATOR_LV;
            case 2 -> MetaItems.FIELD_GENERATOR_MV;
            case 3 -> MetaItems.FIELD_GENERATOR_HV;
            case 4 -> MetaItems.FIELD_GENERATOR_EV;
            case 5 -> MetaItems.FIELD_GENERATOR_IV;
            case 6 -> MetaItems.FIELD_GENERATOR_LuV;
            case 7 -> MetaItems.FIELD_GENERATOR_ZPM;
            case 8 -> MetaItems.FIELD_GENERATOR_UV;
            case 9 -> MetaItems.FIELD_GENERATOR_UHV;
            case 10 -> MetaItems.FIELD_GENERATOR_UEV;
            case 11 -> MetaItems.FIELD_GENERATOR_UIV;
            case 12 -> MetaItems.FIELD_GENERATOR_UXV;
            case 13 -> MetaItems.FIELD_GENERATOR_OpV;
            default -> throw new IllegalStateException("Out of Voltage: " + GTValues.VN[voltage]);
        };
    }

    public static MetaItem<?>.MetaValueItem conveyorModule(int voltage) {
        return switch (voltage) {
            case 1 -> MetaItems.CONVEYOR_MODULE_LV;
            case 2 -> MetaItems.CONVEYOR_MODULE_MV;
            case 3 -> MetaItems.CONVEYOR_MODULE_HV;
            case 4 -> MetaItems.CONVEYOR_MODULE_EV;
            case 5 -> MetaItems.CONVEYOR_MODULE_IV;
            case 6 -> MetaItems.CONVEYOR_MODULE_LuV;
            case 7 -> MetaItems.CONVEYOR_MODULE_ZPM;
            case 8 -> MetaItems.CONVEYOR_MODULE_UV;
            case 9 -> MetaItems.CONVEYOR_MODULE_UHV;
            case 10 -> MetaItems.CONVEYOR_MODULE_UEV;
            case 11 -> MetaItems.CONVEYOR_MODULE_UIV;
            case 12 -> MetaItems.CONVEYOR_MODULE_UXV;
            case 13 -> MetaItems.CONVEYOR_MODULE_OpV;
            default -> throw new IllegalStateException("Out of Voltage: " + GTValues.VN[voltage]);
        };
    }

    public static MetaItem<?>.MetaValueItem emitter(int voltage) {
        return switch (voltage) {
            case 1 -> MetaItems.EMITTER_LV;
            case 2 -> MetaItems.EMITTER_MV;
            case 3 -> MetaItems.EMITTER_HV;
            case 4 -> MetaItems.EMITTER_EV;
            case 5 -> MetaItems.EMITTER_IV;
            case 6 -> MetaItems.EMITTER_LuV;
            case 7 -> MetaItems.EMITTER_ZPM;
            case 8 -> MetaItems.EMITTER_UV;
            case 9 -> MetaItems.EMITTER_UHV;
            case 10 -> MetaItems.EMITTER_UEV;
            case 11 -> MetaItems.EMITTER_UIV;
            case 12 -> MetaItems.EMITTER_UXV;
            case 13 -> MetaItems.EMITTER_OpV;
            default -> throw new IllegalStateException("Out of Voltage: " + GTValues.VN[voltage]);
        };
    }

    public static MetaItem<?>.MetaValueItem sensor(int voltage) {
        return switch (voltage) {
            case 1 -> MetaItems.SENSOR_LV;
            case 2 -> MetaItems.SENSOR_MV;
            case 3 -> MetaItems.SENSOR_HV;
            case 4 -> MetaItems.SENSOR_EV;
            case 5 -> MetaItems.SENSOR_IV;
            case 6 -> MetaItems.SENSOR_LuV;
            case 7 -> MetaItems.SENSOR_ZPM;
            case 8 -> MetaItems.SENSOR_UV;
            case 9 -> MetaItems.SENSOR_UHV;
            case 10 -> MetaItems.SENSOR_UEV;
            case 11 -> MetaItems.SENSOR_UIV;
            case 12 -> MetaItems.SENSOR_UXV;
            case 13 -> MetaItems.SENSOR_OpV;
            default -> throw new IllegalStateException("Out of Voltage: " + GTValues.VN[voltage]);
        };
    }

    public static MetaItem<?>.MetaValueItem batteryHull(int voltage) {
        return switch (voltage) {
            case 1 -> MetaItems.BATTERY_HULL_LV;
            case 2 -> MetaItems.BATTERY_HULL_MV;
            case 3 -> MetaItems.BATTERY_HULL_HV;
            case 4 -> MetaItems.BATTERY_HULL_SMALL_VANADIUM;
            case 5 -> MetaItems.BATTERY_HULL_MEDIUM_VANADIUM;
            case 6 -> MetaItems.BATTERY_HULL_LARGE_VANADIUM;
            case 7 -> MetaItems.BATTERY_HULL_MEDIUM_NAQUADRIA;
            case 8 -> MetaItems.BATTERY_HULL_LARGE_NAQUADRIA;
            default -> throw new IllegalStateException("Out of Voltage: " + GTValues.VN[voltage]);
        };
    }

    public static MetaItem<?>.MetaValueItem circuitBoard(int voltage) {
        return switch (voltage) {
            case 1 -> MetaItems.BASIC_CIRCUIT_BOARD;
            case 2 -> MetaItems.GOOD_CIRCUIT_BOARD;
            case 3 -> MetaItems.PLASTIC_CIRCUIT_BOARD;
            case 4 -> MetaItems.ADVANCED_CIRCUIT_BOARD;
            case 5 -> MetaItems.EXTREME_CIRCUIT_BOARD;
            case 6 -> MetaItems.ELITE_CIRCUIT_BOARD;
            case 7 -> MetaItems.WETWARE_CIRCUIT_BOARD;
            default -> throw new IllegalStateException("Out of Voltage: " + GTValues.VN[voltage]);
        };
    }

    public static String oreDictionaryCircuit(int voltage) {
        return switch (voltage) {
            case 0 -> "circuitUlv";
            case 1 -> "circuitLv";
            case 2 -> "circuitMv";
            case 3 -> "circuitHv";
            case 4 -> "circuitEv";
            case 5 -> "circuitIv";
            case 6 -> "circuitLuv";
            case 7 -> "circuitZpm";
            case 8 -> "circuitUv";
            case 9 -> "circuitUhv";
            case 10 -> "circuitUev";
            case 11 -> "circuitUiv";
            case 12 -> "circuitUxv";
            case 13 -> "circuitOpv";
            default -> throw new IllegalStateException("Out of Voltage: " + GTValues.VN[voltage]);
        };
    }

    public static void registerOre(ItemStack itemStack, Material material, long amount) {
        registerOre(itemStack, new MaterialStack(material, amount));
    }

    public static void registerOre(ItemStack itemStack, MaterialStack... materialStacks) {
        registerOre(itemStack, new ItemMaterialInfo(materialStacks));
    }

    public static void registerOre(ItemStack itemStack, ItemMaterialInfo materialInfo) {
        OreDictUnifier.registerOre(itemStack, materialInfo);
    }
}
