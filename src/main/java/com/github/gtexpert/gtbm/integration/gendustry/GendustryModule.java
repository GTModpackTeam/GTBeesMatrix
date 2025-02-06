package com.github.gtexpert.gtbm.integration.gendustry;

import com.github.gtexpert.gtbm.integration.gendustry.recipes.GendustryCraftingRecipe;
import net.minecraft.block.Block;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;

import com.github.gtexpert.gtbm.api.ModValues;
import com.github.gtexpert.gtbm.api.modules.TModule;
import com.github.gtexpert.gtbm.api.util.Mods;
import com.github.gtexpert.gtbm.integration.GTBMIntegrationSubmodule;
import com.github.gtexpert.gtbm.integration.gendustry.metatileentities.GendustryMetaTileEntities;
import com.github.gtexpert.gtbm.integration.gendustry.recipes.GendustryBlocksRecipe;
import com.github.gtexpert.gtbm.integration.gendustry.recipes.GendustryItemsRecipe;
import com.github.gtexpert.gtbm.module.Modules;

@TModule(
         moduleID = Modules.MODULE_GENDUSTRY,
         containerID = ModValues.MODID,
         modDependencies = { Mods.Names.FORESTRY, Mods.Names.GENDUSTRY },
         name = "GTBeesMatrix Gendustry For Minecraft Integration",
         description = "Gendustry Integration Module")
public class GendustryModule extends GTBMIntegrationSubmodule {

    @Override
    public void registerBlocks(RegistryEvent.Register<Block> event) {
        GendustryMetaTileEntities.init();
    }

    @Override
    public void postInit(FMLPostInitializationEvent event) {
        GendustryItemsRecipe.init();
        GendustryBlocksRecipe.init();
        GendustryCraftingRecipe.init();
    }
}
