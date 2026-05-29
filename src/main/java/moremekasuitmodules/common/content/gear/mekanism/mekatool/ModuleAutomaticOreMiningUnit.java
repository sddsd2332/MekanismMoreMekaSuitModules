package moremekasuitmodules.common.content.gear.mekanism.mekatool;

import mekanism.api.annotations.ParametersAreNotNullByDefault;
import mekanism.api.energy.IEnergizedItem;
import mekanism.api.gear.ICustomModule;
import mekanism.api.gear.IModule;
import mekanism.api.gear.config.IModuleConfigItem;
import mekanism.api.gear.config.ModuleColorData;
import mekanism.api.gear.config.ModuleBooleanData;
import mekanism.api.gear.config.ModuleConfigItemCreator;
import mekanism.api.gear.config.ModuleEnumData;
import mekanism.api.text.IHasTextComponent;
import mekanism.api.text.TextComponentGroup;
import mekanism.common.MekanismModules;
import mekanism.common.config.MekanismConfig;
import mekanism.common.content.gear.IModuleContainerItem;
import mekanism.common.content.gear.ModuleHelper;
import mekanism.common.content.gear.mekatool.ModuleExcavationEscalationUnit;
import mekanism.common.content.gear.mekatool.ModuleVeinMiningUnit;
import mekanism.common.util.WorldUtils;
import moremekasuitmodules.common.MekaSuitMoreModules;
import moremekasuitmodules.common.MoreMekaSuitModules;
import moremekasuitmodules.common.content.gear.mekanism.MekaLightningEffectHelper;
import moremekasuitmodules.common.content.gear.mekanism.mekasuit.ModuleOreVisualEnhancementUnit;
import moremekasuitmodules.common.content.gear.mekanism.mekasuit.OreVisualScanServerCache;
import moremekasuitmodules.common.content.gear.mekanism.mekasuit.gmut.MoreMekaSuitModulesLang;
import moremekasuitmodules.common.network.to_client.PacketOreMiningWave;
import moremekasuitmodules.common.network.to_client.PacketOreVisualScan;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.StatList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeHooks;

import java.util.ArrayDeque;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.stream.Collectors;

@ParametersAreNotNullByDefault
public class ModuleAutomaticOreMiningUnit implements ICustomModule<ModuleAutomaticOreMiningUnit> {

    private static final int MINING_WAVE_DELAY_TICKS = 40;
    private static final int MINING_WAVE_DURATION_TICKS = 40;
    private static final int DEFAULT_MINING_WAVE_COLOR = 0xFFFF3030;

    private IModuleConfigItem<MaxBlocks> maxBlocks;
    private IModuleConfigItem<Boolean> chainMining;
    private IModuleConfigItem<Integer> miningWaveColor;

    @Override
    public void init(IModule<ModuleAutomaticOreMiningUnit> module, ModuleConfigItemCreator configItemCreator) {
        maxBlocks = configItemCreator.createConfigItem("max_blocks", MoreMekaSuitModulesLang.MODULE_MAX_BLOCKS, new ModuleEnumData<>(MaxBlocks.MED));
        chainMining = configItemCreator.createConfigItem("chain_mining", MoreMekaSuitModulesLang.MODULE_CHAIN_MINING, new ModuleBooleanData(true));
        miningWaveColor = configItemCreator.createConfigItem("mining_wave_color", MoreMekaSuitModulesLang.MODULE_MINING_WAVE_COLOR, ModuleColorData.argb(DEFAULT_MINING_WAVE_COLOR));
    }

    @Override
    public void tickServerUpdate(IModule<ModuleAutomaticOreMiningUnit> module, ItemStack stack, World world, Entity entity, int itemSlot, boolean isSelected) {
        if (!isSelected || world.isRemote || !(entity instanceof EntityPlayerMP player) || player.getHeldItemMainhand() != stack
                || !(stack.getItem() instanceof IModuleContainerItem moduleContainer) || !(stack.getItem() instanceof IEnergizedItem energyContainer)) {
            if (!world.isRemote && entity instanceof EntityPlayerMP player && AutomaticOreMiningTracker.hasState(player)) {
                AutomaticOreMiningTracker.cancel(player);
            }
            return;
        }
        IModule<ModuleOreVisualEnhancementUnit> scannerModule = getScannerModule(player);
        if (scannerModule == null || !scannerModule.isEnabled()) {
            AutomaticOreMiningTracker.cancel(player);
            return;
        }
        ModuleOreVisualEnhancementUnit scanner = scannerModule.getCustomInstance();
        int radius = scanner.getRange();
        int delayTicks = scanner.getScanDelayTicks();
        if (radius <= 0 || delayTicks <= 0) {
            AutomaticOreMiningTracker.cancel(player);
            return;
        }
        OreVisualScanServerCache.ScanResult scanResult = OreVisualScanServerCache.get(player);
        if (scanResult == null || scanResult.getRadius() != radius) {
            AutomaticOreMiningTracker.cancel(player);
            return;
        }
        tickMiningWave(player, moduleContainer, energyContainer, stack, world, scanResult);
    }

    private void tickMiningWave(EntityPlayerMP player, IModuleContainerItem moduleContainer, IEnergizedItem energyContainer, ItemStack stack, World world, OreVisualScanServerCache.ScanResult scanResult) {
        long sequence = scanResult.getSequence();
        AutomaticOreMiningTracker.MineState state = AutomaticOreMiningTracker.getState(player);
        if (state.hasCompleted(sequence)) {
            return;
        }
        long now = world.getTotalWorldTime();
        if (!state.isActive(sequence)) {
            long scanAnimationDone = scanResult.getScanTick() + ModuleOreVisualEnhancementUnit.SCANNER_ANIMATION_TICKS;
            state.begin(sequence, Math.max(now, scanAnimationDone) + MINING_WAVE_DELAY_TICKS);
        }
        if (now < state.getWaitUntilTick()) {
            return;
        }
        if (!state.isWaveStarted()) {
            state.startWave(now);
            MoreMekaSuitModules.packetHandler.sendTo(new PacketOreMiningWave.Message(scanResult.getCenter(), scanResult.getRadius(), miningWaveColor.get(), MINING_WAVE_DURATION_TICKS), player);
        }
        int currentRadius = getMiningWaveRadius(now - state.getWaveStartTick(), scanResult.getRadius());
        if (currentRadius > state.getLastMinedRadius()) {
            WaveMineResult result = mineScannedOresInWave(player, moduleContainer, energyContainer, stack, scanResult, state.getLastMinedRadius(), currentRadius, maxBlocks.get().getMaxBlocks() - state.getMinedBlocks());
            state.addMinedBlocks(result.minedBlocks);
            state.addExperience(result.experience);
            state.setLastMinedRadius(currentRadius);
            if (result.finished || state.getMinedBlocks() >= maxBlocks.get().getMaxBlocks()) {
                state.complete(player, sequence);
                return;
            }
        }
        if (now - state.getWaveStartTick() >= MINING_WAVE_DURATION_TICKS) {
            WaveMineResult result = mineScannedOresInWave(player, moduleContainer, energyContainer, stack, scanResult, state.getLastMinedRadius(), scanResult.getRadius(), maxBlocks.get().getMaxBlocks() - state.getMinedBlocks());
            state.addMinedBlocks(result.minedBlocks);
            state.addExperience(result.experience);
            state.complete(player, sequence);
        }
    }

    private int getMiningWaveRadius(long elapsedTicks, int targetRadius) {
        if (elapsedTicks >= MINING_WAVE_DURATION_TICKS) {
            return targetRadius;
        }
        double progress = Math.max(0.0D, Math.min(1.0D, elapsedTicks / (double) MINING_WAVE_DURATION_TICKS));
        return Math.min(targetRadius, (int) Math.floor(targetRadius * progress * progress));
    }

    private IModule<ModuleOreVisualEnhancementUnit> getScannerModule(EntityPlayerMP player) {
        ItemStack helmet = player.getItemStackFromSlot(EntityEquipmentSlot.HEAD);
        if (helmet.isEmpty()) {
            return null;
        }
        return ModuleHelper.get().load(helmet, MekaSuitMoreModules.ORE_VISUAL_ENHANCEMENT_UNIT);
    }

    private WaveMineResult mineScannedOresInWave(EntityPlayerMP player, IModuleContainerItem moduleContainer, IEnergizedItem energyContainer, ItemStack stack, OreVisualScanServerCache.ScanResult scanResult, int previousRadius, int currentRadius, int remainingLimit) {
        if (currentRadius < 0 || currentRadius <= previousRadius) {
            return new WaveMineResult(0, false, 0);
        }
        if (remainingLimit <= 0) {
            return new WaveMineResult(0, true, 0);
        }
        World world = player.world;
        BlockPos origin = scanResult.getCenter();
        boolean creative = player.isCreative();
        boolean silk = moduleContainer.isModuleEnabled(stack, MekanismModules.SILK_TOUCH_UNIT);
        if (!creative && energyContainer.getEnergy(stack) <= 0) {
            return new WaveMineResult(0, true, 0);
        }
        int mined = 0;
        int experience = 0;
        Set<BlockPos> visited = new HashSet<>();
        int previousRadiusSq = previousRadius * previousRadius;
        int currentRadiusSq = currentRadius * currentRadius;
        List<PacketOreVisualScan.OreEntry> entries = scanResult.getEntries();
        for (PacketOreVisualScan.OreEntry entry : getSortedUsableEntries(entries, origin, scanResult.getRadius())) {
            if (mined >= remainingLimit) {
                break;
            }
            int distanceSq = (int) entry.getPos().distanceSq(origin);
            if (distanceSq <= previousRadiusSq || distanceSq > currentRadiusSq) {
                continue;
            }
            BlockPos pos = entry.getPos();
            if (!visited.add(pos) || !isLoadedBuildHeight(world, pos)) {
                continue;
            }
            IBlockState state = world.getBlockState(pos);
            if (!canMineOre(world, pos, state) || !matchesScannedOre(entry, world, pos, state)) {
                continue;
            }
            MineResult result = mineOreTarget(player, moduleContainer, energyContainer, stack, entry, state, silk, creative, remainingLimit - mined, visited, scanResult, previousRadiusSq, currentRadiusSq);
            mined += result.minedBlocks;
            experience += result.experience;
            if (result.outOfEnergy) {
                break;
            }
        }
        if (mined > 0) {
            player.swingArm(EnumHand.MAIN_HAND);
        }
        return new WaveMineResult(mined, currentRadius >= scanResult.getRadius() || mined >= remainingLimit, experience);
    }

    private List<PacketOreVisualScan.OreEntry> getSortedUsableEntries(List<PacketOreVisualScan.OreEntry> entries, BlockPos origin, int radius) {
        return entries.stream()
                .filter(entry -> isWithinCurrentRange(origin, entry.getPos(), radius))
                .sorted(Comparator.comparingDouble(entry -> entry.getPos().distanceSq(origin)))
                .collect(Collectors.toList());
    }

    private MineResult mineOreTarget(EntityPlayerMP player, IModuleContainerItem moduleContainer, IEnergizedItem energyContainer, ItemStack stack, PacketOreVisualScan.OreEntry seedEntry, IBlockState seedState, boolean silk, boolean creative, int remaining, Set<BlockPos> visited, OreVisualScanServerCache.ScanResult scanResult, int previousRadiusSq, int currentRadiusSq) {
        Map<BlockPos, OreCandidate> candidates = getCandidates(player.world, moduleContainer, stack, seedEntry, seedState, remaining, scanResult);
        boolean singleCandidate = candidates.size() == 1;
        int mined = 0;
        int experience = 0;
        boolean outOfEnergy = false;
        for (Map.Entry<BlockPos, OreCandidate> entry : candidates.entrySet()) {
            if (mined >= remaining) {
                break;
            }
            BlockPos pos = entry.getKey();
            OreCandidate candidate = entry.getValue();
            int distanceSq = (int) pos.distanceSq(scanResult.getCenter());
            if (distanceSq <= previousRadiusSq || distanceSq > currentRadiusSq) {
                continue;
            }
            visited.add(pos);
            BlockBreakResult result = breakOreBlock(player, moduleContainer, energyContainer, stack, pos, silk, creative, candidate.distance);
            if (result.result == BreakResult.BROKEN) {
                if (singleCandidate) {
                    MekaLightningEffectHelper.renderBlockImpact(player.world, pos, player.ticksExisted, candidate.entry.getColor());
                }
                mined++;
                experience += result.experience;
            } else if (result.result == BreakResult.OUT_OF_ENERGY) {
                outOfEnergy = true;
                break;
            }
        }
        return new MineResult(mined, outOfEnergy, experience);
    }

    private Map<BlockPos, OreCandidate> getCandidates(World world, IModuleContainerItem moduleContainer, ItemStack stack, PacketOreVisualScan.OreEntry seedEntry, IBlockState seedState, int limit, OreVisualScanServerCache.ScanResult scanResult) {
        Map<BlockPos, OreCandidate> candidates = new LinkedHashMap<>();
        IModule<ModuleVeinMiningUnit> veinMiningUnit = moduleContainer.getModule(stack, MekanismModules.VEIN_MINING_UNIT);
        if (!chainMining.get() || veinMiningUnit == null || !veinMiningUnit.isEnabled() || !ModuleVeinMiningUnit.canVeinBlock(seedState)) {
            candidates.put(seedEntry.getPos(), new OreCandidate(seedEntry, 0));
            return candidates;
        }
        findScannedOreVeinCandidates(world, seedEntry, Math.max(1, limit), scanResult, candidates);
        if (candidates.isEmpty()) {
            candidates.put(seedEntry.getPos(), new OreCandidate(seedEntry, 0));
        }
        return candidates;
    }

    private void findScannedOreVeinCandidates(World world, PacketOreVisualScan.OreEntry seedEntry, int limit, OreVisualScanServerCache.ScanResult scanResult, Map<BlockPos, OreCandidate> candidates) {
        Map<BlockPos, PacketOreVisualScan.OreEntry> remaining = new LinkedHashMap<>();
        for (PacketOreVisualScan.OreEntry entry : scanResult.getEntries()) {
            if (entry.getOreName().equals(seedEntry.getOreName()) && entry.getDisplayName().equals(seedEntry.getDisplayName())) {
                remaining.put(entry.getPos(), entry);
            }
        }
        Queue<SearchNode> frontier = new ArrayDeque<>();
        Set<BlockPos> queued = new HashSet<>();
        frontier.add(new SearchNode(seedEntry.getPos(), 0));
        queued.add(seedEntry.getPos());
        while (!frontier.isEmpty() && candidates.size() < limit) {
            SearchNode node = frontier.remove();
            BlockPos pos = node.pos;
            PacketOreVisualScan.OreEntry entry = remaining.get(pos);
            if (entry == null) {
                continue;
            }
            candidates.put(pos, new OreCandidate(entry, node.distance));
            if (candidates.size() >= limit) {
                break;
            }
            for (BlockPos nextMutable : BlockPos.getAllInBoxMutable(pos.add(-1, -1, -1), pos.add(1, 1, 1))) {
                BlockPos next = nextMutable.toImmutable();
                PacketOreVisualScan.OreEntry nextEntry = remaining.get(next);
                if (nextEntry != null && queued.add(next)) {
                    frontier.add(new SearchNode(next, node.distance + 1));
                    MekaLightningEffectHelper.renderBlockChain(world, pos, next, candidates.size(), nextEntry.getColor());
                }
            }
        }
    }

    private BlockBreakResult breakOreBlock(EntityPlayerMP player, IModuleContainerItem moduleContainer, IEnergizedItem energyContainer, ItemStack stack, BlockPos pos, boolean silk, boolean creative, int veinDistance) {
        World world = player.world;
        if (!isLoadedBuildHeight(world, pos)) {
            return BlockBreakResult.skipped();
        }
        IBlockState state = world.getBlockState(pos);
        if (!canMineOre(world, pos, state)) {
            return BlockBreakResult.skipped();
        }
        double energyRequired = getDestroyEnergy(moduleContainer, stack, state.getBlockHardness(world, pos), silk, veinDistance);
        if (!creative && energyContainer.extract(stack, energyRequired, false) < energyRequired) {
            return BlockBreakResult.outOfEnergy();
        }
        int exp = ForgeHooks.onBlockBreakEvent(world, player.interactionManager.getGameType(), player, pos);
        if (exp == -1) {
            return BlockBreakResult.skipped();
        }
        Block block = state.getBlock();
        TileEntity tileEntity = WorldUtils.getTileEntity(world, pos);
        AutomaticOreMiningDropRedirector.rememberForCurrentTick(player, pos);
        if (!AutomaticOreMiningDropRedirector.callAtPlayer(player, () -> block.removedByPlayer(state, world, pos, player, true), pos)) {
            return BlockBreakResult.skipped();
        }
        AutomaticOreMiningDropRedirector.runAtPlayer(player, pos, () -> {
            block.onPlayerDestroy(world, pos, state);
            block.harvestBlock(world, player, pos, state, tileEntity, stack);
        });
        Item usedTool = stack.getItem();
        player.addStat(StatList.getObjectUseStats(usedTool));
        if (!creative) {
            energyContainer.extract(stack, energyRequired, true);
        }
        return BlockBreakResult.broken(exp);
    }

    private double getDestroyEnergy(IModuleContainerItem moduleContainer, ItemStack stack, float hardness, boolean silk) {
        return getDestroyEnergy(getDestroyEnergy(moduleContainer, stack, silk), hardness);
    }

    private double getDestroyEnergy(IModuleContainerItem moduleContainer, ItemStack stack, float hardness, boolean silk, int veinDistance) {
        if (veinDistance <= 0) {
            return getDestroyEnergy(moduleContainer, stack, hardness, silk);
        }
        return getDestroyEnergy(getBaseDestroyEnergy(silk), hardness) * (0.5D * Math.pow(veinDistance, 1.5D));
    }

    private double getDestroyEnergy(double baseDestroyEnergy, float hardness) {
        return hardness == 0 ? baseDestroyEnergy / 2 : baseDestroyEnergy;
    }

    private double getDestroyEnergy(IModuleContainerItem moduleContainer, ItemStack stack, boolean silk) {
        double destroyEnergy = getBaseDestroyEnergy(silk);
        IModule<ModuleExcavationEscalationUnit> excavationUnit = moduleContainer.getModule(stack, MekanismModules.EXCAVATION_ESCALATION_UNIT);
        float efficiency = excavationUnit == null || !excavationUnit.isEnabled() ? MekanismConfig.current().meka.mekaToolBaseEfficiency.val() : excavationUnit.getCustomInstance().getEfficiency();
        return destroyEnergy * efficiency;
    }

    private double getBaseDestroyEnergy(boolean silk) {
        return silk ? MekanismConfig.current().meka.mekaToolEnergyUsageSilk.val() : MekanismConfig.current().meka.mekaToolEnergyUsage.val();
    }

    private boolean isLoadedBuildHeight(World world, BlockPos pos) {
        return pos.getY() >= 0 && pos.getY() < world.getHeight() && world.isBlockLoaded(pos);
    }

    private boolean canMineOre(World world, BlockPos pos, IBlockState state) {
        return !state.getBlock().isAir(state, world, pos)
                && !state.getMaterial().isLiquid()
                && state.getBlockHardness(world, pos) != -1
                && ModuleOreVisualEnhancementUnit.isOreBlock(world, pos, state);
    }

    private boolean matchesScannedOre(PacketOreVisualScan.OreEntry entry, World world, BlockPos pos, IBlockState state) {
        return ModuleOreVisualEnhancementUnit.getOreName(world, pos, state).filter(entry.getOreName()::equals).isPresent();
    }

    private boolean isWithinCurrentRange(BlockPos origin, BlockPos pos, int radius) {
        return pos.distanceSq(origin) <= radius * radius;
    }

    private enum BreakResult {
        BROKEN,
        SKIPPED,
        OUT_OF_ENERGY
    }

    private static class MineResult {
        private final int minedBlocks;
        private final boolean outOfEnergy;
        private final int experience;

        private MineResult(int minedBlocks, boolean outOfEnergy, int experience) {
            this.minedBlocks = minedBlocks;
            this.outOfEnergy = outOfEnergy;
            this.experience = experience;
        }
    }

    private static class WaveMineResult {
        private final int minedBlocks;
        private final boolean finished;
        private final int experience;

        private WaveMineResult(int minedBlocks, boolean finished, int experience) {
            this.minedBlocks = minedBlocks;
            this.finished = finished;
            this.experience = experience;
        }
    }

    private static class BlockBreakResult {
        private static final BlockBreakResult SKIPPED = new BlockBreakResult(BreakResult.SKIPPED, 0);
        private static final BlockBreakResult OUT_OF_ENERGY = new BlockBreakResult(BreakResult.OUT_OF_ENERGY, 0);

        private final BreakResult result;
        private final int experience;

        private BlockBreakResult(BreakResult result, int experience) {
            this.result = result;
            this.experience = experience;
        }

        private static BlockBreakResult skipped() {
            return SKIPPED;
        }

        private static BlockBreakResult outOfEnergy() {
            return OUT_OF_ENERGY;
        }

        private static BlockBreakResult broken(int experience) {
            return new BlockBreakResult(BreakResult.BROKEN, Math.max(0, experience));
        }
    }

    private static class SearchNode {
        private final BlockPos pos;
        private final int distance;

        private SearchNode(BlockPos pos, int distance) {
            this.pos = pos;
            this.distance = distance;
        }
    }

    private static class OreCandidate {
        private final PacketOreVisualScan.OreEntry entry;
        private final int distance;

        private OreCandidate(PacketOreVisualScan.OreEntry entry, int distance) {
            this.entry = entry;
            this.distance = distance;
        }
    }

    public enum MaxBlocks implements IHasTextComponent {
        LOW(16),
        MED(64),
        HIGH(128),
        ULTRA(256);

        private final int maxBlocks;
        private final ITextComponent label;

        MaxBlocks(int maxBlocks) {
            this.maxBlocks = maxBlocks;
            this.label = new TextComponentGroup().getString(Integer.toString(maxBlocks));
        }

        @Override
        public ITextComponent getTextComponent() {
            return label;
        }

        public int getMaxBlocks() {
            return maxBlocks;
        }
    }
}
