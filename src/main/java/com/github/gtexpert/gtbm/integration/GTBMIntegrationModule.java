package com.github.gtexpert.gtbm.integration;

import java.util.Collections;
import java.util.List;

import net.minecraftforge.fml.common.event.FMLInitializationEvent;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import com.github.gtexpert.gtbm.api.ModValues;
import com.github.gtexpert.gtbm.api.modules.TModule;
import com.github.gtexpert.gtbm.module.BaseModule;
import com.github.gtexpert.gtbm.module.Modules;

@TModule(
         moduleID = Modules.MODULE_INTEGRATION,
         containerID = ModValues.MODID,
         name = "GTBeesMatrix Integration",
         description = "General GTBeesMatrix Integration Module. Disabling this disables all integration modules.")
public class GTBMIntegrationModule extends BaseModule {

    public static final Logger logger = LogManager.getLogger("GTBeesMatrix Mod Integration");

    @NotNull
    @Override
    public Logger getLogger() {
        return logger;
    }

    @NotNull
    @Override
    public List<Class<?>> getEventBusSubscribers() {
        return Collections.singletonList(GTBMIntegrationModule.class);
    }

    @Override
    public void init(FMLInitializationEvent event) {
        super.init(event);
    }
}
