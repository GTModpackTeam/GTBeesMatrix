package com.github.gtexpert.gtbm.common;

import com.github.gtexpert.gtbm.api.ModValues;

import net.minecraftforge.common.config.Config;

@Config(modid = ModValues.MODID,
        name = ModValues.MODID + "/" + ModValues.MODID,
        category = "GregTech Bees Matrix")
public class GTBMConfigHolder {

    @Config.Comment({ "", "Default: false" })
    public static Boolean featureFlag = false;
}
