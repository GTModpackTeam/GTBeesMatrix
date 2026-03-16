package com.github.gtexpert.gtbm.integration.gtfo.farming;

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
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import forestry.api.farming.ICrop;
import forestry.api.farming.IFarmable;
import forestry.api.farming.IFarmableInfo;
import forestry.farming.logic.crops.CropDestroy;

public class FarmableGTFOSapling implements IFarmable {

    private final Block saplingBlock;
    private final ItemStack germling;
    private final ItemStack[] windfall;

    public FarmableGTFOSapling(Block saplingBlock, int itemDamage, ItemStack[] windfall) {
        this.saplingBlock = saplingBlock;
        this.germling = new ItemStack(saplingBlock, 1, itemDamage);
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
        ItemStack originalHeldItem = player.getHeldItem(EnumHand.MAIN_HAND);
        try {
            ItemStack copy = germling.copy();
            player.setHeldItem(EnumHand.MAIN_HAND, copy);
            EnumActionResult result = copy.onItemUse(player, world, pos.down(),
                    EnumHand.MAIN_HAND, EnumFacing.UP, 0, 0, 0);
            if (result == EnumActionResult.SUCCESS) {
                world.playSound(null, pos, Blocks.SAPLING.getSoundType().getPlaceSound(),
                        SoundCategory.BLOCKS, 1.0F, 1.0F);
                return true;
            }
            return false;
        } finally {
            player.setHeldItem(EnumHand.MAIN_HAND, originalHeldItem);
        }
    }
}
