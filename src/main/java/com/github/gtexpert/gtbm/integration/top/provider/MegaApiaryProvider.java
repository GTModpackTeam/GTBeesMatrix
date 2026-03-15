package com.github.gtexpert.gtbm.integration.top.provider;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;

import gregtech.api.GTValues;
import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.metatileentity.interfaces.IGregTechTileEntity;
import gregtech.api.util.GTUtility;
import gregtech.api.util.TextFormattingUtil;

import com.github.gtexpert.gtbm.api.ModValues;
import com.github.gtexpert.gtbm.integration.gendustry.metatileentities.multiblock.MetaTileEntityMegaApiary;

import mcjty.theoneprobe.api.IProbeHitData;
import mcjty.theoneprobe.api.IProbeInfo;
import mcjty.theoneprobe.api.IProbeInfoProvider;
import mcjty.theoneprobe.api.NumberFormat;
import mcjty.theoneprobe.api.ProbeMode;
import mcjty.theoneprobe.api.TextStyleClass;

public class MegaApiaryProvider implements IProbeInfoProvider {

    @Override
    public String getID() {
        return ModValues.MODID + ":mega_apiary";
    }

    @Override
    public void addProbeInfo(ProbeMode probeMode, IProbeInfo probeInfo, EntityPlayer entityPlayer, World world,
                             IBlockState state, IProbeHitData data) {
        if (state.getBlock().hasTileEntity(state)) {
            TileEntity tileEntity = world.getTileEntity(data.getPos());
            if (tileEntity instanceof IGregTechTileEntity) {
                MetaTileEntity metaTileEntity = ((IGregTechTileEntity) tileEntity).getMetaTileEntity();
                if (metaTileEntity instanceof MetaTileEntityMegaApiary &&
                        ((MetaTileEntityMegaApiary) metaTileEntity).isStructureFormed()) {
                    // Progress Bar
                    int currentProgress = ((MetaTileEntityMegaApiary) metaTileEntity).getProgressTicks();
                    int maxProgress = 100;
                    String text = null;
                    if (metaTileEntity.isActive()) {
                        currentProgress = Math.round(currentProgress / 20.0F);
                        maxProgress = Math.round(maxProgress / 20.0F);
                        text = " / " + TextFormattingUtil.formatNumbers(maxProgress) + " s";
                    }
                    if (maxProgress > 0) {
                        int color = ((MetaTileEntityMegaApiary) metaTileEntity).isWorkingEnabled() ? 0xFF4CBB17 :
                                0xFFBB1C28;
                        probeInfo.progress(currentProgress, maxProgress, probeInfo.defaultProgressStyle()
                                .suffix(text)
                                .filledColor(color)
                                .alternateFilledColor(color)
                                .borderColor(0xFF555555).numberFormat(NumberFormat.COMMAS));
                    }
                    // Energy
                    long EUt = ((MetaTileEntityMegaApiary) metaTileEntity).getCurrentConsumption();
                    text = TextFormatting.RED + TextFormattingUtil.formatNumbers(EUt) + TextStyleClass.INFO +
                            " EU/t" + TextFormatting.GREEN +
                            " (" + GTValues.VNF[GTUtility.getTierByVoltage(EUt)] + TextFormatting.GREEN + ")";
                    probeInfo.text(TextStyleClass.INFO + "{*gregtech.top.energy_consumption*} " + text);

                    // Queen
                    int queenAmount = ((MetaTileEntityMegaApiary) metaTileEntity).getQueenAmount();
                    if (queenAmount > 0) {
                        probeInfo.text(TextStyleClass.INFO + "{*gtbm.top.mega_apiary.queen*} " + queenAmount);
                    }
                    // Product
                    if (!entityPlayer.isSneaking()) {
                        for (ItemStack stack : ((MetaTileEntityMegaApiary) metaTileEntity).getProductList()) {
                            String displayName = stack.getDisplayName();
                            int count = stack.getCount();
                            probeInfo.text(
                                    TextStyleClass.INFO + displayName + ": " + TextFormatting.GOLD + "x" + count);
                        }
                    }

                }
            }
        }
    }
}
