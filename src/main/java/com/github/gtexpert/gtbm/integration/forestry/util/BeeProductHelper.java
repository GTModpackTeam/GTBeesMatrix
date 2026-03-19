package com.github.gtexpert.gtbm.integration.forestry.util;

import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandlerModifiable;

import forestry.api.apiculture.EnumBeeType;
import forestry.api.apiculture.IBeeHousingInventory;
import forestry.api.apiculture.IBeeRoot;

/**
 * Product distribution logic for bee housings backed by GT item handlers.
 */
public class BeeProductHelper {

    /**
     * Distributes a bee product into the housing's inventory.
     *
     * <p>
     * When the Automation Upgrade is active, bee members are re-inserted
     * into bee slots (princess → queen, drone → drone / merge).
     * Everything else goes to the export (output) slots.
     */
    public static boolean addProduct(ItemStack product, IBeeRoot beeRoot,
                                     boolean isAutomated,
                                     IBeeHousingInventory beeInv,
                                     IItemHandlerModifiable importItems, IItemHandlerModifiable exportItems) {
        if (product.isEmpty()) return true;

        ItemStack remaining = product.copy();

        if (isAutomated && beeRoot != null && beeRoot.isMember(remaining)) {
            if (beeRoot.isMember(remaining, EnumBeeType.PRINCESS)) {
                if (beeInv.getQueen().isEmpty()) {
                    beeInv.setQueen(remaining);
                    return true;
                }
            }
            if (beeRoot.isMember(remaining, EnumBeeType.DRONE)) {
                if (beeInv.getDrone().isEmpty()) {
                    beeInv.setDrone(remaining);
                    return true;
                }
                ItemStack mergeResult = importItems.insertItem(1, remaining, false);
                if (mergeResult.isEmpty()) return true;
                remaining = mergeResult;
            }
        }

        for (int i = 0; i < exportItems.getSlots(); i++) {
            remaining = exportItems.insertItem(i, remaining, false);
            if (remaining.isEmpty()) return true;
        }
        return remaining.isEmpty();
    }
}
