package com.github.gtexpert.gtbm.integration.top.provider;

import net.bdew.gendustry.api.ApiaryModifiers;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;

import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.metatileentity.interfaces.IGregTechTileEntity;

import com.github.gtexpert.gtbm.api.ModValues;
import com.github.gtexpert.gtbm.integration.gendustry.metatileentities.MetaTileEntityIndustrialApiary;

import forestry.api.apiculture.BeeManager;
import forestry.api.apiculture.EnumBeeType;
import forestry.api.apiculture.IBee;
import forestry.api.apiculture.IBeeRoot;
import mcjty.theoneprobe.api.IProbeHitData;
import mcjty.theoneprobe.api.IProbeInfo;
import mcjty.theoneprobe.api.IProbeInfoProvider;
import mcjty.theoneprobe.api.ProbeMode;
import mcjty.theoneprobe.api.TextStyleClass;

public class IndustrialApiaryProvider implements IProbeInfoProvider {

    @Override
    public String getID() {
        return ModValues.MODID + ":industrial_apiary";
    }

    @Override
    public void addProbeInfo(ProbeMode probeMode, IProbeInfo probeInfo, EntityPlayer entityPlayer, World world,
                             IBlockState state, IProbeHitData data) {
        if (!state.getBlock().hasTileEntity(state)) return;

        TileEntity tileEntity = world.getTileEntity(data.getPos());
        if (!(tileEntity instanceof IGregTechTileEntity)) return;

        MetaTileEntity metaTileEntity = ((IGregTechTileEntity) tileEntity).getMetaTileEntity();
        if (!(metaTileEntity instanceof MetaTileEntityIndustrialApiary)) return;

        MetaTileEntityIndustrialApiary apiary = (MetaTileEntityIndustrialApiary) metaTileEntity;

        // Species
        ItemStack queenStack = apiary.getQueen();
        if (!queenStack.isEmpty()) {
            IBeeRoot beeRoot = BeeManager.beeRoot;
            if (beeRoot != null) {
                IBee bee = beeRoot.getMember(queenStack);
                if (bee != null) {
                    EnumBeeType type = beeRoot.getType(queenStack);
                    if (type == EnumBeeType.QUEEN || type == EnumBeeType.PRINCESS) {
                        String speciesName = bee.getGenome().getPrimary().getAlleleName();
                        probeInfo.text(
                                TextStyleClass.INFO + "{*gtbm.top.industrial_apiary.species*} " + speciesName);
                    }
                }
            }
        }

        // Sneaking: Modifiers
        if (entityPlayer.isSneaking()) {
            ApiaryModifiers mods = apiary.getModifiers();
            if (mods != null) {
                probeInfo.text(TextStyleClass.INFO + "{*gtbm.top.industrial_apiary.production*} " +
                        TextFormatting.GOLD + Math.round(mods.production * 100) + "%");
                probeInfo.text(TextStyleClass.INFO + "{*gtbm.top.industrial_apiary.lifespan*} " +
                        TextFormatting.GOLD + Math.round(mods.lifespan * 100) + "%");
            }
        }
    }
}
