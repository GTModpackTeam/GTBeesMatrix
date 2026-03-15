package com.github.gtexpert.gtbm.integration.forestry.util;

import javax.annotation.Nullable;

import net.bdew.gendustry.api.ApiaryModifiers;

import forestry.api.apiculture.DefaultBeeModifier;
import forestry.api.apiculture.IBeeGenome;

/**
 * IBeeModifier implementation that delegates to ApiaryModifiers.
 * Reusable bridge between Gendustry's upgrade system and Forestry's modifier interface.
 * Override {@link #isHellish()} in subclass since it requires world/pos context.
 */
public class ApiaryModifierBridge extends DefaultBeeModifier {

    protected final ApiaryModifiers modifiers;

    public ApiaryModifierBridge(ApiaryModifiers modifiers) {
        this.modifiers = modifiers;
    }

    @Override
    public float getTerritoryModifier(IBeeGenome genome, float currentModifier) {
        return Math.min(modifiers.territory, 5);
    }

    @Override
    public float getMutationModifier(IBeeGenome genome, IBeeGenome mate, float currentModifier) {
        return modifiers.mutation;
    }

    @Override
    public float getLifespanModifier(IBeeGenome genome, @Nullable IBeeGenome mate, float currentModifier) {
        return modifiers.lifespan;
    }

    @Override
    public float getProductionModifier(IBeeGenome genome, float currentModifier) {
        return modifiers.production;
    }

    @Override
    public float getFloweringModifier(IBeeGenome genome, float currentModifier) {
        return modifiers.flowering;
    }

    @Override
    public float getGeneticDecay(IBeeGenome genome, float currentModifier) {
        return modifiers.geneticDecay;
    }

    @Override
    public boolean isSealed() {
        return modifiers.isSealed;
    }

    @Override
    public boolean isSelfLighted() {
        return modifiers.isSelfLighted;
    }

    @Override
    public boolean isSunlightSimulated() {
        return modifiers.isSunlightSimulated;
    }
}
