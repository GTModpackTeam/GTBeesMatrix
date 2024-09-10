package com.github.gtexpert.gtbm.integration.forestry;

import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;

import com.github.gtexpert.gtbm.api.ModValues;
import com.github.gtexpert.gtbm.api.modules.TModule;
import com.github.gtexpert.gtbm.api.util.Mods;
import com.github.gtexpert.gtbm.integration.GTBMIntegrationSubmodule;
import com.github.gtexpert.gtbm.module.Modules;

@TModule(
         moduleID = Modules.MODULE_FORESTRY,
         containerID = ModValues.MODID,
         modDependencies = Mods.Names.FORESTRY,
         name = "GTBeesMatrix Forestry Integration",
         description = "Forestry Integration Module")
public class ForestryModule extends GTBMIntegrationSubmodule {

    @Override
    public void postInit(FMLPostInitializationEvent event) {}
}
