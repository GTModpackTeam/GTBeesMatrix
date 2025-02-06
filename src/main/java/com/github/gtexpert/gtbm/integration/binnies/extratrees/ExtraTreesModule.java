package com.github.gtexpert.gtbm.integration.binnies.extratrees;

import net.minecraft.item.crafting.IRecipe;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;

import com.github.gtexpert.gtbm.api.ModValues;
import com.github.gtexpert.gtbm.api.modules.TModule;
import com.github.gtexpert.gtbm.api.util.Mods;
import com.github.gtexpert.gtbm.integration.GTBMIntegrationSubmodule;
import com.github.gtexpert.gtbm.integration.binnies.extratrees.recipes.*;
import com.github.gtexpert.gtbm.module.Modules;

@TModule(
         moduleID = Modules.MODULE_EXTREES,
         containerID = ModValues.MODID,
         modDependencies = { Mods.Names.FORESTRY, Mods.Names.EXTRA_TREES },
         name = "GTBeesMatrix Extra Trees(Binnie's Mods) Integration",
         description = "Extra Trees(Binnie's Mods) Integration Module")
public class ExtraTreesModule extends GTBMIntegrationSubmodule {

    @Override
    public void postInit(FMLPostInitializationEvent event) {
        ExtraTreesItemsRecipe.init();
    }

    @Override
    public void registerRecipesNormal(RegistryEvent.Register<IRecipe> event) {}
}
