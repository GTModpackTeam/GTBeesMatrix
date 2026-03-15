package com.github.gtexpert.gtbm.integration.forestry.util;

import javax.annotation.Nullable;

import net.bdew.gendustry.api.ApiaryModifiers;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;

import forestry.api.core.BiomeHelper;
import forestry.api.core.EnumHumidity;
import forestry.api.core.EnumTemperature;

/**
 * Reusable climate calculation helper for bee housings.
 * Combines biome data with ApiaryModifiers for temperature/humidity.
 */
public class BeeClimateHelper {

    private final ApiaryModifiers modifiers;

    public BeeClimateHelper(ApiaryModifiers modifiers) {
        this.modifiers = modifiers;
    }

    @Nullable
    public Biome getEffectiveBiome(@Nullable World world, BlockPos pos) {
        if (modifiers.biomeOverride != null) return modifiers.biomeOverride;
        return world != null ? world.getBiome(pos) : null;
    }

    public Biome getBiome(World world, BlockPos pos) {
        if (modifiers.biomeOverride != null) {
            return modifiers.biomeOverride;
        }
        return world.getBiome(pos);
    }

    public EnumTemperature getTemperature(@Nullable World world, BlockPos pos) {
        Biome biome = getEffectiveBiome(world, pos);
        if (biome == null) return EnumTemperature.NORMAL;
        if (BiomeHelper.isBiomeHellish(biome)) return EnumTemperature.HELLISH;
        return EnumTemperature.getFromValue(biome.getTemperature(pos) + modifiers.temperature);
    }

    public EnumHumidity getHumidity(@Nullable World world, BlockPos pos) {
        Biome biome = getEffectiveBiome(world, pos);
        if (biome == null) return EnumHumidity.NORMAL;
        return EnumHumidity.getFromValue(biome.getRainfall() + modifiers.humidity);
    }

    public boolean isHellish(@Nullable World world, BlockPos pos) {
        Biome biome = getEffectiveBiome(world, pos);
        return biome != null && BiomeHelper.isBiomeHellish(biome);
    }
}
