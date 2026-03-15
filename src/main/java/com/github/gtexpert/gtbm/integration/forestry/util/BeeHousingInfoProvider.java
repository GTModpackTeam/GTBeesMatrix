package com.github.gtexpert.gtbm.integration.forestry.util;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import org.jetbrains.annotations.NotNull;

import gregtech.api.metatileentity.interfaces.IGregTechTileEntity;

import com.github.gtexpert.gtbm.api.ModValues;

import forestry.api.apiculture.*;
import mcjty.theoneprobe.api.IProbeHitData;
import mcjty.theoneprobe.api.IProbeInfo;
import mcjty.theoneprobe.api.IProbeInfoProvider;
import mcjty.theoneprobe.api.ProbeMode;

/**
 * TOP provider that shows queen health for any GT MTE implementing IBeeHousing.
 */
public class BeeHousingInfoProvider implements IProbeInfoProvider {

    private static final int HEALTH_THRESHOLD_DIVISOR = 4;
    private static final int HEALTH_COLOR_HIGH = 0xFF00D4CE;
    private static final int HEALTH_COLOR_LOW = 0xFFBB1C28;

    @Override
    public String getID() {
        return ModValues.MODID + ":bee_housing_provider";
    }

    @Override
    public void addProbeInfo(@NotNull ProbeMode mode, @NotNull IProbeInfo probeInfo, @NotNull EntityPlayer player,
                             @NotNull World world, @NotNull IBlockState blockState, @NotNull IProbeHitData data) {
        TileEntity te = world.getTileEntity(data.getPos());
        if (!(te instanceof IGregTechTileEntity)) return;

        var mte = ((IGregTechTileEntity) te).getMetaTileEntity();
        if (!(mte instanceof IBeeHousing)) return;

        IBeeHousing housing = (IBeeHousing) mte;
        IBeeHousingInventory inv = housing.getBeeInventory();
        if (inv.getQueen().isEmpty()) return;

        IBeeRoot beeRoot = BeeManager.beeRoot;
        if (beeRoot == null) return;

        EnumBeeType type = beeRoot.getType(inv.getQueen());
        if (type == EnumBeeType.QUEEN) {
            IBee bee = beeRoot.getMember(inv.getQueen());
            if (bee != null && bee.getMaxHealth() > 0) {
                int health = bee.getHealth();
                int maxHealth = bee.getMaxHealth();
                int color = health > maxHealth / HEALTH_THRESHOLD_DIVISOR ? HEALTH_COLOR_HIGH : HEALTH_COLOR_LOW;
                probeInfo.progress(health, maxHealth, probeInfo.defaultProgressStyle()
                        .suffix(" / " + maxHealth + " HP")
                        .filledColor(color)
                        .alternateFilledColor(color)
                        .borderColor(0xFF555555));
            }
        }
    }
}
