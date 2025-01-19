package com.github.gtexpert.gtbm.integration.binnies.botany;

import net.minecraft.item.crafting.IRecipe;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;

import com.github.gtexpert.gtbm.api.ModValues;
import com.github.gtexpert.gtbm.api.modules.TModule;
import com.github.gtexpert.gtbm.api.util.Mods;
import com.github.gtexpert.gtbm.integration.GTBMIntegrationSubmodule;
import com.github.gtexpert.gtbm.integration.binnies.botany.recipes.BotanyItemsRecipe;
import com.github.gtexpert.gtbm.module.Modules;

@TModule(
           moduleID = Modules.MODULE_BOTANY,
           containerID = ModValues.MODID,
           modDependencies = { Mods.Names.FORESTRY, Mods.Names.BOTANY },
           name = "GTBeesMatrix Botany(Binnie's Mods) Integration",
           description = "Botany(Binnie's Mods) Integration Module")
public class BotanyModule extends GTBMIntegrationSubmodule {

    @Override
    public void postInit(FMLPostInitializationEvent event) {
        BotanyItemsRecipe.init();
    }

    @Override
    public void registerRecipesNormal(RegistryEvent.Register<IRecipe> event) {}
}
