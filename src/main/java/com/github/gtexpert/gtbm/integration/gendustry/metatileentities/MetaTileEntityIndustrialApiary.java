package com.github.gtexpert.gtbm.integration.gendustry.metatileentities;

import java.util.Collections;
import java.util.function.Function;

import javax.annotation.Nullable;

import net.bdew.gendustry.api.ApiaryModifiers;
import net.bdew.gendustry.api.items.IApiaryUpgrade;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.items.IItemHandlerModifiable;

import com.mojang.authlib.GameProfile;

import gregtech.api.GTValues;
import gregtech.api.capability.impl.NotifiableItemStackHandler;
import gregtech.api.capability.impl.RecipeLogicEnergy;
import gregtech.api.gui.GuiTextures;
import gregtech.api.gui.ModularUI;
import gregtech.api.gui.widgets.*;
import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.metatileentity.interfaces.IGregTechTileEntity;
import gregtech.api.recipes.RecipeMap;
import gregtech.client.renderer.ICubeRenderer;

import com.github.gtexpert.gtbm.api.gui.GTBMGuiTextures;
import com.github.gtexpert.gtbm.common.metatileentities.GTBMSimpleMachineMetaTileEntity;
import com.github.gtexpert.gtbm.integration.forestry.util.BeeProductHelper;
import com.github.gtexpert.gtbm.integration.gendustry.util.ApiaryModifierBridge;
import com.github.gtexpert.gtbm.integration.gendustry.util.BeeClimateHelper;
import com.github.gtexpert.gtbm.integration.gendustry.util.WidgetBeeStatus;

import forestry.api.apiculture.*;
import forestry.api.core.*;
import forestry.api.genetics.IIndividual;
import forestry.core.errors.ErrorLogic;

public class MetaTileEntityIndustrialApiary extends GTBMSimpleMachineMetaTileEntity
                                            implements IBeeHousing, IBeeHousingInventory {

    private static final int UPGRADE_SLOTS_BASE = 4;
    private static final int UPGRADE_SLOTS_EXTENDED = 8;
    private static final int OUTPUT_SLOT_COUNT = 9;
    private static final int Y_OFFSET = 13;
    private static final int BEE_LOGIC_SYNC_ID = 1000;

    private final ApiaryModifiers modifiers = new ApiaryModifiers();
    private final IErrorLogic errorLogic = new ErrorLogic();
    private final BeeClimateHelper climateHelper = new BeeClimateHelper(modifiers);
    private final IBeeModifier beeModifier = new ApiaryModifierBridge(modifiers) {

        @Override
        public boolean isHellish() {
            return climateHelper.isHellish(getWorld(), getPos());
        }
    };
    private final IBeeListener beeListener = new DefaultBeeListener() {

        @Override
        public void onQueenDeath() {
            if (autoBreeding) {
                getLogic().setNeedsMovePrincess();
            }
        }

        @Override
        public boolean onPollenRetrieved(IIndividual pollen) {
            if (!modifiers.isCollectingPollen) return false;
            ItemStack stack = pollen.getGenome().getSpeciesRoot()
                    .getMemberStack(pollen, forestry.api.arboriculture.EnumGermlingType.POLLEN);
            return addProduct(stack, true);
        }
    };
    private IItemHandlerModifiable upgradeInventory;
    private GameProfile owner;
    private boolean autoBreeding = false;

    public MetaTileEntityIndustrialApiary(ResourceLocation metaTileEntityId, RecipeMap<?> recipeMap,
                                          ICubeRenderer renderer, int tier, boolean hasFrontFacing,
                                          Function<Integer, Integer> tankScalingFunction) {
        super(metaTileEntityId, recipeMap, renderer, tier, hasFrontFacing, tankScalingFunction);
    }

    @Override
    public MetaTileEntity createMetaTileEntity(IGregTechTileEntity tileEntity) {
        return new MetaTileEntityIndustrialApiary(metaTileEntityId, workable.getRecipeMap(), renderer, getTier(),
                hasFrontFacing(), getTankScalingFunction());
    }

    @Override
    protected RecipeLogicEnergy createWorkable(RecipeMap<?> recipeMap) {
        return new IndustrialApiaryLogic(this, recipeMap, () -> energyContainer);
    }

    // ---- Inventory ----

    @Override
    protected IItemHandlerModifiable createImportItemHandler() {
        return new NotifiableItemStackHandler(this, 2, this, false) {

            @Override
            public boolean isItemValid(int slot, @org.jetbrains.annotations.NotNull ItemStack stack) {
                IBeeRoot beeRoot = getLogic().getBeeRoot();
                if (beeRoot == null) return true;
                if (slot == 0) {
                    return beeRoot.isMember(stack, EnumBeeType.QUEEN) || beeRoot.isMember(stack, EnumBeeType.PRINCESS);
                }
                if (slot == 1) {
                    return beeRoot.isMember(stack, EnumBeeType.DRONE);
                }
                return false;
            }
        };
    }

    @Override
    protected IItemHandlerModifiable createExportItemHandler() {
        return new NotifiableItemStackHandler(this, OUTPUT_SLOT_COUNT, this, true);
    }

    private int getUpgradeSlotCount() {
        return getTier() >= GTValues.LuV ? UPGRADE_SLOTS_EXTENDED : UPGRADE_SLOTS_BASE;
    }

    @Override
    protected void initializeInventory() {
        super.initializeInventory();
        this.upgradeInventory = new NotifiableItemStackHandler(this, getUpgradeSlotCount(), this, false) {

            @Override
            public boolean isItemValid(int slot, @org.jetbrains.annotations.NotNull ItemStack stack) {
                if (stack.isEmpty() || !(stack.getItem() instanceof IApiaryUpgrade)) return false;
                return getMaxAdditionalUpgrades(stack) >= stack.getCount();
            }

            @Override
            public int getSlotLimit(int slot) {
                return 64;
            }
        };
    }

    public int getMaxAdditionalUpgrades(ItemStack stack) {
        if (stack.isEmpty() || !(stack.getItem() instanceof IApiaryUpgrade)) return 0;
        IApiaryUpgrade upgrade = (IApiaryUpgrade) stack.getItem();
        long thisId = upgrade.getStackingId(stack);
        int existing = 0;
        for (int i = 0; i < upgradeInventory.getSlots(); i++) {
            ItemStack slotStack = upgradeInventory.getStackInSlot(i);
            if (!slotStack.isEmpty() && slotStack.getItem() instanceof IApiaryUpgrade) {
                if (((IApiaryUpgrade) slotStack.getItem()).getStackingId(slotStack) == thisId) {
                    existing += slotStack.getCount();
                }
            }
        }
        return upgrade.getMaxNumber(stack) - existing;
    }

    // ---- NBT ----

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound data) {
        super.writeToNBT(data);
        NBTTagCompound upgradeTag = new NBTTagCompound();
        for (int i = 0; i < upgradeInventory.getSlots(); i++) {
            ItemStack stack = upgradeInventory.getStackInSlot(i);
            if (!stack.isEmpty()) {
                upgradeTag.setTag("Slot" + i, stack.writeToNBT(new NBTTagCompound()));
            }
        }
        data.setTag("UpgradeInventory", upgradeTag);

        if (owner != null) {
            NBTTagCompound ownerTag = new NBTTagCompound();
            ownerTag.setString("Name", owner.getName());
            ownerTag.setString("UUID", owner.getId().toString());
            data.setTag("Owner", ownerTag);
        }

        data.setBoolean("AutoBreeding", autoBreeding);
        return data;
    }

    @Override
    public void readFromNBT(NBTTagCompound data) {
        super.readFromNBT(data);
        if (data.hasKey("UpgradeInventory")) {
            NBTTagCompound upgradeTag = data.getCompoundTag("UpgradeInventory");
            for (int i = 0; i < upgradeInventory.getSlots(); i++) {
                String key = "Slot" + i;
                if (upgradeTag.hasKey(key)) {
                    upgradeInventory.setStackInSlot(i, new ItemStack(upgradeTag.getCompoundTag(key)));
                }
            }
        }
        if (data.hasKey("Owner")) {
            NBTTagCompound ownerTag = data.getCompoundTag("Owner");
            owner = new GameProfile(
                    java.util.UUID.fromString(ownerTag.getString("UUID")),
                    ownerTag.getString("Name"));
        }
        if (data.hasKey("AutoBreeding")) {
            autoBreeding = data.getBoolean("AutoBreeding");
        }
    }

    // ---- GUI ----

    @Override
    protected ModularUI createUI(EntityPlayer player) {
        if (owner == null) {
            owner = player.getGameProfile();
        }
        IBeekeepingLogic logic = getBeekeepingLogic();
        if (logic != null && !getWorld().isRemote) {
            logic.clearCachedValues();
        }

        ModularUI.Builder builder = ModularUI.builder(GuiTextures.BACKGROUND, 176, 166 + Y_OFFSET);
        builder.widget(new LabelWidget(5, 5, getMetaFullName()));

        builder.widget(new SlotWidget(importItems, 0, 7, 18, true, true, true)
                .setBackgroundTexture(GuiTextures.SLOT));
        builder.widget(new ImageWidget(8, 19, 16, 16, GTBMGuiTextures.QUEEN_OVERLAY));
        builder.widget(new SlotWidget(importItems, 1, 7, 36, true, true, true)
                .setBackgroundTexture(GuiTextures.SLOT));
        builder.widget(new ImageWidget(8, 37, 16, 16, GTBMGuiTextures.DRONE_OVERLAY));

        builder.widget(new WidgetBeeStatus(8, 56, this, getLogic().getBeeRoot(), getModifiers(),
                getLogic()::getEUPerTick));

        builder.widget(new ProgressWidget(getLogic()::getBeeProgress, 60, 16, 20, 20,
                GuiTextures.PROGRESS_BAR_ARROW, ProgressWidget.MoveType.HORIZONTAL));

        int upgradeSlots = getUpgradeSlotCount();
        for (int i = 0; i < upgradeSlots; i++) {
            int col = i % 4;
            int row = i / 4;
            builder.widget(new SlotWidget(upgradeInventory, i, 34 + col * 18, 36 + row * 18,
                    true, true, true).setBackgroundTexture(GuiTextures.SLOT));
            builder.widget(new ImageWidget(35 + col * 18, 37 + row * 18, 16, 16,
                    GTBMGuiTextures.UPGRADE_OVERLAY));
        }

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                builder.widget(new SlotWidget(exportItems, j + i * 3, 116 + j * 18, 18 + i * 18,
                        true, false, false).setBackgroundTexture(GuiTextures.SLOT));
            }
        }

        builder.widget(new ImageWidget(79, 42 + Y_OFFSET, 18, 18, GuiTextures.INDICATOR_NO_ENERGY)
                .setIgnoreColor(true).setPredicate(workable::isHasNotEnoughEnergy));
        builder.widget(new ToggleButtonWidget(7, 62 + Y_OFFSET, 18, 18,
                GuiTextures.BUTTON_ITEM_OUTPUT, this::isAutoOutputItems, this::setAutoOutputItems)
                        .setTooltipText("gregtech.gui.item_auto_output.tooltip").shouldUseBaseBackground());
        builder.widget(new CycleButtonWidget(25, 62 + Y_OFFSET, 18, 18,
                workable.getAvailableOverclockingTiers(), workable::getOverclockTier, workable::setOverclockTier)
                        .setTooltipHoverString("gregtech.gui.overclock.description")
                        .setButtonTexture(GuiTextures.BUTTON_OVERCLOCK));
        builder.widget(new ToggleButtonWidget(43, 62 + Y_OFFSET, 18, 18,
                GuiTextures.BUTTON_ITEM_OUTPUT, this::isAutoBreeding, this::setAutoBreeding)
                        .setTooltipText("gtbm.gui.auto_breeding.tooltip").shouldUseBaseBackground());
        builder.widget(new SlotWidget(chargerInventory, 0, 79, 62 + Y_OFFSET, true, true, true)
                .setBackgroundTexture(GuiTextures.SLOT, GuiTextures.CHARGER_OVERLAY)
                .setTooltipText("gregtech.gui.charger_slot.tooltip",
                        GTValues.VNF[getTier()], GTValues.VNF[getTier()]));
        builder.widget(new ImageWidget(152, 63 + Y_OFFSET, 17, 17,
                GTValues.XMAS.get() ? GTBMGuiTextures.GTBM_LOGO_XMAS : GTBMGuiTextures.GTBM_LOGO)
                        .setIgnoreColor(true));
        builder.bindPlayerInventory(player.inventory, GuiTextures.SLOT, Y_OFFSET);

        return builder.build(getHolder(), player);
    }

    // ---- IBeeHousing ----

    @Override
    public Iterable<IBeeModifier> getBeeModifiers() {
        return Collections.singletonList(beeModifier);
    }

    @Override
    public Iterable<IBeeListener> getBeeListeners() {
        return Collections.singletonList(beeListener);
    }

    @Override
    public IBeeHousingInventory getBeeInventory() {
        return this;
    }

    @Override
    public IBeekeepingLogic getBeekeepingLogic() {
        return getLogic().getBeekeepingLogic();
    }

    @Override
    public int getBlockLightValue() {
        return getWorld() != null ? getWorld().getLightFromNeighbors(getPos().offset(EnumFacing.UP)) : 0;
    }

    @Override
    public boolean canBlockSeeTheSky() {
        return getWorld() != null && getWorld().canBlockSeeSky(getPos().offset(EnumFacing.UP, 2));
    }

    @Override
    public boolean isRaining() {
        return getWorld() != null && getWorld().isRainingAt(getPos().up());
    }

    @Override
    @Nullable
    public GameProfile getOwner() {
        return owner;
    }

    @Override
    public Vec3d getBeeFXCoordinates() {
        return new Vec3d(getPos().getX() + 0.5, getPos().getY() + 0.5, getPos().getZ() + 0.5);
    }

    @Override
    public BlockPos getCoordinates() {
        return getPos();
    }

    @Override
    public World getWorldObj() {
        return getWorld();
    }

    // ---- IClimateProvider ----

    @Override
    public Biome getBiome() {
        return climateHelper.getBiome(getWorld(), getPos());
    }

    @Override
    public EnumTemperature getTemperature() {
        return climateHelper.getTemperature(getWorld(), getPos());
    }

    @Override
    public EnumHumidity getHumidity() {
        return climateHelper.getHumidity(getWorld(), getPos());
    }

    @Override
    public IErrorLogic getErrorLogic() {
        return errorLogic;
    }

    // ---- IBeeHousingInventory ----

    @Override
    public ItemStack getQueen() {
        return importItems.getStackInSlot(0);
    }

    @Override
    public ItemStack getDrone() {
        return importItems.getStackInSlot(1);
    }

    @Override
    public void setQueen(ItemStack stack) {
        importItems.setStackInSlot(0, stack);
    }

    @Override
    public void setDrone(ItemStack stack) {
        importItems.setStackInSlot(1, stack);
    }

    @Override
    public boolean addProduct(ItemStack product, boolean all) {
        return BeeProductHelper.addProduct(product, getLogic().getBeeRoot(),
                autoBreeding, modifiers.isAutomated, this, importItems, exportItems);
    }

    // ---- Bee FX sync ----

    public void syncBeeLogicToClient() {
        IBeekeepingLogic logic = getBeekeepingLogic();
        if (logic != null && getWorld() != null && !getWorld().isRemote) {
            writeCustomData(BEE_LOGIC_SYNC_ID, buf -> logic.writeData(buf));
        }
    }

    @Override
    public void writeInitialSyncData(@org.jetbrains.annotations.NotNull net.minecraft.network.PacketBuffer buf) {
        super.writeInitialSyncData(buf);
        IBeekeepingLogic logic = getBeekeepingLogic();
        if (logic != null) {
            buf.writeBoolean(true);
            logic.writeData(buf);
        } else {
            buf.writeBoolean(false);
        }
    }

    @Override
    public void receiveInitialSyncData(@org.jetbrains.annotations.NotNull net.minecraft.network.PacketBuffer buf) {
        super.receiveInitialSyncData(buf);
        if (buf.readBoolean()) {
            readBeeLogicData(buf);
        }
    }

    @Override
    public void receiveCustomData(int dataId,
                                  @org.jetbrains.annotations.NotNull net.minecraft.network.PacketBuffer buf) {
        super.receiveCustomData(dataId, buf);
        if (dataId == BEE_LOGIC_SYNC_ID) {
            readBeeLogicData(buf);
        }
    }

    private void readBeeLogicData(net.minecraft.network.PacketBuffer buf) {
        getLogic().initBeekeepingLogicClient();
        IBeekeepingLogic logic = getBeekeepingLogic();
        try {
            if (logic != null) {
                logic.readData(buf);
            } else {
                buf.readBytes(buf.readableBytes());
            }
        } catch (java.io.IOException ignored) {}
    }

    // ---- Accessors ----

    protected IndustrialApiaryLogic getLogic() {
        return (IndustrialApiaryLogic) workable;
    }

    public IItemHandlerModifiable getUpgradeInventory() {
        return upgradeInventory;
    }

    public ApiaryModifiers getModifiers() {
        return modifiers;
    }

    public boolean isAutoBreeding() {
        return autoBreeding;
    }

    public void setAutoBreeding(boolean autoBreeding) {
        this.autoBreeding = autoBreeding;
        if (autoBreeding && getQueen().isEmpty()) {
            getLogic().movePrincessToQueenSlot();
        }
    }
}
