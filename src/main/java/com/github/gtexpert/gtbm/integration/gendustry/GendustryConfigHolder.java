package com.github.gtexpert.gtbm.integration.gendustry;

import net.minecraftforge.common.config.Config;

import com.github.gtexpert.gtbm.api.ModValues;
import com.github.gtexpert.gtbm.module.Modules;

@Config.LangKey(ModValues.MODID + ".config.integration.gendustry")
@Config(modid = ModValues.MODID,
        name = ModValues.MODID + "/integration/" + Modules.MODULE_GENDUSTRY,
        category = "Gendustry")
public class GendustryConfigHolder {

    @Config.Comment({ "If true, each will be uncraftable.", "default: true" })
    public static boolean industrialApiary = true;

    @Config.Comment("MetaTileEntity ID settings")
    public static MetaTileEntityIds metaTileEntityIds = new MetaTileEntityIds();

    public static class MetaTileEntityIds {

        @Config.Comment({ "Starting ID for Industrial Apiary (uses 12 consecutive IDs).", "default: 20000" })
        @Config.RangeInt(min = 1, max = 32767)
        public int industrialApiaryStartId = 20000;

        @Config.Comment({ "ID for Mega Apiary.", "default: 20500" })
        @Config.RangeInt(min = 1, max = 32767)
        public int megaApiaryId = 20500;
    }
}
