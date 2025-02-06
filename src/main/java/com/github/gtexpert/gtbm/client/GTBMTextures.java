package com.github.gtexpert.gtbm.client;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.relauncher.Side;

import gregtech.client.renderer.texture.cube.*;

import com.github.gtexpert.gtbm.api.ModValues;

@Mod.EventBusSubscriber(modid = ModValues.MODID, value = Side.CLIENT)
public class GTBMTextures {

    // Gendustry
    public static OrientedOverlayRenderer INDUSTRIAL_APIARY_OVERLAY = new OrientedOverlayRenderer(
            "machines/industrial_apiary");
}
