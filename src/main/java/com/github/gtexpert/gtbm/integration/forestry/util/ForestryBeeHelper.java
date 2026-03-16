package com.github.gtexpert.gtbm.integration.forestry.util;

import forestry.apiculture.ModuleApiculture;

/**
 * Utilities for Forestry bee work cycle timing and display.
 */
public class ForestryBeeHelper {

    /**
     * Returns ticks per bee work cycle from Forestry's config.
     * Uses {@link ModuleApiculture#ticksPerBeeWorkCycle} (default 550,
     * configured via "beekeeping.ticks.work" in forestry's config).
     */
    public static int getTicksPerWorkCycle() {
        return ModuleApiculture.ticksPerBeeWorkCycle;
    }

    /** Effective ticks per health point, accounting for lifespan modifier. */
    public static int getEffectiveTicksPerHealth(float lifespanModifier) {
        return Math.round(getTicksPerWorkCycle() * lifespanModifier);
    }

    /** Format seconds as "Xm Ys" or "Xs". */
    public static String formatTime(int totalSeconds) {
        if (totalSeconds >= 60) {
            return String.format("%dm %ds", totalSeconds / 60, totalSeconds % 60);
        }
        return totalSeconds + "s";
    }
}
