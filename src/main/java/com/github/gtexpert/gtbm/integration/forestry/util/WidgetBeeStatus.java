package com.github.gtexpert.gtbm.integration.forestry.util;

import java.util.ArrayList;
import java.util.List;
import java.util.function.IntSupplier;

import net.bdew.gendustry.api.ApiaryModifiers;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import gregtech.api.gui.IRenderContext;
import gregtech.api.gui.Widget;
import gregtech.api.util.Position;
import gregtech.api.util.Size;

import forestry.api.apiculture.*;
import forestry.api.core.*;

/**
 * Gendustry-style bee status widget.
 * Displays Forestry error icons (cycled) and shows climate/bee stats on hover.
 */
public class WidgetBeeStatus extends Widget {

    private static final int SYNC_TOOLTIP = 0;
    private static final int SYNC_ERRORS = 1;
    private static final int BREEDING_TIME_TICKS = 100;
    private static final char LINE_SEPARATOR = '\t';

    private final IBeeHousing housing;
    private final IBeeRoot beeRoot;
    private final ApiaryModifiers modifiers;
    private final IntSupplier euPerTickSupplier;

    // Client-side synced data
    private List<String> clientTooltip = new ArrayList<>();
    private List<Short> clientErrorIds = new ArrayList<>();

    // Server-side change tracking
    private int lastErrorHash = Integer.MIN_VALUE;
    private int syncTickCounter;

    public WidgetBeeStatus(int x, int y, IBeeHousing housing, IBeeRoot beeRoot, ApiaryModifiers modifiers,
                           IntSupplier euPerTickSupplier) {
        super(new Position(x, y), new Size(16, 16));
        this.housing = housing;
        this.beeRoot = beeRoot;
        this.modifiers = modifiers;
        this.euPerTickSupplier = euPerTickSupplier;
    }

    // ---- Server-side: sync ----

    @Override
    public void detectAndSendChanges() {
        if (housing.getWorldObj() == null) return;

        // Sync error IDs (only on change)
        IErrorLogic errorLogic = housing.getErrorLogic();
        var errorStates = errorLogic.getErrorStates();
        int errorHash = errorStates.hashCode();
        if (errorHash != lastErrorHash) {
            lastErrorHash = errorHash;
            writeUpdateInfo(SYNC_ERRORS, buf -> {
                buf.writeVarInt(errorStates.size());
                for (IErrorState error : errorStates) {
                    buf.writeShort(error.getID());
                }
            });
        }

        // Sync tooltip (active: every 1s, idle: every 2s)
        syncTickCounter++;
        boolean isActive = housing.getBeekeepingLogic() != null && !housing.getBeeInventory().getQueen().isEmpty();
        if (syncTickCounter >= (isActive ? 20 : 40)) {
            syncTickCounter = 0;
            List<String> tooltip = buildTooltipLines();
            writeUpdateInfo(SYNC_TOOLTIP, buf -> {
                buf.writeVarInt(tooltip.size());
                for (String line : tooltip) {
                    buf.writeString(line);
                }
            });
        }
    }

    private List<String> buildTooltipLines() {
        List<String> lines = new ArrayList<>();

        // Error descriptions
        for (IErrorState error : housing.getErrorLogic().getErrorStates()) {
            lines.add(TextFormatting.RED + error.getUnlocalizedDescription());
        }

        // Energy, climate
        addLine(lines, "gtbm.bee.label.energy", String.valueOf(euPerTickSupplier.getAsInt()));
        addLine(lines, "gtbm.bee.label.temperature", housing.getTemperature().getName().toLowerCase());
        addLine(lines, "gtbm.bee.label.humidity", housing.getHumidity().getName().toLowerCase());

        // Bee stats
        IBeeHousingInventory inv = housing.getBeeInventory();
        if (beeRoot != null && !inv.getQueen().isEmpty()) {
            IBee bee = beeRoot.getMember(inv.getQueen());
            if (bee != null) {
                addBeeStats(lines, bee, inv);
            }
        }

        // Upgrade note (check if any modifier is non-default)
        if (hasActiveUpgrades(modifiers)) {
            addLine(lines, "gtbm.bee.label.upgrade_note", "");
        }

        return lines;
    }

    private static boolean hasActiveUpgrades(ApiaryModifiers mods) {
        return mods.energy != 1 || mods.lifespan != 1 || mods.production != 1 ||
                mods.mutation != 1 || mods.territory != 1 || mods.flowering != 1 ||
                mods.geneticDecay != 1 || mods.humidity != 0 || mods.temperature != 0 ||
                mods.isSealed || mods.isSelfLighted || mods.isSunlightSimulated ||
                mods.isAutomated || mods.isCollectingPollen || mods.biomeOverride != null;
    }

    private void addBeeStats(List<String> lines, IBee bee, IBeeHousingInventory inv) {
        EnumBeeType type = beeRoot.getType(inv.getQueen());

        if (type == EnumBeeType.PRINCESS) {
            addLine(lines, "gtbm.bee.label.breeding", (BREEDING_TIME_TICKS / 20) + "s");
        }

        if (bee.isAnalyzed()) {
            IBeeGenome genome = bee.getGenome();
            addLine(lines, "gtbm.bee.label.production",
                    String.format("%.0f%%", 100F * modifiers.production * genome.getSpeed()));
            addLine(lines, "gtbm.bee.label.flowering",
                    String.format("%.0f%%", modifiers.flowering * genome.getFlowering()));
            addLine(lines, "gtbm.bee.label.lifespan",
                    String.format("%.0f%%", 100F * modifiers.lifespan * genome.getLifespan()));
            var t = genome.getTerritory();
            addLine(lines, "gtbm.bee.label.territory", String.format("%.0f x %.0f x %.0f",
                    t.getX() * modifiers.territory, t.getY() * modifiers.territory,
                    t.getZ() * modifiers.territory));
        } else {
            addLine(lines, "gtbm.bee.label.not_analyzed", "");
        }
    }

    private static void addLine(List<String> lines, String key, String value) {
        lines.add(key + LINE_SEPARATOR + value);
    }

    // ---- Client-side: receive ----

    @Override
    public void readUpdateInfo(int id, PacketBuffer buf) {
        if (id == SYNC_ERRORS) {
            int count = buf.readVarInt();
            clientErrorIds = new ArrayList<>(count);
            for (int i = 0; i < count; i++) {
                clientErrorIds.add(buf.readShort());
            }
        } else if (id == SYNC_TOOLTIP) {
            int count = buf.readVarInt();
            clientTooltip = new ArrayList<>(count);
            for (int i = 0; i < count; i++) {
                clientTooltip.add(buf.readString(512));
            }
        }
    }

    // ---- Client-side: render icon ----

    @Override
    @SideOnly(Side.CLIENT)
    public void drawInBackground(int mouseX, int mouseY, float partialTicks, IRenderContext context) {
        Position pos = getPosition();

        if (!clientErrorIds.isEmpty()) {
            // Errors: cycle through Forestry error icons
            Minecraft mc = Minecraft.getMinecraft();
            mc.getTextureManager().bindTexture(ForestryAPI.textureManager.getGuiTextureMap());
            GlStateManager.color(1, 1, 1, 1);
            GlStateManager.enableBlend();
            GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA,
                    GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);

            int index = (int) ((mc.world.getTotalWorldTime() / 40) % clientErrorIds.size());
            IErrorState errorState = ForestryAPI.errorStateRegistry.getErrorState(clientErrorIds.get(index));
            if (errorState != null) {
                TextureAtlasSprite sprite = errorState.getSprite();
                if (sprite != null) {
                    drawAtlasSprite(pos.x, pos.y, 16, 16, sprite);
                }
            }
        } else if (!clientTooltip.isEmpty()) {
            // Working, no errors: show JEI info icon
            com.github.gtexpert.gtbm.api.gui.GTBMGuiTextures.BEE_INFO_ICON
                    .draw(pos.x, pos.y, 16, 16);
        }
    }

    @SideOnly(Side.CLIENT)
    private static void drawAtlasSprite(int x, int y, int width, int height, TextureAtlasSprite sprite) {
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();
        buffer.begin(7, DefaultVertexFormats.POSITION_TEX);
        buffer.pos(x, y + height, 0).tex(sprite.getMinU(), sprite.getMaxV()).endVertex();
        buffer.pos(x + width, y + height, 0).tex(sprite.getMaxU(), sprite.getMaxV()).endVertex();
        buffer.pos(x + width, y, 0).tex(sprite.getMaxU(), sprite.getMinV()).endVertex();
        buffer.pos(x, y, 0).tex(sprite.getMinU(), sprite.getMinV()).endVertex();
        tessellator.draw();
    }

    // ---- Client-side: render tooltip ----

    @Override
    @SideOnly(Side.CLIENT)
    public void drawInForeground(int mouseX, int mouseY) {
        if (!isMouseOverElement(mouseX, mouseY) || clientTooltip.isEmpty()) return;

        List<String> tooltip = new ArrayList<>(clientTooltip.size());
        for (String line : clientTooltip) {
            int tab = line.indexOf(LINE_SEPARATOR);
            if (tab >= 0) {
                String key = line.substring(0, tab);
                String value = line.substring(tab + 1);
                if (key.contains("temperature") || key.contains("humidity")) {
                    tooltip.add(I18n.format(key, I18n.format("for.gui." + value)));
                } else {
                    tooltip.add(I18n.format(key, value));
                }
            } else if (line.startsWith(TextFormatting.RED.toString())) {
                tooltip.add(TextFormatting.RED + I18n.format(
                        line.substring(TextFormatting.RED.toString().length())));
            } else {
                tooltip.add(line);
            }
        }

        if (!tooltip.isEmpty()) {
            drawHoveringText(ItemStack.EMPTY, tooltip, 200, mouseX, mouseY);
        }
    }
}
