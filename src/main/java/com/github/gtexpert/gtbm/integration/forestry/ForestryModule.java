package com.github.gtexpert.gtbm.integration.forestry;

import net.minecraft.item.crafting.IRecipe;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;

import com.github.gtexpert.gtbm.api.ModValues;
import com.github.gtexpert.gtbm.api.modules.TModule;
import com.github.gtexpert.gtbm.api.util.Mods;
import com.github.gtexpert.gtbm.integration.GTBMIntegrationSubmodule;
import com.github.gtexpert.gtbm.integration.forestry.loaders.FFMOreDictionaryLoader;
import com.github.gtexpert.gtbm.integration.forestry.recipes.*;
import com.github.gtexpert.gtbm.integration.forestry.recipes.machines.*;
import com.github.gtexpert.gtbm.module.Modules;

@TModule(
           moduleID = Modules.MODULE_FORESTRY,
           containerID = ModValues.MODID,
           modDependencies = Mods.Names.FORESTRY,
           name = "GTBeesMatrix Forestry For Minecraft Integration",
           description = "Forestry For Minecraft Integration Module")
public class ForestryModule extends GTBMIntegrationSubmodule {

    @Override
    public void postInit(FMLPostInitializationEvent event) {
        FFMBlockRecipe.init();
        FFMItemRecipe.init();
        FFMMaterialsRecipe.init();
        FFMToolRecipe.init();

        CarpenterLoader.initBase();
        CarpenterLoader.initMode();
        CentrifugeLoader.init();
        FabricatorLoader.init();
    }

    @Override
    public void registerRecipesNormal(RegistryEvent.Register<IRecipe> event) {
        FFMOreDictionaryLoader.init();
    }
}
