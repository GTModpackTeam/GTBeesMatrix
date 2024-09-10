package com.github.gtexpert.gtbm.integration.forestry;

import net.minecraftforge.common.config.Config;

import com.github.gtexpert.gtbm.api.ModValues;
import com.github.gtexpert.gtbm.module.Modules;

@Config.LangKey(ModValues.MODID + ".config.integration.forestry")
@Config(modid = ModValues.MODID,
        name = ModValues.MODID + "/integration/" + Modules.MODULE_FORESTRY,
        category = "Forestry")
public class ForestryConfigHolder {}
