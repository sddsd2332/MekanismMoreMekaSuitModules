package moremekasuitmodules.common.content.gear.mekanism.mekasuit;

import moremekasuitmodules.common.network.to_client.PacketOreVisualScan;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.math.BlockPos;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public final class OreVisualScanServerCache {

    private static final Map<UUID, ScanResult> RESULTS = new HashMap<>();
    private static long scanSequence;

    private OreVisualScanServerCache() {
    }

    public static void update(EntityPlayerMP player, BlockPos center, int radius, List<PacketOreVisualScan.OreEntry> entries) {
        RESULTS.put(player.getUniqueID(), new ScanResult(++scanSequence, player.world.provider.getDimension(), player.world.getTotalWorldTime(), center, radius, entries));
    }

    public static ScanResult get(EntityPlayerMP player) {
        ScanResult result = RESULTS.get(player.getUniqueID());
        return result != null && result.dimensionId == player.world.provider.getDimension() ? result : null;
    }

    public static void remove(EntityPlayerMP player, BlockPos pos) {
        ScanResult result = get(player);
        if (result != null) {
            result.remove(pos);
        }
    }

    public static void clear(EntityPlayerMP player) {
        RESULTS.remove(player.getUniqueID());
    }

    public static void clearDimension(int dimensionId) {
        RESULTS.entrySet().removeIf(entry -> entry.getValue().dimensionId == dimensionId);
    }

    public static void clearAll() {
        RESULTS.clear();
    }

    public static class ScanResult {
        private final long sequence;
        private final int dimensionId;
        private final long scanTick;
        private final BlockPos center;
        private final int radius;
        private final List<PacketOreVisualScan.OreEntry> entries;

        private ScanResult(long sequence, int dimensionId, long scanTick, BlockPos center, int radius, List<PacketOreVisualScan.OreEntry> entries) {
            this.sequence = sequence;
            this.dimensionId = dimensionId;
            this.scanTick = scanTick;
            this.center = center;
            this.radius = radius;
            this.entries = new ArrayList<>(entries);
        }

        public long getSequence() {
            return sequence;
        }

        public long getScanTick() {
            return scanTick;
        }

        public BlockPos getCenter() {
            return center;
        }

        public int getRadius() {
            return radius;
        }

        public List<PacketOreVisualScan.OreEntry> getEntries() {
            return Collections.unmodifiableList(entries);
        }

        private void remove(BlockPos pos) {
            entries.removeIf(entry -> entry.getPos().equals(pos));
        }
    }
}
