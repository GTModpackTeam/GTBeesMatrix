package com.github.gtexpert.gtbm.client;

import static com.github.gtexpert.gtbm.api.util.ModUtility.disabledItems;

import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;

import com.github.gtexpert.gtbm.common.CommonProxy;

@Mod.EventBusSubscriber(Side.CLIENT)
public class ClientProxy extends CommonProxy {

    @Override
    public void preInit(FMLPreInitializationEvent event) {
        super.preInit(event);
    }

    @SubscribeEvent
    public static void registerModels(ModelRegistryEvent event) {}

    @SubscribeEvent
    public static void onItemTooltip(ItemTooltipEvent event) {
        ItemStack stack = event.getItemStack();
        if (disabledItems.size() > 1) {
            for (ItemStack disabled : disabledItems) {
                if (stack.isItemEqual(disabled)) {
                    event.getToolTip().add(I18n.format("gtbm.tooltip.warn.disabled_item"));
                }
            }

        }
    }
}
