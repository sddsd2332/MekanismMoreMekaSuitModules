package moremekasuitmodules.common.content.gear.mekanism.mekasuit;

import mekanism.api.EnumColor;
import mekanism.api.annotations.ParametersAreNotNullByDefault;
import mekanism.api.gear.ICustomModule;
import mekanism.api.gear.IModule;
import mekanism.api.gear.config.IModuleConfigItem;
import mekanism.api.gear.config.ModuleColorData;
import mekanism.api.gear.config.ModuleConfigItemCreator;
import mekanism.api.gear.config.ModuleEnumData;
import mekanism.api.text.IHasTextComponent;
import mekanism.api.text.TextComponentGroup;
import mekanism.common.MekanismLang;
import mekanism.common.util.LangUtils;
import moremekasuitmodules.common.MoreMekaSuitModules;
import moremekasuitmodules.common.content.gear.mekanism.mekasuit.gmut.MoreMekaSuitModulesLang;
import moremekasuitmodules.common.network.to_client.PacketOreVisualScan;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;
import net.minecraftforge.oredict.OreDictionary;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;

@ParametersAreNotNullByDefault
public class ModuleOreVisualEnhancementUnit implements ICustomModule<ModuleOreVisualEnhancementUnit> {

    public static final int DEFAULT_BOX_COLOR = 0xFF3CFE9A;
    public static final int DEFAULT_TEXT_COLOR = 0xFFFFFFFF;
    public static final int MAX_RESULTS = 256;
    public static final int SCANNER_ANIMATION_TICKS = 40;
    private static final Map<String, Integer> ORE_COLORS = createOreColors();
    private static final Map<Integer, List<BlockPos>> SORTED_OFFSETS = new HashMap<>();

    private IModuleConfigItem<Range> range;
    private IModuleConfigItem<ScanDelay> scanDelay;
    private IModuleConfigItem<Integer> boxColor;
    private IModuleConfigItem<Integer> textColor;

    @Override
    public void init(IModule<ModuleOreVisualEnhancementUnit> module, ModuleConfigItemCreator configItemCreator) {
        range = configItemCreator.createConfigItem("range", MekanismLang.MODULE_RANGE, new ModuleEnumData<>(Range.LOW, module.getInstalledCount() + 1));
        scanDelay = configItemCreator.createConfigItem("scan_delay", MoreMekaSuitModulesLang.MODULE_SCAN_DELAY, new ModuleEnumData<>(ScanDelay.SLOW, module.getInstalledCount() + 1));
        boxColor = configItemCreator.createConfigItem("box_color", MoreMekaSuitModulesLang.MODULE_BOX_COLOR, ModuleColorData.argb(DEFAULT_BOX_COLOR));
        textColor = configItemCreator.createConfigItem("text_color", MoreMekaSuitModulesLang.MODULE_TEXT_COLOR, ModuleColorData.argb(DEFAULT_TEXT_COLOR));
    }

    @Override
    public void tickServer(IModule<ModuleOreVisualEnhancementUnit> module, EntityPlayer player) {
        if (!(player instanceof EntityPlayerMP serverPlayer)) {
            return;
        }
        int radius = getRange();
        int delayTicks = getScanDelayTicks();
        if (radius <= 0 || delayTicks <= 0) {
            return;
        }
        if (OreVisualScanTracker.shouldScan(serverPlayer, delayTicks)) {
            BlockPos origin = serverPlayer.getPosition();
            List<PacketOreVisualScan.OreEntry> entries = scan(serverPlayer, radius, origin);
            OreVisualScanServerCache.update(serverPlayer, origin, radius, entries);
            MoreMekaSuitModules.packetHandler.sendTo(new PacketOreVisualScan.Message(origin, entries), serverPlayer);
        }
    }

    public int getRange() {
        return range.get().getRadius();
    }

    public int getScanDelayTicks() {
        return scanDelay.get().getSeconds() * 20;
    }

    public int getBoxColor() {
        return boxColor.get();
    }

    public int getTextColor() {
        return textColor.get();
    }

    @Override
    public void addHUDStrings(IModule<ModuleOreVisualEnhancementUnit> module, EntityPlayer player, Consumer<String> hudStringAdder) {
        int radius = getRange();
        int delayTicks = getScanDelayTicks();
        if (radius <= 0 || delayTicks <= 0) {
            return;
        }
        int remainingTicks = OreVisualScanTracker.getTicksUntilNextScan(player);
        int seconds = (remainingTicks + 19) / 20;
        int count = MoreMekaSuitModules.proxy.getOreVisualClientEntryCount();
        hudStringAdder.accept(EnumColor.DARK_GREY + LangUtils.localize(MoreMekaSuitModulesLang.MODULE_ORE_SCAN_NEXT.getTranslationKey()) + " " + EnumColor.INDIGO + seconds + "s");
        hudStringAdder.accept(EnumColor.DARK_GREY + LangUtils.localize(MoreMekaSuitModulesLang.MODULE_ORE_SCAN_COUNT.getTranslationKey()) + " " + EnumColor.INDIGO + count);
    }

    private List<PacketOreVisualScan.OreEntry> scan(EntityPlayerMP player, int radius, BlockPos origin) {
        World world = player.world;
        List<PacketOreVisualScan.OreEntry> entries = new ArrayList<>();
        for (BlockPos offset : getSortedOffsets(radius)) {
            BlockPos pos = origin.add(offset);
            if (pos.getY() < 0 || pos.getY() >= world.getHeight() || !world.isBlockLoaded(pos)) {
                continue;
            }
            IBlockState state = world.getBlockState(pos);
            OreInfo oreInfo = getOreInfo(world, pos, state);
            if (oreInfo != null) {
                entries.add(new PacketOreVisualScan.OreEntry(pos, oreInfo.oreName, oreInfo.displayName, getColorForOre(oreInfo.oreName)));
                if (entries.size() >= MAX_RESULTS) {
                    break;
                }
            }
        }
        entries.sort(Comparator.comparingDouble(entry -> entry.getPos().distanceSq(origin)));
        return entries;
    }

    public static List<BlockPos> getSortedOffsets(int radius) {
        return SORTED_OFFSETS.computeIfAbsent(radius, ModuleOreVisualEnhancementUnit::createSortedOffsets);
    }

    private static List<BlockPos> createSortedOffsets(int radius) {
        int radiusSq = radius * radius;
        List<BlockPos> offsets = new ArrayList<>();
        for (int x = -radius; x <= radius; x++) {
            for (int y = -radius; y <= radius; y++) {
                for (int z = -radius; z <= radius; z++) {
                    if (x * x + y * y + z * z <= radiusSq) {
                        offsets.add(new BlockPos(x, y, z));
                    }
                }
            }
        }
        offsets.sort(Comparator.comparingInt(pos -> pos.getX() * pos.getX() + pos.getY() * pos.getY() + pos.getZ() * pos.getZ()));
        return offsets;
    }

    public static boolean isOreBlock(World world, BlockPos pos, IBlockState state) {
        return getOreInfo(world, pos, state) != null;
    }

    public static Optional<String> getOreName(World world, BlockPos pos, IBlockState state) {
        OreInfo oreInfo = getOreInfo(world, pos, state);
        return oreInfo == null ? Optional.empty() : Optional.of(oreInfo.oreName);
    }

    private static OreInfo getOreInfo(World world, BlockPos pos, IBlockState state) {
        Block block = state.getBlock();
        if (block == Blocks.AIR || block.isAir(state, world, pos)) {
            return null;
        }
        try {
            ItemStack stack = block.getPickBlock(state, null, world, pos, null);
            if (stack.isEmpty()) {
                stack = new ItemStack(block, 1, block.damageDropped(state));
            }
            if (stack.isEmpty()) {
                return null;
            }
            for (int oreId : OreDictionary.getOreIDs(stack)) {
                String name = OreDictionary.getOreName(oreId);
                if (name.startsWith("ore")) {
                    return new OreInfo(name, stack.getDisplayName());
                }
            }
        } catch (RuntimeException ignored) {
            return null;
        }
        return null;
    }

    private int getColorForOre(String oreName) {
        Integer color = ORE_COLORS.get(oreName);
        if (color != null) {
            return 0xFF000000 | color;
        }
        int hash = oreName.hashCode();
        int red = 80 + Math.abs(hash & 0x7F);
        int green = 140 + Math.abs((hash >> 8) & 0x6F);
        int blue = 80 + Math.abs((hash >> 16) & 0x7F);
        return 0xFF000000 | (red << 16) | (green << 8) | blue;
    }

    private static Map<String, Integer> createOreColors() {
        Map<String, Integer> colors = new HashMap<>();
        colors.put("oreCoal", 0x433E3B);
        colors.put("oreIron", 0xA17951);
        colors.put("oreGold", 0xF4F71F);
        colors.put("oreLapis", 0x4863F0);
        colors.put("oreDiamond", 0x48E2F0);
        colors.put("oreRedstone", 0xE61E1E);
        colors.put("oreEmerald", 0x12BA16);
        colors.put("oreQuartz", 0xB3D9D2);
        colors.put("oreCopper", 0xE4A020);
        colors.put("oreLead", 0x8187C3);
        colors.put("oreMithril", 0x97D5FE);
        colors.put("oreNickel", 0xD0D3AC);
        colors.put("orePlatinum", 0x7AC0FD);
        colors.put("oreSilver", 0xE8F2FB);
        colors.put("oreTin", 0xCCE4FE);
        colors.put("oreAluminum", 0xCBE4E2);
        colors.put("oreAluminium", 0xCBE4E2);
        colors.put("orePlutonium", 0x9DE054);
        colors.put("oreUranium", 0x9DE054);
        colors.put("oreYellorium", 0xD8E054);
        colors.put("oreArdite", 0xB77E11);
        colors.put("oreCobalt", 0x413BB8);
        colors.put("oreCinnabar", 0xF5DA25);
        colors.put("oreInfusedAir", 0xF7E677);
        colors.put("oreInfusedFire", 0xDC7248);
        colors.put("oreInfusedWater", 0x9595D5);
        colors.put("oreInfusedEarth", 0x49B45A);
        colors.put("oreInfusedOrder", 0x9FF2DE);
        colors.put("oreInfusedEntropy", 0x545476);
        return colors;
    }

    private static class OreInfo {
        private final String oreName;
        private final String displayName;

        private OreInfo(String oreName, String displayName) {
            this.oreName = oreName;
            this.displayName = displayName == null || displayName.isEmpty() ? oreName : displayName;
        }
    }

    public enum Range implements IHasTextComponent {
        OFF(0),
        LOW(4),
        NORMAL(8),
        MEDIUM(16),
        HIGH(32),
        ULTRA(64);

        private final int radius;
        private final ITextComponent label;

        Range(int radius) {
            this.radius = radius;
            this.label = new TextComponentGroup().getString(Integer.toString(radius));
        }

        @Override
        public ITextComponent getTextComponent() {
            return label;
        }

        public int getRadius() {
            return radius;
        }
    }

    public enum ScanDelay implements IHasTextComponent {
        OFF(0),
        SLOW(60),
        NORMAL(45),
        FAST(30),
        VERY_FAST(15),
        ULTRA_FAST(10);

        private final int seconds;
        private final ITextComponent label;

        ScanDelay(int seconds) {
            this.seconds = seconds;
            this.label = new TextComponentGroup().getString(seconds == 0 ? "0s" : seconds + "s");
        }

        @Override
        public ITextComponent getTextComponent() {
            return label;
        }

        public int getSeconds() {
            return seconds;
        }
    }

}
