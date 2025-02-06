package com.github.gtexpert.gtbm.api.unification.material;

/**
 * Material Registration.
 * <p>
 * All Material Builders should follow this general formatting:
 * <p>
 * material = new MaterialBuilder(id, name)
 * .ingot().fluid().ore() <--- types
 * .color().iconSet() <--- appearance
 * .flags() <--- special generation
 * .element() / .components() <--- composition
 * .toolStats() <---
 * .oreByProducts() | additional properties
 * ... <---
 * .blastTemp() <--- blast temperature
 * .build();
 * <p>
 * Use defaults to your advantage! Some defaults:
 * - iconSet: DULL
 * - color: 0xFFFFFF
 */
public class GTBMMaterials {
    /*
     * FOR ADDON DEVELOPERS:
     *
     * GTCEu will not take more than 3000 IDs. Anything past ID 2999
     * is considered FAIR GAME, take whatever you like.
     *
     * If you would like to reserve IDs, feel free to reach out to the
     * development team and claim a range of IDs! We will mark any
     * claimed ranges below this comment. Max value is 32767.
     *
     * - Gregicality: 3000-19999
     * - Gregification: 20000-20999
     * - HtmlTech: 21000-21499
     * - GregTech Food Option: 21500-22499
     * - FREE RANGE 22500-23599
     * - MechTech: 23600-23999
     * - FREE RANGE 24000-31999
     * - Reserved for CraftTweaker: 32000-32767
     */

    // Element Materials

    // First Degree Materials

    // Second Degree Materials

    // Third Degree Materials

    // Organic Chemistry Materials

    // Unknown Composition Materials

    public static void registerMaterialsHigh() {
        GTBMMaterialFlags.init();
    }

    public static void registerMaterialsLow() {}

    public static void registerMaterialsLowest() {}
}
