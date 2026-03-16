package com.github.gtexpert.gtbm.mixins.forestry;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

import forestry.core.render.ParticleRender;

/**
 * Fixes bee particle FX color to use the species overlay color (renderPass=1)
 * instead of the body base color (renderPass=0).
 *
 * <p>
 * Forestry's {@link ParticleRender#addBeeHiveFX} uses {@code getSpriteColour(0)}
 * which returns the gray body base for most bees. The distinctive species color
 * (e.g. yellow for Common bee) is at renderPass=1.
 * </p>
 */
@Mixin(value = ParticleRender.class, remap = false)
public class ParticleRenderMixin {

    /**
     * Changes the renderPass argument of {@code getSpriteColour(0)} to {@code getSpriteColour(1)}
     * so that bee particles use the overlay/species color instead of the body base color.
     */
    @ModifyArg(
               method = "addBeeHiveFX",
               at = @At(value = "INVOKE",
                        target = "getSpriteColour(I)I"),
               index = 0)
    private static int gtbm$useOverlayRenderPass(int renderPass) {
        return 1;
    }
}
