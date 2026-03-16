package com.github.gtexpert.gtbm.integration.forestry.util;

import java.util.function.BiFunction;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;

import com.github.gtexpert.gtbm.api.util.Mods;

import forestry.api.circuits.ChipsetManager;
import forestry.api.circuits.ICircuitLayout;
import forestry.api.core.ForestryAPI;
import forestry.api.farming.IFarmLogic;
import forestry.api.farming.IFarmProperties;
import forestry.core.items.EnumElectronTube;
import forestry.farming.circuits.CircuitFarmLogic;

public class ForestryFarmHelper {

    public static IFarmProperties registerFarmType(String farmId,
                                                   BiFunction<IFarmProperties, Boolean, IFarmLogic> logicFactory,
                                                   EnumElectronTube tube) {
        IFarmProperties farm = ForestryAPI.farmRegistry.registerLogic(farmId, logicFactory);
        farm.registerSoil(new ItemStack(Blocks.DIRT),
                Block.getBlockFromItem(Mods.Forestry.getItem("humus").getItem()).getDefaultState());

        CircuitFarmLogic managed = new CircuitFarmLogic("managed." + farmId, farm, false);
        CircuitFarmLogic manual = new CircuitFarmLogic("manual." + farmId, farm, true);

        ICircuitLayout layoutManaged = ChipsetManager.circuitRegistry.getLayout("forestry.farms.managed");
        ICircuitLayout layoutManual = ChipsetManager.circuitRegistry.getLayout("forestry.farms.manual");
        ItemStack tubeStack = Mods.Forestry.getItem("thermionic_tubes", 1, tube.ordinal());

        ChipsetManager.solderManager.addRecipe(layoutManaged, tubeStack, managed);
        ChipsetManager.solderManager.addRecipe(layoutManual, tubeStack, manual);

        return farm;
    }
}
