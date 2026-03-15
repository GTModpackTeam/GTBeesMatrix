package com.github.gtexpert.gtbm.integration.gendustry.util;

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

    /**
     * Creates a climate helper using the given apiary modifiers.
     *
     * @param modifiers the Gendustry apiary modifiers (for biome override, temperature/humidity offsets)
     */
    public BeeClimateHelper(ApiaryModifiers modifiers) {
        this.modifiers = modifiers;
    }

    /**
     * Returns the effective biome, respecting the modifier's biome override.
     *
     * @param world the world (may be null)
     * @param pos   the block position
     * @return the overridden or actual biome, or null if world is null and no override exists
     */
    @Nullable
    public Biome getEffectiveBiome(@Nullable World world, BlockPos pos) {
        if (modifiers.biomeOverride != null) return modifiers.biomeOverride;
        return world != null ? world.getBiome(pos) : null;
    }

    /**
     * Returns the effective biome, requiring a non-null world.
     *
     * @param world the world (must not be null)
     * @param pos   the block position
     * @return the overridden or actual biome
     */
    public Biome getBiome(World world, BlockPos pos) {
        if (modifiers.biomeOverride != null) {
            return modifiers.biomeOverride;
        }
        return world.getBiome(pos);
    }

    /**
     * Computes the effective temperature, applying modifier offsets.
     *
     * @param world the world (may be null; defaults to NORMAL)
     * @param pos   the block position
     * @return the computed temperature enum value
     */
    public EnumTemperature getTemperature(@Nullable World world, BlockPos pos) {
        Biome biome = getEffectiveBiome(world, pos);
        if (biome == null) return EnumTemperature.NORMAL;
        if (BiomeHelper.isBiomeHellish(biome)) return EnumTemperature.HELLISH;
        return EnumTemperature.getFromValue(biome.getTemperature(pos) + modifiers.temperature);
    }

    /**
     * Computes the effective humidity, applying modifier offsets.
     *
     * @param world the world (may be null; defaults to NORMAL)
     * @param pos   the block position
     * @return the computed humidity enum value
     */
    public EnumHumidity getHumidity(@Nullable World world, BlockPos pos) {
        Biome biome = getEffectiveBiome(world, pos);
        if (biome == null) return EnumHumidity.NORMAL;
        return EnumHumidity.getFromValue(biome.getRainfall() + modifiers.humidity);
    }

    /**
     * Checks whether the effective biome is hellish (Nether-like).
     *
     * @param world the world (may be null)
     * @param pos   the block position
     * @return true if the effective biome is hellish
     */
    public boolean isHellish(@Nullable World world, BlockPos pos) {
        Biome biome = getEffectiveBiome(world, pos);
        return biome != null && BiomeHelper.isBiomeHellish(biome);
    }
}
