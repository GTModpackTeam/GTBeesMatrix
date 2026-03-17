package com.github.gtexpert.gtbm.integration.forestry.util;

import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandlerModifiable;

import forestry.api.apiculture.EnumBeeType;
import forestry.api.apiculture.IBeeHousingInventory;
import forestry.api.apiculture.IBeeRoot;

/**
 * Reusable product distribution logic for bee housings.
 * Handles automated bee re-insertion and output slot management.
 */
public class BeeProductHelper {

    /**
     * Add a bee product to the housing's inventory.
     * If automated, attempts to re-insert bees into queen/drone slots first.
     *
     * @param product      the product to add
     * @param beeRoot      the bee root (nullable, skips automation if null)
     * @param autoBreeding whether auto-breeding is enabled (princess auto-insertion)
     * @param isAutomated  whether automation upgrade is active (drone auto-insertion)
     * @param beeInv       the bee housing inventory (queen/drone access)
     * @param importItems  the import item handler (for drone merging)
     * @param exportItems  the export item handler (output slots)
     * @return true if the product was fully inserted
     */
    public static boolean addProduct(ItemStack product, IBeeRoot beeRoot,
                                     boolean autoBreeding, boolean isAutomated,
                                     IBeeHousingInventory beeInv,
                                     IItemHandlerModifiable importItems, IItemHandlerModifiable exportItems) {
        if (product.isEmpty()) return true;

        ItemStack remaining = product.copy();

        if (beeRoot != null && beeRoot.isMember(remaining)) {
            if (autoBreeding && (beeRoot.isMember(remaining, EnumBeeType.PRINCESS) ||
                    beeRoot.isMember(remaining, EnumBeeType.QUEEN))) {
                if (beeInv.getQueen().isEmpty()) {
                    beeInv.setQueen(remaining);
                    return true;
                }
            }
            if (isAutomated && beeRoot.isMember(remaining, EnumBeeType.DRONE)) {
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
