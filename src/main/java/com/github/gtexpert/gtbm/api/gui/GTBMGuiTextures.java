package com.github.gtexpert.gtbm.api.gui;

import net.minecraft.util.ResourceLocation;

import gregtech.api.gui.resources.TextureArea;

public class GTBMGuiTextures {

    public static final TextureArea GTBM_LOGO = TextureArea
            .fullImage("textures/gui/icon/gtbm_logo.png");
    public static final TextureArea GTBM_LOGO_XMAS = TextureArea
            .fullImage("textures/gui/icon/gtbm_logo_xmas.png");
    public static final TextureArea GTBM_LOGO_DARK = TextureArea
            .fullImage("textures/gui/icon/gtbm_logo_dark.png");
    public static final TextureArea GTBM_LOGO_BLINKING_YELLOW = TextureArea
            .fullImage("textures/gui/icon/gtbm_logo_blinking_yellow.png");
    public static final TextureArea GTBM_LOGO_BLINKING_RED = TextureArea
            .fullImage("textures/gui/icon/gtbm_logo_blinking_red.png");

    // Bee status icon (directly from JEI's hint icons)
    public static final TextureArea BEE_INFO_ICON = new TextureArea(
            new ResourceLocation("jei", "textures/gui/icons/info.png"), 0, 0, 1, 1);

    // Bee slot overlays (directly from Gendustry's hint icons)
    public static final TextureArea QUEEN_OVERLAY = new TextureArea(
            new ResourceLocation("gendustry", "textures/items/hints/queen.png"), 0, 0, 1, 1);
    public static final TextureArea DRONE_OVERLAY = new TextureArea(
            new ResourceLocation("gendustry", "textures/items/hints/drone.png"), 0, 0, 1, 1);
    public static final TextureArea UPGRADE_OVERLAY = new TextureArea(
            new ResourceLocation("gendustry", "textures/items/hints/upgrade.png"), 0, 0, 1, 1);
}
