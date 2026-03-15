package com.github.gtexpert.gtbm.integration.forestry.metatileentites;

import com.github.gtexpert.gtbm.integration.forestry.metatileentites.multiblock.electric.megaapiary.MetaTileEntityMegaApiary;

import static com.github.gtexpert.gtbm.api.util.ModUtility.gtbm;
import static gregtech.common.metatileentities.MetaTileEntities.registerMetaTileEntity;

public class ForestryMetaTileEntities {

    public static MetaTileEntityMegaApiary MEGA_APIARY;

    public static void init() {
        register();
    }

    public static void register() {
        MEGA_APIARY = registerMetaTileEntity(30000, new MetaTileEntityMegaApiary(
                gtbm("mega_apiary")));
    }
}
