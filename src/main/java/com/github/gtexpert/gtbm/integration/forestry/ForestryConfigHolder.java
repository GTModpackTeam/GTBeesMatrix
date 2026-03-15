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

    @Config.Comment({ "If true, each will be uncraftable.", "default: true" })
    public static boolean Still = true,
            Fabricator = true,
            Centrifuge = true,
            Bottler = true,
            Fermenter = true,
            Rainmaker = true,
            Carpenter = true,
            Moistener = true,
            Raintank = true,
            Squeezer = true,
            FarmBlock = true;
}
