package com.github.gtexpert.gtbm.module;

import com.github.gtexpert.gtbm.api.ModValues;
import com.github.gtexpert.gtbm.api.modules.IModuleContainer;

public class Modules implements IModuleContainer {

    public static final String MODULE_CORE = "core";
    public static final String MODULE_TOOLS = "tools";
    public static final String MODULE_INTEGRATION = "integration";

    public static final String MODULE_FORESTRY = "forestry";

    @Override
    public String getID() {
        return ModValues.MODID;
    }
}
