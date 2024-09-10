package com.github.gtexpert.gtbm.integration;

import java.util.Collections;
import java.util.Set;

import net.minecraft.util.ResourceLocation;

import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import com.github.gtexpert.gtbm.api.util.ModUtility;
import com.github.gtexpert.gtbm.module.BaseModule;
import com.github.gtexpert.gtbm.module.Modules;

public class GTBMIntegrationSubmodule extends BaseModule {

    private static final Set<ResourceLocation> DEPENDENCY_UID = Collections.singleton(
            ModUtility.id(Modules.MODULE_INTEGRATION));

    @NotNull
    @Override
    public Logger getLogger() {
        return GTBMIntegrationModule.logger;
    }

    @NotNull
    @Override
    public Set<ResourceLocation> getDependencyUids() {
        return DEPENDENCY_UID;
    }
}
