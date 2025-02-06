package com.github.gtexpert.gtbm.api.unification.material;

import static gregtech.api.unification.material.info.MaterialFlags.*;

import gregtech.api.unification.material.Materials;

import com.github.gtexpert.gtbm.api.util.Mods;

public class GTBMMaterialFlags {

    public static void init() {
        if (Mods.Forestry.isModLoaded()) {
            // Copper
            Materials.Copper.addFlags(GENERATE_GEAR);

            // Tin
            Materials.Tin.addFlags(GENERATE_GEAR);

            // Iron
            Materials.Iron.addFlags(GENERATE_FINE_WIRE, GENERATE_FOIL);

            // Bronze
            Materials.Bronze.addFlags(GENERATE_FINE_WIRE);

            // Rose Gold
            Materials.RoseGold.addFlags(GENERATE_FOIL);
        }
    }
}
