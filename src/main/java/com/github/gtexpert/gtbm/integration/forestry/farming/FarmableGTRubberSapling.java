package com.github.gtexpert.gtbm.integration.forestry.farming;

import java.util.Collections;

import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import forestry.api.farming.ICrop;
import forestry.api.farming.IFarmable;
import forestry.api.farming.IFarmableInfo;
import forestry.core.network.packets.PacketFXSignal;
import forestry.core.utils.NetworkUtil;
import forestry.farming.logic.crops.CropDestroy;

public class FarmableGTRubberSapling implements IFarmable {

    private final Block saplingBlock;
    private final ItemStack germling;
    private final ItemStack[] windfall;

    public FarmableGTRubberSapling(Block saplingBlock, ItemStack[] windfall) {
        this.saplingBlock = saplingBlock;
        this.germling = new ItemStack(saplingBlock);
        this.windfall = windfall;
    }

    @Override
    public boolean isSaplingAt(World world, BlockPos pos, IBlockState blockState) {
        return blockState.getBlock() == saplingBlock;
    }

    @Nullable
    @Override
    public ICrop getCropAt(World world, BlockPos pos, IBlockState blockState) {
        Block block = blockState.getBlock();
        if (!block.isWood(world, pos)) {
            return null;
        }
        return new CropDestroy(world, blockState, pos);
    }

    @Override
    public boolean isGermling(ItemStack itemstack) {
        return ItemStack.areItemsEqual(germling, itemstack);
    }

    @Override
    public void addInformation(IFarmableInfo info) {
        info.addGermlings(Collections.singletonList(germling));
        info.addProducts(windfall);
    }

    @Override
    public boolean isWindfall(ItemStack itemstack) {
        for (ItemStack drop : windfall) {
            if (drop.isItemEqual(itemstack)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean plantSaplingAt(EntityPlayer player, ItemStack germling, World world, BlockPos pos) {
        ItemStack copy = germling.copy();
        player.setHeldItem(EnumHand.MAIN_HAND, copy);
        EnumActionResult result = copy.onItemUse(player, world, pos.down(),
                EnumHand.MAIN_HAND, EnumFacing.UP, 0, 0, 0);
        player.setHeldItem(EnumHand.MAIN_HAND, ItemStack.EMPTY);
        if (result == EnumActionResult.SUCCESS) {
            PacketFXSignal packet = new PacketFXSignal(
                    PacketFXSignal.SoundFXType.BLOCK_PLACE, pos,
                    Blocks.SAPLING.getDefaultState());
            NetworkUtil.sendNetworkPacket(packet, pos, world);
            return true;
        }
        return false;
    }
}
