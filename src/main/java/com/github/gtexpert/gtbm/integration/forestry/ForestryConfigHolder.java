package com.github.gtexpert.gtbm.integration.forestry;

import net.minecraftforge.common.config.Config;

import com.github.gtexpert.gtbm.api.ModValues;
import com.github.gtexpert.gtbm.module.Modules;

@Config.LangKey(ModValues.MODID + ".config.integration.forestry")
@Config(modid = ModValues.MODID,
        name = ModValues.MODID + "/integration/" + Modules.MODULE_FORESTRY,
        category = "Forestry")
public class ForestryConfigHolder {

    @Config.Comment({ "Recipes for various items in Forestry will be more difficult",
            "default: NORMAL",
            "valid: [NORMAL, HARD]" })
    public static String gameMode = "NORMAL";

    @Config.Comment({ "If true, each will be uncraftable.", "default: false" })
    public static boolean Still = false,
            Fabricator = false,
            Centrifuge = false,
            Bottler = false,
            Fermenter = false,
            Rainmaker = false,
            Carpenter = false,
            Moistener = false,
            Raintank = false,
            Squeezer = false;
}
