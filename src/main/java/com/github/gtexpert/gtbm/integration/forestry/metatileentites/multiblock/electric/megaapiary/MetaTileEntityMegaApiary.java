package com.github.gtexpert.gtbm.integration.forestry.metatileentites.multiblock.electric.megaapiary;

import codechicken.lib.render.CCRenderState;
import codechicken.lib.render.pipeline.IVertexOperation;
import codechicken.lib.vec.Matrix4;

import forestry.api.apiculture.BeeManager;
import forestry.api.apiculture.IAlleleBeeSpecies;
import forestry.api.apiculture.IBeeGenome;

import forestry.api.apiculture.IBeekeepingMode;
import forestry.apiculture.ModuleApiculture;
import forestry.apiculture.genetics.BeeDefinition;
import forestry.apiculture.genetics.BeeGenome;

import gregtech.api.GTValues;
import gregtech.api.capability.GregtechDataCodes;
import gregtech.api.capability.GregtechTileCapabilities;
import gregtech.api.capability.IControllable;
import gregtech.api.capability.IEnergyContainer;
import gregtech.api.capability.impl.EnergyContainerList;
import gregtech.api.capability.impl.ItemHandlerList;
import gregtech.api.items.itemhandlers.GTItemStackHandler;
import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.metatileentity.interfaces.IGregTechTileEntity;
import gregtech.api.metatileentity.multiblock.IMultiblockPart;
import gregtech.api.metatileentity.multiblock.MultiblockAbility;
import gregtech.api.metatileentity.multiblock.MultiblockDisplayText;
import gregtech.api.metatileentity.multiblock.MultiblockWithDisplayBase;

import gregtech.api.pattern.BlockPattern;
import gregtech.api.pattern.FactoryBlockPattern;
import gregtech.api.pattern.PatternMatchContext;
import gregtech.api.util.GTTransferUtils;
import gregtech.api.util.GTUtility;
import gregtech.api.util.ItemStackHashStrategy;
import gregtech.api.util.TextComponentUtil;
import gregtech.api.util.TextFormattingUtil;
import gregtech.client.renderer.ICubeRenderer;

import gregtech.client.renderer.texture.Textures;
import gregtech.common.blocks.BlockMetalCasing;
import gregtech.common.blocks.MetaBlocks;

import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;

import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;

import net.minecraftforge.common.capabilities.Capability;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MetaTileEntityMegaApiary extends MultiblockWithDisplayBase implements IControllable {

    private boolean isWorkingEnabled = true;
    private boolean isWorking = false;

    private static final int PROCESS_TIME = 100;
    private int progressTicks = 0;
    private int inputSize = 0;

    private long consumption;

    private static final float MAX_PRODUCTION_MODIFIER_FROM_UPGRADES = (float) (4 * Math.pow(1.2, 8));

    private final List<ItemStack> queenStacks = new ArrayList<>();
    private final List<ItemStack> energyCalculationStacks = new ArrayList<>();
    private NonNullList<ItemStack> products = NonNullList.create();
    private NonNullList<ItemStack> merged = NonNullList.create();

    private EnergyContainerList energyContainer;

    private static IBeekeepingMode mode;

    public MetaTileEntityMegaApiary(ResourceLocation metaTileEntityId) {
        super(metaTileEntityId);
    }

    @Override
    public MetaTileEntity createMetaTileEntity(IGregTechTileEntity tileEntity) {
        return new MetaTileEntityMegaApiary(metaTileEntityId);
    }

    @Override
    protected void formStructure(PatternMatchContext context) {
        super.formStructure(context);
        List<IEnergyContainer> inputs = new ArrayList<>();
        inputs.addAll(getAbilities(MultiblockAbility.INPUT_ENERGY));
        inputs.addAll(getAbilities(MultiblockAbility.SUBSTATION_INPUT_ENERGY));
        inputs.addAll(getAbilities(MultiblockAbility.INPUT_LASER));
        this.energyContainer = new EnergyContainerList(inputs);

        this.importItems = new ItemHandlerList(getAbilities(MultiblockAbility.IMPORT_ITEMS));
        this.exportItems = new ItemHandlerList(getAbilities(MultiblockAbility.EXPORT_ITEMS));
    }

    @Override
    public void invalidateStructure() {
        this.energyContainer = null;
        this.importItems = new GTItemStackHandler(this, 0);
        this.exportItems = new GTItemStackHandler(this, 0);
        super.invalidateStructure();
    }

    @Override
    protected void updateFormedValid() {

    }

    @Override
    protected @NotNull BlockPattern createStructurePattern() {
        return FactoryBlockPattern.start()
                .aisle("CCCCC", "CCCCC", "CCCCC", "CCCCC", "CCCCC")
                .aisle("CCCCC", "C###C", "C###C", "C###C", "CCCCC")
                .aisle("CCCCC", "C###C", "C###C", "C###C", "CCCCC")
                .aisle("CCCCC", "C###C", "C###C", "C###C", "CCCCC")
                .aisle("CCCCC", "CCCCC", "CCSCC", "CCCCC", "CCCCC")
                .where('C', states(MetaBlocks.METAL_CASING.getState(BlockMetalCasing.MetalCasingType.BRONZE_BRICKS))
                        .or(abilities(MultiblockAbility.IMPORT_ITEMS).setMinGlobalLimited(1, 2).setMaxGlobalLimited(5))
                        .or(abilities(MultiblockAbility.EXPORT_ITEMS).setMinGlobalLimited(1, 2).setMaxGlobalLimited(5))
                        .or(abilities(MultiblockAbility.IMPORT_FLUIDS).setMaxGlobalLimited(5, 1))
                        .or(abilities(MultiblockAbility.MAINTENANCE_HATCH).setExactLimit(1))
                        .or(abilities(MultiblockAbility.INPUT_ENERGY).setMaxGlobalLimited(4, 1))
                        .or(abilities(MultiblockAbility.SUBSTATION_INPUT_ENERGY).setMaxGlobalLimited(4, 1))
                        .or(abilities(MultiblockAbility.INPUT_LASER).setMaxGlobalLimited(4, 1)))
                .where('S', selfPredicate())
                .where('#', any())
                .build();
    }

    @Override
    public ICubeRenderer getBaseTexture(IMultiblockPart sourcePart) {
        return Textures.BRONZE_PLATED_BRICKS;
    }

    @SideOnly(Side.CLIENT)
    @NotNull
    @Override
    protected ICubeRenderer getFrontOverlay() {
        return Textures.ASSEMBLER_OVERLAY;
    }

    @Override
    public void renderMetaTileEntity(CCRenderState renderState, Matrix4 translation, IVertexOperation[] pipeline) {
        super.renderMetaTileEntity(renderState, translation, pipeline);
        getFrontOverlay().renderSided(getFrontFacing(), renderState, translation, pipeline);
    }

    @Override
    public void update() {
        super.update();
        if (this.getWorld().isRemote || energyContainer == null || !isWorkingEnabled) {
            return;
        }

        if (!canWork()) {
            isWorking = false;
            progressTicks = 0;
            consumeEnergy(getCurrentConsumption());
            resetConsumption();
            return;
        }

        if (!isWorking) {
            if (canStartProcess()) {
                isWorking = true;
                progressTicks = 0;
            }
            consumeEnergy(getCurrentConsumption());
            return;
        }

        progressTicks++;

        if (progressTicks >= PROCESS_TIME) {
            progressTicks = 0;
            outputProducts();

            if (!canStartProcess()) {
                isWorking = false;
            }
        }
        consumeEnergy(getCurrentConsumption());
    }

    private void setConsumption() {
        long base = GTValues.V[GTValues.LuV];
        this.consumption = base * this.inputSize;
    }

    private void resetConsumption() {
        energyCalculationStacks.clear();
        for (int i = 0; i < importItems.getSlots(); i++) {
            ItemStack stack = importItems.getStackInSlot(i);
            if (isQueen(stack)) {
                energyCalculationStacks.add(stack);
            }
        }
        this.inputSize = energyCalculationStacks.size();
        setConsumption();
        energyCalculationStacks.clear();
    }

    public long getMaxVoltage() {
        long highestVoltage = energyContainer.getHighestInputVoltage();
        if (energyContainer.getNumHighestInputContainers() > 1) {
            int tier = GTUtility.getTierByVoltage(highestVoltage);
            return GTValues.V[Math.min(tier + 1, GTValues.MAX)];
        } else {
            return highestVoltage;
        }
    }

    private int getMaxTier() {
        return GTUtility.getTierByVoltage(getMaxVoltage());
    }

    private long getCurrentConsumption() {
        return this.consumption;
    }

    // ==Process==
    @SuppressWarnings("ResultIsAlwaysInverted")
    private boolean canWork() {
        setConsumption();
        return energyContainer.getEnergyStored() >= getCurrentConsumption() && energyContainer != null;
    }

    private void consumeEnergy(long energy) {
        energyContainer.removeEnergy(Math.min(energyContainer.getEnergyStored(), energy));
    }

    private boolean canStartProcess() {
        queenStacks.clear();
        collectBees();
        processProducts();
        this.inputSize = queenStacks.size();
        return !queenStacks.isEmpty();
    }

    private void collectBees() {
        for (int i = 0; i < importItems.getSlots(); i++) {
            ItemStack stack = importItems.getStackInSlot(i);
            if (isQueen(stack)) {
                queenStacks.add(stack);
            } else {
                stack = importItems.extractItem(i, importItems.getStackInSlot(i).getCount(), false);
                if (GTTransferUtils.addItemsToItemHandler(exportItems, true,
                        Collections.singletonList(stack))) {
                    GTTransferUtils.addItemsToItemHandler(exportItems, false, Collections.singletonList(stack));
                }
            }
        }
    }

    private void processProducts() {
        World world = this.getWorld();

        if (queenStacks.isEmpty()) return;

        if (mode == null) {
            mode = BeeManager.beeRoot.getBeekeepingMode(world);
        }

        for (ItemStack queen: queenStacks) {
            IBeeGenome genome;
            NBTTagCompound tag = queen.getTagCompound();
            if (tag == null || tag.isEmpty() || !tag.hasKey("Genome")) {
                genome = BeeDefinition.FOREST.getGenome();
            } else {
                genome = BeeGenome.fromNBT(tag.getCompoundTag("Genome"));
            }

            IAlleleBeeSpecies primary = genome.getPrimary();
            IAlleleBeeSpecies secondary = genome.getSecondary();

            float speed = genome.getSpeed();
            float baseModifier = mode.getBeeModifier().getProductionModifier(genome, MAX_PRODUCTION_MODIFIER_FROM_UPGRADES);

            primary.getProductChances().forEach((stack, f) -> {
                    double v = speed + f + baseModifier + baseModifier * getMaxTier();
                    while (v > 1.0F) {
                        int size = Math.min((int) v, 64);
                        stack.setCount(size);
                        products.add(stack);
                        v -= size;
                    }
            });
            secondary.getProductChances().forEach((stack, f) -> {
                double v = speed + f + baseModifier + baseModifier * getMaxTier();
                while (v > 1.0F) {
                    int size = Math.min((int) v, 64);
                    stack.setCount(size);
                    products.add(stack);
                    v -= size;
                }
            });

            primary.getSpecialtyChances().forEach((stack, f) -> {
                double v = speed + f + baseModifier + baseModifier * getMaxTier();
                while (v > 1.0F) {
                    int size = Math.min((int) v, 64);
                    stack.setCount(size);
                    products.add(stack);
                    v -= size;
                }
            });
        }

        mergeProducts();
    }

    private void mergeProducts() {
        for (ItemStack stack : products) {
            if (stack.isEmpty()) {
                continue;
            }

            boolean found = false;

            for (ItemStack existing : merged) {
                if (ItemStackHashStrategy.comparingAllButCount().equals(existing, stack)) {
                    existing.grow(stack.getCount());
                    found = true;
                    break;
                }
            }

            if (!found) {
                merged.add(stack.copy());
            }
        }
    }

    private void outputProducts() {
        for (int i = products.size() - 1; i >= 0; i--) {
            ItemStack output = products.get(i);
            if (GTTransferUtils.addItemsToItemHandler(exportItems, true, Collections.singletonList(output))) {
                GTTransferUtils.addItemsToItemHandler(exportItems, false, Collections.singletonList(output));
            }
        }
        products.clear();
        merged.clear();
    }

    private boolean isQueen(ItemStack stack) {
        return stack.isItemEqual(ModuleApiculture.getItems().beeQueenGE.getItemStack());
    }

    // ==Display Text==
    @Override
    protected void addDisplayText(List<ITextComponent> textList) {
        MultiblockDisplayText.Builder builder = MultiblockDisplayText.builder(textList, isStructureFormed());
        builder.setWorkingStatus(isWorkingEnabled, isWorking)
                .addLowPowerLine(!canWork())
                .addEnergyUsageLine(energyContainer)
                .addEnergyTierLine(getMaxTier())
                .addEnergyUsageExactLine(getCurrentConsumption())
                .addCustom(tl -> {
                    tl.add(TextComponentUtil.translationWithColor(
                            TextFormatting.GRAY,
                            "gtbm.multiblock.mega_apiary.princess",
                            this.inputSize));
                    if (isWorking) {
                        float currentProgress = ((float) progressTicks / PROCESS_TIME) * 100;
                        double current = (double) progressTicks / 20;
                        tl.add(TextComponentUtil.translationWithColor(TextFormatting.GRAY,
                                "gtbm.multiblock.progress",
                                formatTime(current), formatTime((double) PROCESS_TIME / 20),
                                String.format("%.2f", currentProgress)));
                    }
                    for (ItemStack stack : merged) {
                        tl.add(TextComponentUtil.translationWithColor(
                                TextFormatting.WHITE,
                                "gtbm.multiblock.maga_apiary.outputs",
                                TextComponentUtil.stringWithColor(
                                        TextFormatting.AQUA,
                                        stack.getDisplayName()
                                ),
                                TextComponentUtil.stringWithColor(
                                        TextFormatting.GOLD,
                                        TextFormattingUtil.formatNumbers(stack.getCount()))
                        ));
                    }
                })
                .addWorkingStatusLine();
    }

    private static ITextComponent formatTime(double time) {
        return TextComponentUtil.translationWithColor(TextFormatting.GRAY, "gtbm.multiblock.progress_sec", String.format("%.2f", time));
    }

    // ==Others==
    @Override
    public boolean allowsExtendedFacing() {
        return false;
    }

    @Override
    public boolean allowsFlip() {
        return false;
    }

    @Override
    public boolean isWorkingEnabled() {
        return isWorkingEnabled;
    }

    @Override
    public void setWorkingEnabled(boolean isWorkingAllowed) {
        this.isWorkingEnabled = isWorkingAllowed;
        markDirty();
        World world = getWorld();
        if (world != null && !world.isRemote) {
            writeCustomData(GregtechDataCodes.WORKING_ENABLED, buf -> buf.writeBoolean(isWorkingEnabled));
        }
    }

    @Override
    public boolean isActive() {
        return isWorking;
    }

    @Override
    public <T> T getCapability(Capability<T> capability, EnumFacing side) {
        if (capability == GregtechTileCapabilities.CAPABILITY_CONTROLLABLE) {
            return GregtechTileCapabilities.CAPABILITY_CONTROLLABLE.cast(this);
        }
        return super.getCapability(capability, side);
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound data) {
        super.writeToNBT(data);
        data.setBoolean("isWorking", this.isWorking);
        data.setBoolean("isWorkingEnabled", this.isWorkingEnabled);
        data.setInteger("inputSize", this.inputSize);
        data.setInteger("progressTicks", this.progressTicks);
        return data;
    }

    @Override
    public void readFromNBT(NBTTagCompound data) {
        super.readFromNBT(data);
        this.isWorking = data.getBoolean("isWorking");
        this.isWorkingEnabled = data.getBoolean("isWorkingEnabled");
        this.inputSize = data.getInteger("inputSize");
        this.progressTicks = data.getInteger("progressTicks");
    }

    @Override
    public void writeInitialSyncData(PacketBuffer buf) {
        super.writeInitialSyncData(buf);
        buf.writeBoolean(this.isWorking);
        buf.writeBoolean(this.isWorkingEnabled);
        buf.writeInt(this.inputSize);
        buf.writeInt(this.progressTicks);
    }

    @Override
    public void receiveInitialSyncData(PacketBuffer buf) {
        super.receiveInitialSyncData(buf);
        this.isWorking = buf.readBoolean();
        this.isWorkingEnabled = buf.readBoolean();
        this.inputSize = buf.readInt();
        this.progressTicks = buf.readInt();
    }

    @Override
    public void receiveCustomData(int dataId, @NotNull PacketBuffer buf) {
        super.receiveCustomData(dataId, buf);
        if (dataId == GregtechDataCodes.WORKABLE_ACTIVE) {
            this.isWorking = buf.readBoolean();
            scheduleRenderUpdate();
        } else if (dataId == GregtechDataCodes.WORKING_ENABLED) {
            this.isWorkingEnabled = buf.readBoolean();
            scheduleRenderUpdate();
        }
    }
}
