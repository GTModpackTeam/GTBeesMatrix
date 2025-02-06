package com.github.gtexpert.gtbm.common;

import net.minecraftforge.common.config.Config;

import com.github.gtexpert.gtbm.api.ModValues;

@Config(modid = ModValues.MODID,
        name = ModValues.MODID + "/" + ModValues.MODID,
        category = "GregTech Bees Matrix")
public class GTBMConfigHolder {

    @Config.Comment({ "", "Default: false" })
    public static Boolean featureFlag = false;
}
