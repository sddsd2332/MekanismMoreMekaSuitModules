package moremekasuitmodules.client;

import moremekasuitmodules.common.network.to_client.PacketOreVisualScan;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@SideOnly(Side.CLIENT)
public final class OreVisualScanClientCache {

    private static final List<PacketOreVisualScan.OreEntry> ENTRIES = new ArrayList<>();
    private static final List<OreCluster> CLUSTERS = new ArrayList<>();
    private static BlockPos scanCenter = BlockPos.ORIGIN;
    private static long lastUpdateTime;
    private static MiningWave miningWave;

    private OreVisualScanClientCache() {
    }

    public static void update(BlockPos center, List<PacketOreVisualScan.OreEntry> entries) {
        scanCenter = center;
        ENTRIES.clear();
        ENTRIES.addAll(entries);
        ENTRIES.sort(Comparator.comparingDouble(entry -> entry.getPos().distanceSq(center)));
        rebuildClusters();
        lastUpdateTime = System.currentTimeMillis();
    }

    public static List<PacketOreVisualScan.OreEntry> getEntries() {
        return ENTRIES;
    }

    public static List<OreCluster> getClusters() {
        return CLUSTERS;
    }

    public static void remove(BlockPos pos) {
        if (ENTRIES.removeIf(entry -> entry.getPos().equals(pos))) {
            rebuildClusters();
        }
    }

    public static void removeAll(List<BlockPos> positions) {
        if (positions.isEmpty()) {
            return;
        }
        Set<BlockPos> removed = new HashSet<>(positions);
        if (ENTRIES.removeIf(entry -> removed.contains(entry.getPos()))) {
            rebuildClusters();
        }
    }

    public static BlockPos getScanCenter() {
        return scanCenter;
    }

    public static long getLastUpdateTime() {
        return lastUpdateTime;
    }

    public static void startMiningWave(BlockPos center, int radius, int color, int durationTicks) {
        miningWave = new MiningWave(center, radius, color, durationTicks);
    }

    public static MiningWave getMiningWave() {
        if (miningWave != null && miningWave.isFinished()) {
            miningWave = null;
        }
        return miningWave;
    }

    public static void clear() {
        ENTRIES.clear();
        CLUSTERS.clear();
        miningWave = null;
    }

    private static void rebuildClusters() {
        CLUSTERS.clear();
        Map<BlockPos, PacketOreVisualScan.OreEntry> remaining = new HashMap<>();
        for (PacketOreVisualScan.OreEntry entry : ENTRIES) {
            remaining.put(entry.getPos(), entry);
        }
        ArrayDeque<BlockPos> queue = new ArrayDeque<>();
        while (!remaining.isEmpty()) {
            PacketOreVisualScan.OreEntry seed = remaining.values().iterator().next();
            remaining.remove(seed.getPos());
            OreCluster cluster = new OreCluster(seed);
            queue.clear();
            queue.add(seed.getPos());
            while (!queue.isEmpty()) {
                BlockPos pos = queue.removeFirst();
                for (BlockPos neighbor : getNeighbors(pos)) {
                    PacketOreVisualScan.OreEntry entry = remaining.get(neighbor);
                    if (entry == null || !cluster.matches(entry)) {
                        continue;
                    }
                    remaining.remove(neighbor);
                    cluster.add(entry);
                    queue.add(neighbor);
                }
            }
            cluster.finish();
            CLUSTERS.add(cluster);
        }
    }

    private static List<BlockPos> getNeighbors(BlockPos pos) {
        List<BlockPos> neighbors = new ArrayList<>(6);
        neighbors.add(pos.north());
        neighbors.add(pos.south());
        neighbors.add(pos.east());
        neighbors.add(pos.west());
        neighbors.add(pos.up());
        neighbors.add(pos.down());
        return neighbors;
    }

    public static class OreCluster {

        final String oreName;
        public final String displayName;
        public final int color;
        public final Set<BlockPos> positions = new HashSet<>();
        private int minX;
        private int minY;
        private int minZ;
        private int maxX;
        private int maxY;
        private int maxZ;
        public Vec3d center = Vec3d.ZERO;

        private OreCluster(PacketOreVisualScan.OreEntry seed) {
            this.oreName = seed.getOreName();
            this.displayName = seed.getDisplayName();
            this.color = seed.getColor();
            this.minX = seed.getPos().getX();
            this.minY = seed.getPos().getY();
            this.minZ = seed.getPos().getZ();
            this.maxX = seed.getPos().getX() + 1;
            this.maxY = seed.getPos().getY() + 1;
            this.maxZ = seed.getPos().getZ() + 1;
            add(seed);
        }

        private boolean matches(PacketOreVisualScan.OreEntry entry) {
            return color == entry.getColor() && oreName.equals(entry.getOreName()) && displayName.equals(entry.getDisplayName());
        }

        private void add(PacketOreVisualScan.OreEntry entry) {
            BlockPos pos = entry.getPos();
            positions.add(pos);
            minX = Math.min(minX, pos.getX());
            minY = Math.min(minY, pos.getY());
            minZ = Math.min(minZ, pos.getZ());
            maxX = Math.max(maxX, pos.getX() + 1);
            maxY = Math.max(maxY, pos.getY() + 1);
            maxZ = Math.max(maxZ, pos.getZ() + 1);
        }

        private void finish() {
            center = new Vec3d((minX + maxX) * 0.5D, (minY + maxY) * 0.5D, (minZ + maxZ) * 0.5D);
        }

        public double getDistanceSq(BlockPos pos) {
            return center.squareDistanceTo(pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D);
        }
    }

    public static class MiningWave {

        private final BlockPos center;
        private final int radius;
        private final int color;
        private final long startTime;
        private final long durationMs;

        private MiningWave(BlockPos center, int radius, int color, int durationTicks) {
            this.center = center;
            this.radius = radius;
            this.color = color;
            this.startTime = System.currentTimeMillis();
            this.durationMs = Math.max(1, durationTicks) * 50L;
        }

        public BlockPos getCenter() {
            return center;
        }

        public int getRadius() {
            return radius;
        }

        public int getColor() {
            return color;
        }

        public long getStartTime() {
            return startTime;
        }

        public long getDurationMs() {
            return durationMs;
        }

        private boolean isFinished() {
            return System.currentTimeMillis() - startTime > durationMs;
        }
    }
}
