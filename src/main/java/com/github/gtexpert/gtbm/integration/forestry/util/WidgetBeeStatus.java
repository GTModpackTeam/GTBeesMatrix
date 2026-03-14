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

    private final IBeeHousing housing;
    private final IBeeRoot beeRoot;
    private final ApiaryModifiers modifiers;
    private final IntSupplier euPerTickSupplier;

    // Client-side synced data
    private List<String> clientTooltip = new ArrayList<>();
    private List<Short> clientErrorIds = new ArrayList<>();

    // Server-side change tracking
    private int lastTooltipHash = Integer.MIN_VALUE;
    private int lastErrorHash = Integer.MIN_VALUE;

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

        // Sync error IDs (for icon display)
        IErrorLogic errorLogic = housing.getErrorLogic();
        List<Short> errorIds = new ArrayList<>();
        for (IErrorState error : errorLogic.getErrorStates()) {
            errorIds.add(error.getID());
        }
        int errorHash = errorIds.hashCode();
        if (errorHash != lastErrorHash) {
            lastErrorHash = errorHash;
            writeUpdateInfo(SYNC_ERRORS, buf -> {
                buf.writeVarInt(errorIds.size());
                for (short id : errorIds) {
                    buf.writeShort(id);
                }
            });
        }

        // Sync tooltip lines
        List<String> tooltip = buildTooltipLines();
        int tooltipHash = tooltip.hashCode();
        if (tooltipHash != lastTooltipHash) {
            lastTooltipHash = tooltipHash;
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

        // Error descriptions (red)
        IErrorLogic errorLogic = housing.getErrorLogic();
        for (IErrorState error : errorLogic.getErrorStates()) {
            lines.add(TextFormatting.RED + error.getUnlocalizedDescription());
        }

        // Energy
        lines.add("gtbm.bee.label.energy\t" + euPerTickSupplier.getAsInt());

        // Temperature & Humidity (send enum name for client-side localization)
        EnumTemperature temp = housing.getTemperature();
        EnumHumidity hum = housing.getHumidity();
        lines.add("gtbm.bee.label.temperature\t" + temp.getName().toLowerCase());
        lines.add("gtbm.bee.label.humidity\t" + hum.getName().toLowerCase());

        // Bee stats
        IBeeHousingInventory inv = housing.getBeeInventory();
        if (beeRoot != null && !inv.getQueen().isEmpty()) {
            IBee bee = beeRoot.getMember(inv.getQueen());
            if (bee != null && bee.isAnalyzed()) {
                IBeeGenome genome = bee.getGenome();
                lines.add("gtbm.bee.label.production\t" +
                        String.format("%.0f%%", 100F * modifiers.production * genome.getSpeed()));
                lines.add("gtbm.bee.label.flowering\t" +
                        String.format("%.0f%%", modifiers.flowering * genome.getFlowering()));
                lines.add("gtbm.bee.label.lifespan\t" +
                        String.format("%.0f%%", 100F * modifiers.lifespan * genome.getLifespan()));
                var t = genome.getTerritory();
                lines.add("gtbm.bee.label.territory\t" + String.format("%.0f x %.0f x %.0f",
                        t.getX() * modifiers.territory, t.getY() * modifiers.territory,
                        t.getZ() * modifiers.territory));
            }
        }

        return lines;
    }

    // ---- Client-side: receive ----

    @Override
    public void readUpdateInfo(int id, PacketBuffer buf) {
        if (id == SYNC_ERRORS) {
            int count = buf.readVarInt();
            clientErrorIds = new ArrayList<>();
            for (int i = 0; i < count; i++) {
                clientErrorIds.add(buf.readShort());
            }
        } else if (id == SYNC_TOOLTIP) {
            int count = buf.readVarInt();
            clientTooltip = new ArrayList<>();
            for (int i = 0; i < count; i++) {
                clientTooltip.add(buf.readString(512));
            }
        }
    }

    // ---- Client-side: render icon ----

    @Override
    @SideOnly(Side.CLIENT)
    public void drawInBackground(int mouseX, int mouseY, float partialTicks, IRenderContext context) {
        if (clientErrorIds.isEmpty()) return;

        Position pos = getPosition();
        Minecraft mc = Minecraft.getMinecraft();
        mc.getTextureManager().bindTexture(ForestryAPI.textureManager.getGuiTextureMap());
        GlStateManager.color(1, 1, 1, 1);
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);

        // Cycle through error icons (same as Gendustry: 40 tick interval)
        int index = (int) ((mc.world.getTotalWorldTime() / 40) % clientErrorIds.size());
        short errorId = clientErrorIds.get(index);
        IErrorState errorState = ForestryAPI.errorStateRegistry.getErrorState(errorId);
        if (errorState != null) {
            TextureAtlasSprite sprite = errorState.getSprite();
            if (sprite != null) {
                drawAtlasSprite(pos.x, pos.y, 16, 16, sprite);
            }
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
        if (!isMouseOverElement(mouseX, mouseY)) return;
        if (clientTooltip.isEmpty()) return;

        List<String> tooltip = new ArrayList<>();
        for (String line : clientTooltip) {
            // Lines with \t are "key\tvalue" pairs for localization
            int tab = line.indexOf('\t');
            if (tab >= 0) {
                String key = line.substring(0, tab);
                String value = line.substring(tab + 1);
                // Temperature/humidity values need for.gui.* localization
                if (key.contains("temperature") || key.contains("humidity")) {
                    tooltip.add(I18n.format(key, I18n.format("for.gui." + value)));
                } else {
                    tooltip.add(I18n.format(key, value));
                }
            } else {
                // Error lines (already have TextFormatting.RED + unlocalized key)
                if (line.startsWith(TextFormatting.RED.toString())) {
                    String errorKey = line.substring(TextFormatting.RED.toString().length());
                    tooltip.add(TextFormatting.RED + I18n.format(errorKey));
                } else {
                    tooltip.add(line);
                }
            }
        }

        if (!tooltip.isEmpty()) {
            drawHoveringText(ItemStack.EMPTY, tooltip, 200, mouseX, mouseY);
        }
    }
}
