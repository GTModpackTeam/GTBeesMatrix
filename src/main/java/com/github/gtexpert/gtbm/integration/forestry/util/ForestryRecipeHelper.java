package com.github.gtexpert.gtbm.integration.forestry.util;

import org.jetbrains.annotations.NotNull;

import gregtech.common.ConfigHolder;

import com.github.gtexpert.gtbm.api.util.ModLog;

import forestry.api.core.ForestryAPI;

/**
 * Utilities for Forestry machine recipe timing and difficulty modes.
 */
public class ForestryRecipeHelper {

    public static float energyModifier = ForestryAPI.activeMode.getFloatSetting("energy.demand.modifier");
    public static int feToEu = ConfigHolder.compat.energy.euToFeRatio;

    /**
     * Calculates the Carpenter recipe duration in ticks from EU/t.
     *
     * @param EUt the energy per tick
     * @return duration in ticks
     */
    public static int timeCarpenter(int EUt) {
        return Math.round(EUt * 204 * energyModifier / (100 * feToEu));
    }

    /**
     * Calculates the Thermionic Fabricator recipe duration in ticks from EU/t.
     *
     * @param EUt the energy per tick
     * @return duration in ticks
     */
    public static int timeFabricator(int EUt) {
        return Math.round(200 * energyModifier / (EUt * feToEu));
    }

    /** Forestry recipe difficulty modes. Only NORMAL and HARD are actively supported. */
    public enum RecipeMode {

        EASY("EASY"),
        NORMAL("NORMAL"),
        HARD("HARD"),
        OP("OP");

        private final String mode;

        public String getString() {
            return this.mode;
        }

        RecipeMode(final String mode) {
            this.mode = mode;
        }

        /**
         * Parses a recipe mode by name, falling back to NORMAL on invalid input.
         *
         * @param name the mode name to parse
         * @return the matching recipe mode, or NORMAL if invalid
         */
        public static RecipeMode safeValueOf(@NotNull String name) {
            if (name.isEmpty()) {
                ModLog.logger.error("Invalid recipe mode is empty! Set to default value.", new Throwable());
                return NORMAL;
            }
            try {
                return RecipeMode.valueOf(name);
            } catch (IllegalArgumentException e) {
                ModLog.logger.error("Invalid recipe mode! Set to default value. : {}", name, e, new Throwable());
                return NORMAL;
            }
        }
    }
}
