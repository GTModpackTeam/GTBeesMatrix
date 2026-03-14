package com.github.gtexpert.gtbm.integration.gendustry.metatileentities;

import java.util.Collections;
import java.util.List;
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
import net.minecraftforge.items.ItemStackHandler;

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

import forestry.api.apiculture.*;
import forestry.api.core.*;
import forestry.api.genetics.IIndividual;
import forestry.core.errors.ErrorLogic;

public class MetaTileEntityIndustrialApiary extends GTBMSimpleMachineMetaTileEntity
                                            implements IBeeHousing, IBeeModifier, IBeeListener, IBeeHousingInventory {

    private static final int UPGRADE_SLOT_COUNT = 4;
    private static final int OUTPUT_SLOT_COUNT = 9;

    private final ApiaryModifiers modifiers = new ApiaryModifiers();
    private final IErrorLogic errorLogic = new ErrorLogic();
    private IItemHandlerModifiable upgradeInventory;
    private GameProfile owner;

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
        // Slot 0: Queen/Princess, Slot 1: Drone
        return new NotifiableItemStackHandler(this, 2, this, false) {

            @Override
            public boolean isItemValid(int slot, @org.jetbrains.annotations.NotNull ItemStack stack) {
                IBeeRoot beeRoot = getLogic().getBeeRoot();
                if (beeRoot == null) return true; // Allow if bee system not yet initialized
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

    @Override
    protected void initializeInventory() {
        super.initializeInventory();
        this.upgradeInventory = new ItemStackHandler(UPGRADE_SLOT_COUNT) {

            @Override
            public boolean isItemValid(int slot, @org.jetbrains.annotations.NotNull ItemStack stack) {
                return !stack.isEmpty() && stack.getItem() instanceof IApiaryUpgrade;
            }
        };
    }

    @Override
    public void markDirty() {
        super.markDirty();
    }

    // ---- NBT ----

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound data) {
        super.writeToNBT(data);
        // Upgrades
        NBTTagCompound upgradeTag = new NBTTagCompound();
        for (int i = 0; i < upgradeInventory.getSlots(); i++) {
            ItemStack stack = upgradeInventory.getStackInSlot(i);
            if (!stack.isEmpty()) {
                upgradeTag.setTag("Slot" + i, stack.writeToNBT(new NBTTagCompound()));
            }
        }
        data.setTag("UpgradeInventory", upgradeTag);

        // Owner
        if (owner != null) {
            NBTTagCompound ownerTag = new NBTTagCompound();
            ownerTag.setString("Name", owner.getName());
            ownerTag.setString("UUID", owner.getId().toString());
            data.setTag("Owner", ownerTag);
        }

        return data;
    }

    @Override
    public void readFromNBT(NBTTagCompound data) {
        super.readFromNBT(data);
        // Upgrades
        if (data.hasKey("UpgradeInventory")) {
            NBTTagCompound upgradeTag = data.getCompoundTag("UpgradeInventory");
            for (int i = 0; i < upgradeInventory.getSlots(); i++) {
                String key = "Slot" + i;
                if (upgradeTag.hasKey(key)) {
                    upgradeInventory.setStackInSlot(i, new ItemStack(upgradeTag.getCompoundTag(key)));
                }
            }
        }

        // Owner
        if (data.hasKey("Owner")) {
            NBTTagCompound ownerTag = data.getCompoundTag("Owner");
            owner = new GameProfile(
                    java.util.UUID.fromString(ownerTag.getString("UUID")),
                    ownerTag.getString("Name"));
        }
    }

    // ---- GUI ----
    // Based on Gendustry Industrial Apiary layout, adapted for GT controls.
    // 176x180: Content(y=18-54) | Controls(y=76) | PlayerInv(y=98)

    private static final int Y_OFFSET = 14;

    @Override
    protected ModularUI createUI(EntityPlayer player) {
        if (owner == null) {
            owner = player.getGameProfile();
        }

        ModularUI.Builder builder = ModularUI.builder(GuiTextures.BACKGROUND, 176, 166 + Y_OFFSET);

        // Title
        builder.widget(new LabelWidget(5, 5, getMetaFullName()));

        // Queen slot (x=7, aligned with AutoOutput button)
        builder.widget(new SlotWidget(importItems, 0, 7, 18, true, true, false)
                .setBackgroundTexture(GuiTextures.SLOT));
        builder.widget(new ImageWidget(8, 19, 16, 16, GTBMGuiTextures.QUEEN_OVERLAY));

        // Drone slot (y=36, aligned with upgrade and output middle row)
        builder.widget(new SlotWidget(importItems, 1, 7, 36, true, true, false)
                .setBackgroundTexture(GuiTextures.SLOT));
        builder.widget(new ImageWidget(8, 37, 16, 16, GTBMGuiTextures.DRONE_OVERLAY));

        // Progress bar (centered above upgrade slots)
        builder.widget(new ProgressWidget(getLogic()::getBeeProgress, 60, 18, 20, 20,
                GuiTextures.PROGRESS_BAR_ARROW, ProgressWidget.MoveType.HORIZONTAL));

        // Upgrade slots 1x4 (y=36, same row as drone and output middle)
        for (int i = 0; i < UPGRADE_SLOT_COUNT; i++) {
            builder.widget(new SlotWidget(upgradeInventory, i, 34 + i * 18, 36, true, true, false)
                    .setBackgroundTexture(GuiTextures.SLOT));
            builder.widget(new ImageWidget(35 + i * 18, 37, 16, 16, GTBMGuiTextures.UPGRADE_OVERLAY));
        }

        // Output slots 3x3 (right edge aligned with logo x=169)
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                builder.widget(new SlotWidget(exportItems, j + i * 3, 115 + j * 18, 18 + i * 18,
                        true, false, false)
                                .setBackgroundTexture(GuiTextures.SLOT));
            }
        }

        // Energy indicator (above charger)
        builder.widget(new ImageWidget(79, 42 + Y_OFFSET, 18, 18, GuiTextures.INDICATOR_NO_ENERGY)
                .setIgnoreColor(true)
                .setPredicate(workable::isHasNotEnoughEnergy));

        // Bottom row (GT default positions + Y_OFFSET)
        // Auto output items button
        builder.widget(new ToggleButtonWidget(7, 62 + Y_OFFSET, 18, 18,
                GuiTextures.BUTTON_ITEM_OUTPUT, this::isAutoOutputItems, this::setAutoOutputItems)
                        .setTooltipText("gregtech.gui.item_auto_output.tooltip")
                        .shouldUseBaseBackground());

        // Overclock button
        builder.widget(new CycleButtonWidget(25, 62 + Y_OFFSET, 18, 18,
                workable.getAvailableOverclockingTiers(), workable::getOverclockTier, workable::setOverclockTier)
                        .setTooltipHoverString("gregtech.gui.overclock.description")
                        .setButtonTexture(GuiTextures.BUTTON_OVERCLOCK));

        // Charger slot
        builder.widget(new SlotWidget(chargerInventory, 0, 79, 62 + Y_OFFSET, true, true, false)
                .setBackgroundTexture(GuiTextures.SLOT, GuiTextures.CHARGER_OVERLAY)
                .setTooltipText("gregtech.gui.charger_slot.tooltip",
                        GTValues.VNF[getTier()], GTValues.VNF[getTier()]));

        // Logo
        builder.widget(new ImageWidget(152, 63 + Y_OFFSET, 17, 17,
                GTValues.XMAS.get() ? GTBMGuiTextures.GTBM_LOGO_XMAS : GTBMGuiTextures.GTBM_LOGO)
                        .setIgnoreColor(true));

        // Player inventory
        builder.bindPlayerInventory(player.inventory, GuiTextures.SLOT, Y_OFFSET);

        return builder.build(getHolder(), player);
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World player, List<String> tooltip, boolean advanced) {
        super.addInformation(stack, player, tooltip, advanced);
    }

    // ---- IBeeHousing ----

    @Override
    public Iterable<IBeeModifier> getBeeModifiers() {
        return Collections.singletonList(this);
    }

    @Override
    public Iterable<IBeeListener> getBeeListeners() {
        return Collections.singletonList(this);
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
        if (getWorld() == null) return 0;
        return getWorld().getLightFromNeighbors(getPos().offset(EnumFacing.UP));
    }

    @Override
    public boolean canBlockSeeTheSky() {
        if (getWorld() == null) return false;
        return getWorld().canBlockSeeSky(getPos().offset(EnumFacing.UP, 2));
    }

    @Override
    public boolean isRaining() {
        if (getWorld() == null) return false;
        return getWorld().isRainingAt(getPos().up());
    }

    @Override
    @Nullable
    public GameProfile getOwner() {
        return owner;
    }

    @Override
    public Vec3d getBeeFXCoordinates() {
        BlockPos pos = getPos();
        return new Vec3d(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5);
    }

    // ---- IHousing ----

    @Override
    public BlockPos getCoordinates() {
        return getPos();
    }

    // ---- ILocatable ----

    @Override
    public World getWorldObj() {
        return getWorld();
    }

    // ---- IClimateProvider ----

    private Biome getModifiedBiome() {
        if (modifiers != null && modifiers.biomeOverride != null) {
            return modifiers.biomeOverride;
        }
        if (getWorld() == null) return null;
        return getWorld().getBiome(getPos());
    }

    @Override
    public Biome getBiome() {
        Biome biome = getModifiedBiome();
        return biome != null ? biome : getWorld().getBiome(getPos());
    }

    @Override
    public EnumTemperature getTemperature() {
        Biome biome = getModifiedBiome();
        if (biome == null) return EnumTemperature.NORMAL;
        if (BiomeHelper.isBiomeHellish(biome)) {
            return EnumTemperature.HELLISH;
        }
        float temp = modifiers != null ? modifiers.temperature : 0;
        return EnumTemperature.getFromValue(biome.getTemperature(getPos()) + temp);
    }

    @Override
    public EnumHumidity getHumidity() {
        Biome biome = getModifiedBiome();
        if (biome == null) return EnumHumidity.NORMAL;
        float humidity = modifiers != null ? modifiers.humidity : 0;
        return EnumHumidity.getFromValue(biome.getRainfall() + humidity);
    }

    // ---- IErrorLogicSource ----

    @Override
    public IErrorLogic getErrorLogic() {
        return errorLogic;
    }

    // ---- IBeeModifier ----

    @Override
    public float getTerritoryModifier(IBeeGenome genome, float currentModifier) {
        return Math.min(modifiers.territory, 5);
    }

    @Override
    public float getMutationModifier(IBeeGenome genome, IBeeGenome mate, float currentModifier) {
        return modifiers.mutation;
    }

    @Override
    public float getLifespanModifier(IBeeGenome genome, @Nullable IBeeGenome mate, float currentModifier) {
        return modifiers.lifespan;
    }

    @Override
    public float getProductionModifier(IBeeGenome genome, float currentModifier) {
        return modifiers.production;
    }

    @Override
    public float getFloweringModifier(IBeeGenome genome, float currentModifier) {
        return modifiers.flowering;
    }

    @Override
    public float getGeneticDecay(IBeeGenome genome, float currentModifier) {
        return modifiers.geneticDecay;
    }

    @Override
    public boolean isSealed() {
        return modifiers.isSealed;
    }

    @Override
    public boolean isSelfLighted() {
        return modifiers.isSelfLighted;
    }

    @Override
    public boolean isSunlightSimulated() {
        return modifiers.isSunlightSimulated;
    }

    @Override
    public boolean isHellish() {
        Biome biome = getModifiedBiome();
        return biome != null && BiomeHelper.isBiomeHellish(biome);
    }

    // ---- IBeeListener ----

    @Override
    public void onQueenDeath() {
        if (modifiers.isAutomated) {
            getLogic().setNeedsMovePrincess();
        }
    }

    @Override
    public void wearOutEquipment(int amount) {
        // No-op: Industrial Apiary does not wear out
    }

    @Override
    public boolean onPollenRetrieved(IIndividual pollen) {
        if (!modifiers.isCollectingPollen) return false;
        ItemStack stack = pollen.getGenome().getSpeciesRoot()
                .getMemberStack(pollen, forestry.api.arboriculture.EnumGermlingType.POLLEN);
        return addProduct(stack, true);
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
        if (product.isEmpty()) return true;

        ItemStack remaining = product.copy();
        IBeeRoot beeRoot = getLogic().getBeeRoot();

        // If automated, try to put bees back into bee slots first
        if (modifiers.isAutomated && beeRoot != null && beeRoot.isMember(remaining)) {
            if (beeRoot.isMember(remaining, EnumBeeType.PRINCESS) || beeRoot.isMember(remaining, EnumBeeType.QUEEN)) {
                if (getQueen().isEmpty()) {
                    setQueen(remaining);
                    return true;
                }
            } else if (beeRoot.isMember(remaining, EnumBeeType.DRONE)) {
                ItemStack droneSlot = getDrone();
                if (droneSlot.isEmpty()) {
                    setDrone(remaining);
                    return true;
                } else if (ItemStack.areItemsEqual(droneSlot, remaining) &&
                        ItemStack.areItemStackTagsEqual(droneSlot, remaining) &&
                        droneSlot.getCount() + remaining.getCount() <= droneSlot.getMaxStackSize()) {
                            droneSlot.grow(remaining.getCount());
                            return true;
                        }
            }
        }

        // Try to add to output slots
        for (int i = 0; i < exportItems.getSlots(); i++) {
            ItemStack slotStack = exportItems.getStackInSlot(i);
            if (slotStack.isEmpty()) {
                exportItems.setStackInSlot(i, remaining);
                return true;
            }
            if (ItemStack.areItemsEqual(slotStack, remaining) &&
                    ItemStack.areItemStackTagsEqual(slotStack, remaining)) {
                int space = slotStack.getMaxStackSize() - slotStack.getCount();
                if (space >= remaining.getCount()) {
                    slotStack.grow(remaining.getCount());
                    return true;
                } else if (space > 0) {
                    slotStack.grow(space);
                    remaining.shrink(space);
                }
            }
        }

        return remaining.isEmpty();
    }

    // ---- Accessors ----

    protected IndustrialApiaryLogic getLogic() {
        return (IndustrialApiaryLogic) workable;
    }

    public IItemHandlerModifiable getUpgradeInventory() {
        return upgradeInventory;
    }

    public IItemHandlerModifiable getExportItems() {
        return exportItems;
    }

    public ApiaryModifiers getModifiers() {
        return modifiers;
    }
}
