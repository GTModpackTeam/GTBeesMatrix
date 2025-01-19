package com.github.gtexpert.gtbm.integration.gendustry.metatileentities;

import static com.github.gtexpert.gtbm.api.util.ModUtility.gtbm;
import static gregtech.api.GTValues.V;
import static gregtech.common.metatileentities.MetaTileEntities.registerMetaTileEntities;

import com.github.gtexpert.gtbm.client.GTBMTextures;
import com.github.gtexpert.gtbm.common.GTBMConfigHolder;
import com.github.gtexpert.gtbm.integration.gendustry.GendustryRecipeMaps;

public class GendustryMetaTileEntities {

    public static MetaTileEntityIndustrialApiary[] INDUSTRIAL_APIARY = new MetaTileEntityIndustrialApiary[V.length - 1];

    public static void init() {
        // INDUSTRIAL_APIARY 20000~20012
        // TODO: IDの変更

        if (GTBMConfigHolder.featureFlag) {
            registerMetaTileEntities(INDUSTRIAL_APIARY, 20000, "industrial_apiary",
                    (tier, voltageName) -> new MetaTileEntityIndustrialApiary(
                            gtbm(String.format("%s.%s", "industrial_apiary", voltageName)),
                            GendustryRecipeMaps.INDUSTRIAL_APIARY_RECIPES,
                            GTBMTextures.INDUSTRIAL_APIARY_OVERLAY,
                            tier, false, null));
        }
    }
}
