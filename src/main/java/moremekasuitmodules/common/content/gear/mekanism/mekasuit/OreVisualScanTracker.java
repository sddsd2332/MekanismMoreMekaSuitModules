package moremekasuitmodules.common.content.gear.mekanism.mekasuit;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class OreVisualScanTracker {

    private static final int INITIAL_SCAN_DELAY_TICKS = 200;
    private static final Map<UUID, ScanState> STATES = new HashMap<>();

    private OreVisualScanTracker() {
    }

    public static boolean shouldScan(EntityPlayerMP player, int delayTicks) {
        UUID uuid = player.getUniqueID();
        ScanState state = STATES.computeIfAbsent(uuid, ignored -> new ScanState());
        long now = player.world.getTotalWorldTime();
        int dimensionId = player.world.provider.getDimension();
        if (state.dimensionId != dimensionId) {
            state.dimensionId = dimensionId;
            state.nextScanTick = now + INITIAL_SCAN_DELAY_TICKS;
        }
        if (now < state.nextScanTick) {
            return false;
        }
        state.nextScanTick = now + delayTicks;
        return true;
    }

    public static int getTicksUntilNextScan(EntityPlayer player) {
        ScanState state = STATES.get(player.getUniqueID());
        if (state == null || state.dimensionId != player.world.provider.getDimension()) {
            return 0;
        }
        return (int) Math.max(0L, state.nextScanTick - player.world.getTotalWorldTime());
    }

    public static void clear(EntityPlayerMP player) {
        STATES.remove(player.getUniqueID());
    }

    public static void delayInitialScan(EntityPlayerMP player) {
        ScanState state = STATES.computeIfAbsent(player.getUniqueID(), ignored -> new ScanState());
        state.dimensionId = player.world.provider.getDimension();
        state.nextScanTick = player.world.getTotalWorldTime() + INITIAL_SCAN_DELAY_TICKS;
    }

    public static void clearDimension(int dimensionId) {
        STATES.entrySet().removeIf(entry -> entry.getValue().dimensionId == dimensionId);
    }

    public static void clearAll() {
        STATES.clear();
    }

    private static class ScanState {
        private int dimensionId = Integer.MIN_VALUE;
        private long nextScanTick;
    }
}
