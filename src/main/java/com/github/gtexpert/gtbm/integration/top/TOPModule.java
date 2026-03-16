package com.github.gtexpert.gtbm.integration.top;

import net.minecraftforge.fml.common.event.FMLInitializationEvent;

import com.github.gtexpert.gtbm.api.ModValues;
import com.github.gtexpert.gtbm.api.modules.TModule;
import com.github.gtexpert.gtbm.api.util.Mods;
import com.github.gtexpert.gtbm.integration.GTBMIntegrationSubmodule;
import com.github.gtexpert.gtbm.integration.forestry.util.BeeHousingInfoProvider;
import com.github.gtexpert.gtbm.integration.top.provider.MegaApiaryProvider;
import com.github.gtexpert.gtbm.module.Modules;

import mcjty.theoneprobe.TheOneProbe;
import mcjty.theoneprobe.api.ITheOneProbe;

@TModule(
         moduleID = Modules.MODULE_TOP,
         containerID = ModValues.MODID,
         modDependencies = { Mods.Names.THE_ONE_PROBE },
         name = "GTBeesMatrix The One Probe Integration",
         description = "The One Probe Integration Module")
public class TOPModule extends GTBMIntegrationSubmodule {

    @Override
    public void init(FMLInitializationEvent event) {
        ITheOneProbe oneProbe = TheOneProbe.theOneProbeImp;
        if (Mods.Forestry.isModLoaded()) {
            oneProbe.registerProvider(new BeeHousingInfoProvider());
        }
        if (Mods.Gendustry.isModLoaded()) {
            oneProbe.registerProvider(new MegaApiaryProvider());
        }
    }
}
