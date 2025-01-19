package com.github.gtexpert.gtbm.integration.gendustry;

import net.minecraftforge.common.config.Config;

import com.github.gtexpert.gtbm.api.ModValues;
import com.github.gtexpert.gtbm.module.Modules;

@Config.LangKey(ModValues.MODID + ".config.integration.gendustry")
@Config(modid = ModValues.MODID,
        name = ModValues.MODID + "/integration/" + Modules.MODULE_GENDUSTRY,
        category = "Gendustry")
public class GendustryConfigHolder {}
