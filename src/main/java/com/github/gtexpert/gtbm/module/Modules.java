package com.github.gtexpert.gtbm.module;

import com.github.gtexpert.gtbm.api.ModValues;
import com.github.gtexpert.gtbm.api.modules.IModuleContainer;

public class Modules implements IModuleContainer {

    public static final String MODULE_CORE = "core";
    public static final String MODULE_TOOLS = "tools";
    public static final String MODULE_INTEGRATION = "integration";

    // Integration modules
    public static final String MODULE_JEI = "jei_integration";
    public static final String MODULE_TOP = "top_integration";
    public static final String MODULE_CT = "ct_integration";
    public static final String MODULE_FORESTRY = "forestry";
    public static final String MODULE_GENDUSTRY = "gendustry_integration";
    public static final String MODULE_BINNIES = "binnies_integration";
    public static final String MODULE_BOTANY = "botany_integration";
    public static final String MODULE_EXBEES = "extrabees_integration";
    public static final String MODULE_EXTREES = "extratrees_integration";
    public static final String MODULE_GENETICS = "genetics_integration";
    public static final String MODULE_TC = "tc_integration";
    public static final String MODULE_GTFO = "gtfo_integration";

    @Override
    public String getID() {
        return ModValues.MODID;
    }
}
