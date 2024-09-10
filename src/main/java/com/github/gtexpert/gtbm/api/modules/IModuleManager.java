package com.github.gtexpert.gtbm.api.modules;

import net.minecraft.util.ResourceLocation;

import com.github.gtexpert.gtbm.api.util.ModUtility;

public interface IModuleManager {

    default boolean isModuleEnabled(String containerID, String moduleID) {
        return isModuleEnabled(new ResourceLocation(containerID, moduleID));
    }

    default boolean isModuleEnabled(String moduleID) {
        return isModuleEnabled(ModUtility.id(moduleID));
    }

    boolean isModuleEnabled(ResourceLocation id);

    void registerContainer(IModuleContainer container);

    IModuleContainer getLoadedContainer();

    ModuleStage getStage();

    boolean hasPassedStage(ModuleStage stage);
}