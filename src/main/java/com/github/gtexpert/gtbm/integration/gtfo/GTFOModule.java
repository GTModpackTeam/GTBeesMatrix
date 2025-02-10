package com.github.gtexpert.gtbm.integration.gtfo;

import net.minecraft.item.crafting.IRecipe;
import net.minecraftforge.event.RegistryEvent;

import com.github.gtexpert.gtbm.api.ModValues;
import com.github.gtexpert.gtbm.api.modules.TModule;
import com.github.gtexpert.gtbm.api.util.Mods;
import com.github.gtexpert.gtbm.integration.GTBMIntegrationSubmodule;
import com.github.gtexpert.gtbm.integration.gtfo.recipes.GTFOOverrideRecipe;
import com.github.gtexpert.gtbm.module.Modules;

@TModule(
         moduleID = Modules.MODULE_GTFO,
         containerID = ModValues.MODID,
         modDependencies = Mods.Names.GREGTECH_FOOD_OPTION,
         name = "GTBeesMatrix Gregtech Food Option Integration",
         description = "Gregtech Food Option Integration Module")
public class GTFOModule extends GTBMIntegrationSubmodule {

    @Override
    public void registerRecipesLowest(RegistryEvent.Register<IRecipe> event) {
        GTFOOverrideRecipe.init();
    }
}
