package com.github.gtexpert.gtbm.integration.binnies.genetics;

import net.minecraft.item.crafting.IRecipe;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;

import com.github.gtexpert.gtbm.api.ModValues;
import com.github.gtexpert.gtbm.api.modules.TModule;
import com.github.gtexpert.gtbm.api.util.Mods;
import com.github.gtexpert.gtbm.integration.GTBMIntegrationSubmodule;
import com.github.gtexpert.gtbm.integration.binnies.genetics.recipes.GeneticsItemsRecipe;
import com.github.gtexpert.gtbm.module.Modules;

@TModule(
           moduleID = Modules.MODULE_GENETICS,
           containerID = ModValues.MODID,
           modDependencies = { Mods.Names.FORESTRY, Mods.Names.GENETICS },
           name = "GTBeesMatrix Genetics(Binnie's Mods) Integration",
           description = "Genetics(Binnie's Mods) Integration Module")
public class GeneticsModule extends GTBMIntegrationSubmodule {

    @Override
    public void postInit(FMLPostInitializationEvent event) {
        GeneticsItemsRecipe.init();
    }

    @Override
    public void registerRecipesNormal(RegistryEvent.Register<IRecipe> event) {}
}
