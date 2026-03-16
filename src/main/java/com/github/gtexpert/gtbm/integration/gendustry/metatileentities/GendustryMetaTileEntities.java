package com.github.gtexpert.gtbm.integration.gendustry.metatileentities;

import static com.github.gtexpert.gtbm.api.util.ModUtility.gtbm;
import static gregtech.api.GTValues.V;
import static gregtech.common.metatileentities.MetaTileEntities.registerMetaTileEntities;
import static gregtech.common.metatileentities.MetaTileEntities.registerMetaTileEntity;

import com.github.gtexpert.gtbm.client.GTBMTextures;
import com.github.gtexpert.gtbm.integration.gendustry.GendustryRecipeMaps;
import com.github.gtexpert.gtbm.integration.gendustry.metatileentities.multiblock.MetaTileEntityMegaApiary;

public class GendustryMetaTileEntities {

    public static MetaTileEntityIndustrialApiary[] INDUSTRIAL_APIARY = new MetaTileEntityIndustrialApiary[V.length - 1];

    // Multiblock
    public static MetaTileEntityMegaApiary MEGA_APIARY;

    public static void init() {
        // INDUSTRIAL_APIARY 20000~20012
        // TODO: IDの変更

        registerMetaTileEntities(INDUSTRIAL_APIARY, 20000, "industrial_apiary",
                (tier, voltageName) -> new MetaTileEntityIndustrialApiary(
                        gtbm(String.format("%s.%s", "industrial_apiary", voltageName)),
                        GendustryRecipeMaps.INDUSTRIAL_APIARY_RECIPES,
                        GTBMTextures.INDUSTRIAL_APIARY_OVERLAY,
                        tier, false, null));

        // Multiblock
        // Mega Apiary 20500
        MEGA_APIARY = registerMetaTileEntity(20500, new MetaTileEntityMegaApiary(
                gtbm("mega_apiary")));
    }
}
