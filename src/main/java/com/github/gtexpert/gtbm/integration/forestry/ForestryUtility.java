package com.github.gtexpert.gtbm.integration.forestry;

import org.jetbrains.annotations.NotNull;

import gregtech.common.ConfigHolder;

import com.github.gtexpert.gtbm.api.util.ModLog;

import forestry.api.core.ForestryAPI;

public class ForestryUtility {

    public static float energyModifier = ForestryAPI.activeMode.getFloatSetting("energy.demand.modifier");
    public static int feToEu = ConfigHolder.compat.energy.euToFeRatio;

    public static int timeCarpenter(int EUt) {
        return Math.round(EUt * 204 * energyModifier / (100 * feToEu));
    }

    public static int timeFabricator(int EUt) {
        return Math.round(200 * energyModifier / (EUt * feToEu));
    }

    public enum recipeMode {

        EASY("EASY"), // NO SUPPORT
        NORMAL("NORMAL"),
        HARD("HARD"),
        OP("OP"); // NO SUPPORT

        private final String mode;

        public String getString() {
            return this.mode;
        }

        recipeMode(final String mode) {
            this.mode = mode;
        }

        public static recipeMode safeValueOf(@NotNull String name) {
            if (name.isEmpty()) {
                ModLog.logger.error("Invalid recipe mode is empty! Set to default value.", new Throwable());
                return NORMAL;
            }

            try {
                return recipeMode.valueOf(name);
            } catch (IllegalArgumentException e) {
                ModLog.logger.error("Invalid recipe mode! Set to default value. : {}", name, e, new Throwable());
                return NORMAL;
            }
        }
    }
}
