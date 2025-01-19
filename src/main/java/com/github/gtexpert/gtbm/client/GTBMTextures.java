package com.github.gtexpert.gtbm.client;

import com.github.gtexpert.gtbm.api.ModValues;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.relauncher.Side;

import gregtech.client.renderer.texture.cube.*;

@Mod.EventBusSubscriber(modid = ModValues.MODID, value = Side.CLIENT)
public class GTBMTextures {

    // Gendustry
    public static OrientedOverlayRenderer INDUSTRIAL_APIARY_OVERLAY = new OrientedOverlayRenderer(
            "machines/industrial_apiary");
}
