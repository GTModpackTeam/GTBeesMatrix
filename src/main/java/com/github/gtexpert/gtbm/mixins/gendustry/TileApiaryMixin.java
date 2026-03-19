package com.github.gtexpert.gtbm.mixins.gendustry;

import net.bdew.gendustry.machines.apiary.TileApiary;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

/**
 * Disables the Automation Upgrade's princess-move-on-queen-death in
 * Gendustry's Industrial Apiary. GTBM uses a separate Auto Breeding
 * GUI toggle for this.
 */
@Mixin(value = TileApiary.class, remap = false)
public class TileApiaryMixin {

    /**
     * @author GTBM
     * @reason GTBM handles princess re-insertion via Auto Breeding toggle.
     */
    @Overwrite
    public void onQueenDeath() {}
}
