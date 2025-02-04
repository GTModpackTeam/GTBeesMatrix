package com.github.gtexpert.gtbm.core;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import gregtech.api.GTValues;
import gregtech.api.unification.OreDictUnifier;
import gregtech.common.blocks.MetaBlocks;

import com.github.gtexpert.gtbm.Tags;
import com.github.gtexpert.gtbm.api.ModValues;
import com.github.gtexpert.gtbm.api.modules.IModule;
import com.github.gtexpert.gtbm.api.modules.TModule;
import com.github.gtexpert.gtbm.common.CommonProxy;
import com.github.gtexpert.gtbm.loaders.recipe.CEuOverrideRecipe;
import com.github.gtexpert.gtbm.module.Modules;

@TModule(
         moduleID = Modules.MODULE_CORE,
         containerID = ModValues.MODID,
         name = "GTBMMod Core",
         description = "Core of GTBMMod",
         coreModule = true)
public class CoreModule implements IModule {

    public static final Logger logger = LogManager.getLogger(Tags.MODNAME + " Core");
    @SidedProxy(modId = ModValues.MODID,
                clientSide = "com.github.gtexpert.gtbm.client.ClientProxy",
                serverSide = "com.github.gtexpert.gtbm.common.CommonProxy")
    public static CommonProxy proxy;

    @Override
    public @NotNull Logger getLogger() {
        return logger;
    }

    @Override
    public void preInit(FMLPreInitializationEvent event) {
        proxy.preInit(event);

        logger.info("Hello World!");
    }

    @Override
    public void registerRecipesLow(RegistryEvent.Register<IRecipe> event) {
        OreDictUnifier.registerOre(new ItemStack(MetaBlocks.RUBBER_LOG, 1, GTValues.W), "logRubber");
    }

    @Override
    public void registerRecipesLowest(RegistryEvent.Register<IRecipe> event) {
        CEuOverrideRecipe.init();
    }
}
