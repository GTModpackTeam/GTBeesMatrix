package com.github.gtexpert.gtbm.mixins.forestry;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.google.common.collect.HashMultimap;

import forestry.api.apiculture.BeeManager;
import forestry.api.apiculture.FlowerManager;
import forestry.api.apiculture.IBee;
import forestry.api.apiculture.IBeeGenome;
import forestry.api.genetics.IFlowerProvider;
import forestry.apiculture.items.ItemBeeGE;
import forestry.core.utils.BlockStateSet;

/**
 * Adds accepted flower block list to analyzed bee item tooltips.
 * When Shift is held, shows the full list of flower blocks accepted
 * by this bee's flower provider.
 */
@Mixin(value = ItemBeeGE.class, remap = false)
public class ItemBeeGEMixin {

    @Inject(method = "addInformation", at = @At("TAIL"))
    private void gtbm$addFlowerInfo(ItemStack itemstack, @Nullable World world, List<String> list,
                                    ITooltipFlag flag, CallbackInfo ci) {
        if (itemstack.getTagCompound() == null) return;
        IBee bee = BeeManager.beeRoot.getMember(itemstack);
        if (bee == null || !bee.isAnalyzed()) return;
        if (!GuiScreen.isShiftKeyDown()) return;

        IBeeGenome genome = bee.getGenome();
        IFlowerProvider flowerProvider = genome.getFlowerProvider();
        String flowerType = flowerProvider.getFlowerType();

        Set<String> flowerNames = getAcceptedFlowerNames(flowerType);
        if (!flowerNames.isEmpty()) {
            for (String name : flowerNames) {
                list.add(TextFormatting.DARK_GRAY + "  - " + TextFormatting.WHITE + name);
            }
        }
    }

    /**
     * Collects display names of all accepted flower blocks for a given flower type
     * using the {@link FlowerRegistryAccessor} to access internal registration data.
     * Filters out air and invalid items.
     */
    private static Set<String> getAcceptedFlowerNames(String flowerType) {
        Set<String> names = new LinkedHashSet<>();
        try {
            FlowerRegistryAccessor registry = (FlowerRegistryAccessor) FlowerManager.flowerRegistry;

            HashMultimap<String, Block> acceptableBlocks = registry.gtbm$getAcceptableBlocks();
            for (Block block : acceptableBlocks.get(flowerType)) {
                String name = getBlockDisplayName(block);
                if (name != null) names.add(name);
            }

            Map<String, BlockStateSet> acceptableStates = registry.gtbm$getAcceptableBlockStates();
            BlockStateSet stateSet = acceptableStates.get(flowerType);
            if (stateSet != null) {
                for (IBlockState state : stateSet) {
                    String name = getStateDisplayName(state);
                    if (name != null) names.add(name);
                }
            }
        } catch (Exception ignored) {}
        return names;
    }

    @Nullable
    private static String getBlockDisplayName(Block block) {
        if (block == Blocks.AIR) return null;
        ItemStack stack = new ItemStack(block);
        if (!stack.isEmpty()) return stack.getDisplayName();
        return block.getLocalizedName();
    }

    @Nullable
    private static String getStateDisplayName(IBlockState state) {
        Block block = state.getBlock();
        if (block == Blocks.AIR) return null;
        int meta = block.getMetaFromState(state);
        ItemStack stack = new ItemStack(block, 1, meta);
        if (!stack.isEmpty()) return stack.getDisplayName();
        return block.getLocalizedName();
    }
}
