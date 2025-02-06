package com.github.gtexpert.gtbm.common;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import gregtech.api.unification.material.event.MaterialEvent;

import com.github.gtexpert.gtbm.api.ModValues;
import com.github.gtexpert.gtbm.api.unification.material.GTBMMaterials;

@Mod.EventBusSubscriber(modid = ModValues.MODID)
public class GTBMEventHandlers {

    public GTBMEventHandlers() {}

    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void registerMaterialsHigh(MaterialEvent event) {
        GTBMMaterials.registerMaterialsHigh();
    }

    @SubscribeEvent(priority = EventPriority.LOW)
    public static void registerMaterialsLow(MaterialEvent event) {
        GTBMMaterials.registerMaterialsLow();
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void registerMaterialsLowest(MaterialEvent event) {
        GTBMMaterials.registerMaterialsLowest();
    }
}
