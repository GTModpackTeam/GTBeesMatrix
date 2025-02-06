package com.github.gtexpert.gtbm.integration.binnies;

import net.minecraft.item.crafting.IRecipe;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;

import com.github.gtexpert.gtbm.api.ModValues;
import com.github.gtexpert.gtbm.api.modules.TModule;
import com.github.gtexpert.gtbm.api.util.Mods;
import com.github.gtexpert.gtbm.integration.GTBMIntegrationSubmodule;
import com.github.gtexpert.gtbm.integration.binnies.recipes.BinniesItemsRecipe;
import com.github.gtexpert.gtbm.module.Modules;

@TModule(
         moduleID = Modules.MODULE_BINNIES,
         containerID = ModValues.MODID,
         modDependencies = { Mods.Names.FORESTRY, Mods.Names.GENETICS, Mods.Names.BOTANY,
                 Mods.Names.EXTRA_BEES, Mods.Names.EXTRA_TREES },
         name = "GTBeesMatrix Binnie's Mods Integration",
         description = "Binnie's Mods Integration Module")
public class BinniesModule extends GTBMIntegrationSubmodule {

    @Override
    public void postInit(FMLPostInitializationEvent event) {
        BinniesItemsRecipe.init();
    }

    @Override
    public void registerRecipesNormal(RegistryEvent.Register<IRecipe> event) {}
}
