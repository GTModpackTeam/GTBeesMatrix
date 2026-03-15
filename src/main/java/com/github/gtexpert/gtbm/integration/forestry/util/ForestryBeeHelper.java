package com.github.gtexpert.gtbm.integration.forestry.util;

/**
 * Utilities for Forestry bee work cycle timing and display.
 */
public class ForestryBeeHelper {

    /** Get ticks per bee work cycle from Forestry config. Default 550. */
    public static int getTicksPerWorkCycle() {
        return forestry.apiculture.ModuleApiculture.ticksPerBeeWorkCycle;
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
