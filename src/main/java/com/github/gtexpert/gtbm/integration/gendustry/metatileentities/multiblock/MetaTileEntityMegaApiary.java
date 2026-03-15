package com.github.gtexpert.gtbm.integration.gendustry.metatileentities.multiblock;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.client.resources.I18n;
import net.minecraft.init.Blocks;
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
import gregtech.api.pattern.TraceabilityPredicate;
import gregtech.api.util.GTTransferUtils;
import gregtech.api.util.GTUtility;
import gregtech.api.util.ItemStackHashStrategy;
import gregtech.api.util.RelativeDirection;
import gregtech.api.util.TextComponentUtil;
import gregtech.api.util.TextFormattingUtil;
import gregtech.client.renderer.ICubeRenderer;
import gregtech.client.renderer.texture.Textures;
import gregtech.common.blocks.BlockGlassCasing;
import gregtech.common.blocks.BlockMetalCasing;
import gregtech.common.blocks.MetaBlocks;
import gregtech.common.metatileentities.MetaTileEntities;
import gregtechfoodoption.block.GTFOMetaBlocks;

import com.github.gtexpert.gtbm.api.util.Mods;
import com.github.gtexpert.gtbm.client.GTBMTextures;

import codechicken.lib.render.CCRenderState;
import codechicken.lib.render.pipeline.IVertexOperation;
import codechicken.lib.vec.Matrix4;
import forestry.api.apiculture.BeeManager;
import forestry.api.apiculture.IAlleleBeeSpecies;
import forestry.api.apiculture.IBeeGenome;
import forestry.api.apiculture.IBeekeepingMode;
import forestry.apiculture.ModuleApiculture;
import forestry.apiculture.blocks.BlockAlvearyType;
import forestry.apiculture.genetics.BeeDefinition;
import forestry.apiculture.genetics.BeeGenome;
import forestry.arboriculture.ModuleArboriculture;
import org.jetbrains.annotations.Nullable;

public class MetaTileEntityMegaApiary extends MultiblockWithDisplayBase implements IControllable {

    private boolean isWorkingEnabled = true;
    private boolean isWorking;

    private static final int PROCESS_TIME = 100;
    private int progressTicks = 0;
    private int inputSize = 0;
    private int royalJerry = 0;

    private long consumption;

    private static final float MAX_PRODUCTION_MODIFIER_FROM_UPGRADES = (float) (4 * Math.pow(1.2, 8));

    private final List<ItemStack> queenStacks = new ArrayList<>();
    private final List<ItemStack> energyCalculationStacks = new ArrayList<>();

    private final NonNullList<ItemStack> products = NonNullList.create();
    private final NonNullList<ItemStack> merged = NonNullList.create();

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
    protected void updateFormedValid() {}

    @Override
    protected @NotNull BlockPattern createStructurePattern() {
        return FactoryBlockPattern.start(RelativeDirection.RIGHT, RelativeDirection.FRONT, RelativeDirection.DOWN)
                .aisle("###############", "###############", "###############", "######HHH######", "####HHAAAHH####",
                        "####HAPLPAH####", "###HAPAAAPAH###", "###HALAAALAH###", "###HAPAAAPAH###", "####HAPLPAH####",
                        "####HHAAAHH####", "######HHH######", "###############", "###############", "###############")
                .aisle("###############", "###############", "######GGG######", "####GG###GG####", "###G#######G###",
                        "###G#######G###", "##G#########G##", "##G#########G##", "##G#########G##", "###G#######G###",
                        "###G#######G###", "####GG###GG####", "######GGG######", "###############", "###############")
                .aisle("###############", "######HHH######", "###HHH###HHH###", "##HG#######GH##", "##H#########H##",
                        "##H#########H##", "#H###########H#", "#H###########H#", "#H###########H#", "##H#########H##",
                        "##H#########H##", "##HG#######GH##", "###HHH###HHH###", "######HHH######", "###############")
                .aisle("######GGG######", "###GGG###GGG###", "##G#########G##", "#G###########G#", "#G###########G#",
                        "#G###########G#", "G#############G", "G#############G", "G#############G", "#G###########G#",
                        "#G###########G#", "#G###########G#", "##G#########G##", "###GGG###GGG###", "######GGG######")
                .aisle("######AAA######", "###OLA###ALO###", "##P#########P##", "#O###########O#", "#L###########L#",
                        "#A###########A#", "A#############A", "A#############A", "A#############A", "#A###########A#",
                        "#L###########L#", "#O###########O#", "##P#########P##", "###OLA###ALO###", "######AAA######")
                .aisle("#####AAAAA#####", "###NA#####AO###", "##P#########P##", "#N###########O#", "#A###########A#",
                        "A#############A", "A#####III#####A", "A#####III#####A", "A#####III#####A", "A#############A",
                        "#A###########A#", "#N###########N#", "##P#########P##", "###NA#####AN###", "#####AAAAA#####")
                .aisle("#####AAAAA#####", "###NA#FFF#AO###", "##PFF#####FFP##", "#NF########FFO#", "#AF#########FA#",
                        "A#############A", "AF####JJJ####FA", "AF####JKJ####FA", "AF####JJJ####FA", "A#############A",
                        "#AF#########FA#", "#NFF#######FFN#", "##PFF#####FFP##", "###NA#FFF#AN###", "#####AAAAA#####")
                .aisle("######AAA######", "###OLAFFFALO###", "##PFFFFFFFFFP##", "#OFFFF###FFFFO#", "#LFF#######FFL#",
                        "#AFF#FFFFF##FA#", "AFF##FKKKFF#FFA", "AFF#FFKKKFF#FFA", "AFF#FFKKKF##FFA", "#AF##FFFFF##FA#",
                        "#LFF###FF##FFL#", "#OFFFF####FFFO#", "##PFFFFFFFFFP##", "###OLAFFFALO###", "######AAA######")
                .aisle("######GSG######", "###GGGBBBGGG###", "##GBBFFFFFBBG##", "#GBFFF###FFBBG#", "#GBF#######FBG#",
                        "#GFF#FFFFF##FG#", "GBF##FKKKFF#FBG", "GBF#FFKJKFF#FBG", "GBF#FFKKKF##FBG", "#GF##FFFFF##FG#",
                        "#GBF###FF##FBG#", "#GBBFF####FBBG#", "##GBBFFFFFBBG##", "###GGGBBBGGG###", "######GGG######")
                .aisle("######HHH######", "####HHBBBHH####", "##HHBBBBBBBHH##", "##HBBBWWWBBBH##", "#HBBWWWWWWWBBH#",
                        "#HBBWBBBBBWWBH#", "HBBWWBBBBBBWBBH", "HBBWBBBBBBBWBBH", "HBBWBBBBBBWWBBH", "#HBWWBBBBBWWBH#",
                        "#HBBWWWBBWWBBH#", "##HBBBWWWWBBH##", "##HHBBBBBBBHH##", "####HHBBBHH####", "######HHH######")
                .aisle("###############", "#####GGGGG#####", "###GGGBBBBGG###", "##GBBBBBBBBBG##", "##GBBBBBBBBBG##",
                        "#GBBBBBBBBBBBG#", "#GBBBBBBBBBBBG#", "#GBBBBBBBBBBBG#", "#GBBBBBBBBBBBG#", "#GBBBBBBBBBBBG#",
                        "##GBBBBBBBBBG##", "##GBBBBBBBBBG##", "###GGBBBBBGG###", "#####GGGGG#####", "###############")
                .aisle("###############", "######HHH######", "####HHBBBHH####", "###HBBBBBBBH###", "##HBBBBBBBBBH##",
                        "##HBBBBBBBBBH##", "#HBBBBBBBBBBBH#", "#HBBBBBBBBBBBH#", "#HBBBBBBBBBBBH#", "##HBBBBBBBBBH##",
                        "##HBBBBBBBBBH##", "###HBBBBBBBH###", "####HHBBBHH####", "######HHH######", "###############")
                .aisle("###############", "###############", "######GGG######", "####GGBBBGG####", "###GBBBBBBBG###",
                        "###GBBBBBBBG###", "##GBBBBBBBBBG##", "##GBBBBBBBBBG##", "##GBBBBBBBBBG##", "###GBBBBBBBG###",
                        "###GBBBBBBBG###", "####GGBBBGG####", "######GGG######", "###############", "###############")
                .aisle("###############", "###############", "#######H#######", "#####HHBHH#####", "####HBBBBBH####",
                        "###HBBBBBBBH###", "###HBBBBBBBH###", "##HBBBBBBBBBH##", "###HBBBBBBBH###", "###HBBBBBBBH###",
                        "####HBBBBBH####", "#####HHBHH#####", "#######H#######", "###############", "###############")
                .aisle("###############", "###############", "###############", "#######G#######", "#####GGBGG#####",
                        "####GBBBBBG####", "####GBBBBBG####", "###GBBBBBBBG###", "####GBBBBBG####", "####GBBBBBG####",
                        "#####GGBGG#####", "#######G#######", "###############", "###############", "###############")
                .aisle("###############", "###############", "###############", "###############", "######HHH######",
                        "#####HHHHH#####", "####HHBBBHH####", "####HHBBBHH####", "####HHBBBHH####", "#####HHHHH#####",
                        "######HHH######", "###############", "###############", "###############", "###############")
                .aisle("###############", "###############", "###############", "###############", "###############",
                        "###############", "######GGG######", "######GHG######", "######GGG######", "###############",
                        "###############", "###############", "###############", "###############", "###############")
                .where('A', states(MetaBlocks.TRANSPARENT_CASING.getState(BlockGlassCasing.CasingType.FUSION_GLASS)))
                .where('B', blocks(Blocks.DIRT).or(blocks(Blocks.GRASS)))
                .where('G', states(MetaBlocks.METAL_CASING.getState(BlockMetalCasing.MetalCasingType.BRONZE_BRICKS))
                        .or(abilities(MultiblockAbility.IMPORT_ITEMS).setMinGlobalLimited(1, 2).setMaxGlobalLimited(10))
                        .or(abilities(MultiblockAbility.EXPORT_ITEMS).setMinGlobalLimited(1, 2).setMaxGlobalLimited(10))
                        .or(abilities(MultiblockAbility.MAINTENANCE_HATCH).setExactLimit(1))
                        .or(getEnergyInputPredicate()))
                .where('H', getPlanks())
                .where('I', getSlabs())
                .where('J', blocks(ModuleApiculture.getBlocks().apiary))
                .where('K', blocks(ModuleApiculture.getBlocks().getAlvearyBlock(BlockAlvearyType.PLAIN)))
                .where('L', blocks(ModuleApiculture.getBlocks().getAlvearyBlock(BlockAlvearyType.HYGRO)))
                .where('N', blocks(ModuleApiculture.getBlocks().getAlvearyBlock(BlockAlvearyType.STABILISER)))
                .where('O', blocks(ModuleApiculture.getBlocks().getAlvearyBlock(BlockAlvearyType.HEATER)))
                .where('P', blocks(ModuleApiculture.getBlocks().getAlvearyBlock(BlockAlvearyType.FAN)))
                .where('S', selfPredicate())
                .where('W', blocks(Blocks.WATER))
                .where('F', any())
                .where('#', any())
                .build();
    }

    private TraceabilityPredicate getPlanks() {
        List<Block> planks = new ArrayList<>();
        planks.add(Blocks.PLANKS);
        planks.add(MetaBlocks.PLANKS);
        if (Mods.ForestryArboriculture.isModLoaded()) {
            planks.addAll(ModuleArboriculture.getBlocks().planks);
        }
        if (Mods.GregTechFoodOption.isModLoaded()) {
            planks.addAll(GTFOMetaBlocks.GTFO_PLANKS);
        }

        return blocks(planks.toArray(new Block[0]));
    }

    private TraceabilityPredicate getSlabs() {
        List<Block> slabs = new ArrayList<>();
        slabs.add(Blocks.WOODEN_SLAB);
        slabs.add(MetaBlocks.WOOD_SLAB);
        if (Mods.ForestryArboriculture.isModLoaded()) {
            slabs.addAll(ModuleArboriculture.getBlocks().slabs);
        }

        return blocks(slabs.toArray(new Block[0]));
    }

    private TraceabilityPredicate getEnergyInputPredicate() {
        List<MetaTileEntity> energyHatch = new ArrayList<>();

        energyHatch.addAll(Arrays.asList(MetaTileEntities.ENERGY_INPUT_HATCH));
        energyHatch.addAll(Arrays.asList(MetaTileEntities.SUBSTATION_ENERGY_INPUT_HATCH));
        energyHatch.addAll(Arrays.asList(MetaTileEntities.LASER_INPUT_HATCH_256));
        energyHatch.addAll(Arrays.asList(MetaTileEntities.LASER_INPUT_HATCH_1024));
        energyHatch.addAll(Arrays.asList(MetaTileEntities.LASER_INPUT_HATCH_4096));

        return metaTileEntities(energyHatch.toArray(new MetaTileEntity[0])).setMinGlobalLimited(1, 2)
                .setMaxGlobalLimited(16);
    }

    @Override
    public ICubeRenderer getBaseTexture(IMultiblockPart sourcePart) {
        return Textures.BRONZE_PLATED_BRICKS;
    }

    @SideOnly(Side.CLIENT)
    @NotNull
    @Override
    protected ICubeRenderer getFrontOverlay() {
        return GTBMTextures.INDUSTRIAL_APIARY_OVERLAY;
    }

    @Override
    public void renderMetaTileEntity(CCRenderState renderState, Matrix4 translation, IVertexOperation[] pipeline) {
        super.renderMetaTileEntity(renderState, translation, pipeline);
        getFrontOverlay().renderOrientedState(renderState, translation, pipeline, getFrontFacing(), isActive(), isWorkingEnabled);
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
            } else if (isJerry(stack)) {
                int count = importItems.getStackInSlot(i).getCount();
                royalJerry += count;
                importItems.extractItem(i, count, false);
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

        for (ItemStack queen : queenStacks) {
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
            float baseModifier = mode.getBeeModifier().getProductionModifier(genome,
                    MAX_PRODUCTION_MODIFIER_FROM_UPGRADES);
            int bonus = Math.min(royalJerry, 40);

            float applyBonus = (float) (1 + bonus * 0.05);

            primary.getProductChances().forEach((stack, f) -> {
                float v = (speed + f + baseModifier + baseModifier * getMaxTier()) * applyBonus;
                while (v > 1.0F) {
                    int size = Math.min((int) v, 64);
                    stack.setCount(size);
                    products.add(stack);
                    v -= size;
                }
            });
            secondary.getProductChances().forEach((stack, f) -> {
                float v = (speed + f + baseModifier + baseModifier * getMaxTier()) * applyBonus;
                while (v > 1.0F) {
                    int size = Math.min((int) v, 64);
                    stack.setCount(size);
                    products.add(stack);
                    v -= size;
                }
            });

            primary.getSpecialtyChances().forEach((stack, f) -> {
                float v = (speed + f + baseModifier + baseModifier * getMaxTier()) * applyBonus;
                while (v > 1.0F) {
                    int size = Math.min((int) v, 64);
                    stack.setCount(size);
                    products.add(stack);
                    v -= size;
                }
            });
            royalJerry -= bonus;
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

    private boolean isJerry(ItemStack stack) {
        return stack.isItemEqual(ModuleApiculture.getItems().royalJelly.getItemStack());
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
                            "gtbm.multiblock.mega_apiary.queens",
                            this.inputSize));

                    ITextComponent jerryBody = TextComponentUtil.translationWithColor(
                            TextFormatting.GRAY,
                            "gtbm.multiblock.mega_apiary.jerry_body",
                            TextFormattingUtil.formatNumbers(this.royalJerry));
                    ITextComponent jerryHover = TextComponentUtil.translationWithColor(
                            TextFormatting.WHITE,
                            "gtbm.multiblock.mega_apiary.jerry_hover"
                    );
                    tl.add(TextComponentUtil.setHover(jerryBody, jerryHover));

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
                                        stack.getDisplayName()),
                                TextComponentUtil.stringWithColor(
                                        TextFormatting.GOLD,
                                        TextFormattingUtil.formatNumbers(stack.getCount()))));
                    }
                })
                .addWorkingStatusLine();
    }

    private static ITextComponent formatTime(double time) {
        return TextComponentUtil.translationWithColor(TextFormatting.GRAY, "gtbm.multiblock.progress_sec",
                String.format("%.2f", time));
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
        return isWorking && isWorkingEnabled;
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
        data.setInteger("royalJerry", this.royalJerry);
        return data;
    }

    @Override
    public void readFromNBT(NBTTagCompound data) {
        super.readFromNBT(data);
        this.isWorking = data.getBoolean("isWorking");
        this.isWorkingEnabled = data.getBoolean("isWorkingEnabled");
        this.inputSize = data.getInteger("inputSize");
        this.progressTicks = data.getInteger("progressTicks");
        this.royalJerry = data.getInteger("royalJerry");
    }

    @Override
    public void writeInitialSyncData(PacketBuffer buf) {
        super.writeInitialSyncData(buf);
        buf.writeBoolean(this.isWorking);
        buf.writeBoolean(this.isWorkingEnabled);
        buf.writeInt(this.inputSize);
        buf.writeInt(this.progressTicks);
        buf.writeInt(this.royalJerry);
    }

    @Override
    public void receiveInitialSyncData(PacketBuffer buf) {
        super.receiveInitialSyncData(buf);
        this.isWorking = buf.readBoolean();
        this.isWorkingEnabled = buf.readBoolean();
        this.inputSize = buf.readInt();
        this.progressTicks = buf.readInt();
        this.royalJerry = buf.readInt();
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
