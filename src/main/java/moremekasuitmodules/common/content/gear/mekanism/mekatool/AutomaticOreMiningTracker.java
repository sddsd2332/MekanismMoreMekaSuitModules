package moremekasuitmodules.common.content.gear.mekanism.mekatool;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public final class AutomaticOreMiningTracker {

    private static final Map<UUID, MineState> STATES = new HashMap<>();

    private AutomaticOreMiningTracker() {
    }

    public static MineState getState(EntityPlayerMP player) {
        MineState state = STATES.computeIfAbsent(player.getUniqueID(), ignored -> new MineState());
        int dimensionId = player.world.provider.getDimension();
        if (state.dimensionId != dimensionId) {
            state.resetForDimension(dimensionId);
        }
        return state;
    }

    public static boolean hasState(EntityPlayerMP player) {
        return STATES.containsKey(player.getUniqueID());
    }

    public static void clear(EntityPlayerMP player) {
        STATES.remove(player.getUniqueID());
    }

    public static void cancel(EntityPlayerMP player) {
        MineState state = STATES.get(player.getUniqueID());
        if (state != null) {
            state.flushDrops(player);
            state.flushExperience(player);
            state.cancelActive();
        }
    }

    public static void addDrop(EntityPlayerMP player, ItemStack stack) {
        if (!stack.isEmpty()) {
            getState(player).addDrop(stack);
        }
    }

    public static void clearDimension(int dimensionId) {
        STATES.entrySet().removeIf(entry -> entry.getValue().dimensionId == dimensionId);
    }

    public static void clearAll() {
        STATES.clear();
    }

    public static class MineState {

        private int dimensionId = Integer.MIN_VALUE;
        private long activeScanSequence;
        private long completedScanSequence;
        private long waitUntilTick;
        private long waveStartTick;
        private int lastMinedRadius = -1;
        private int minedBlocks;
        private int pendingExperience;
        private final List<ItemStack> pendingDrops = new ArrayList<>();
        private boolean waveStarted;

        private void resetForDimension(int dimensionId) {
            this.dimensionId = dimensionId;
            activeScanSequence = 0L;
            completedScanSequence = 0L;
            waitUntilTick = 0L;
            waveStartTick = 0L;
            lastMinedRadius = -1;
            minedBlocks = 0;
            pendingExperience = 0;
            pendingDrops.clear();
            waveStarted = false;
        }

        public boolean hasCompleted(long scanSequence) {
            return scanSequence <= completedScanSequence;
        }

        public void begin(long scanSequence, long waitUntilTick) {
            activeScanSequence = scanSequence;
            this.waitUntilTick = waitUntilTick;
            waveStartTick = 0L;
            lastMinedRadius = -1;
            minedBlocks = 0;
            pendingExperience = 0;
            pendingDrops.clear();
            waveStarted = false;
        }

        public boolean isActive(long scanSequence) {
            return activeScanSequence == scanSequence && !hasCompleted(scanSequence);
        }

        public long getWaitUntilTick() {
            return waitUntilTick;
        }

        public boolean isWaveStarted() {
            return waveStarted;
        }

        public void startWave(long waveStartTick) {
            this.waveStartTick = waveStartTick;
            lastMinedRadius = -1;
            waveStarted = true;
        }

        public long getWaveStartTick() {
            return waveStartTick;
        }

        public int getLastMinedRadius() {
            return lastMinedRadius;
        }

        public void setLastMinedRadius(int lastMinedRadius) {
            this.lastMinedRadius = lastMinedRadius;
        }

        public int getMinedBlocks() {
            return minedBlocks;
        }

        public void addMinedBlocks(int minedBlocks) {
            this.minedBlocks += minedBlocks;
        }

        public void addExperience(int experience) {
            pendingExperience += experience;
        }

        public void addDrop(ItemStack stack) {
            ItemStack remaining = stack.copy();
            for (ItemStack pendingDrop : pendingDrops) {
                if (!canMerge(pendingDrop, remaining)) {
                    continue;
                }
                int transfer = Math.min(remaining.getCount(), pendingDrop.getMaxStackSize() - pendingDrop.getCount());
                if (transfer > 0) {
                    pendingDrop.grow(transfer);
                    remaining.shrink(transfer);
                }
                if (remaining.isEmpty()) {
                    return;
                }
            }
            while (!remaining.isEmpty()) {
                int splitSize = Math.min(remaining.getCount(), remaining.getMaxStackSize());
                ItemStack split = remaining.copy();
                split.setCount(splitSize);
                pendingDrops.add(split);
                remaining.shrink(splitSize);
            }
        }

        public void complete(EntityPlayerMP player, long scanSequence) {
            completedScanSequence = Math.max(completedScanSequence, scanSequence);
            if (activeScanSequence == scanSequence) {
                flushDrops(player);
                flushExperience(player);
                cancelActive();
            }
        }

        private void cancelActive() {
            activeScanSequence = 0L;
            waitUntilTick = 0L;
            waveStartTick = 0L;
            lastMinedRadius = -1;
            minedBlocks = 0;
            waveStarted = false;
        }

        private void flushExperience(EntityPlayerMP player) {
            if (pendingExperience > 0) {
                AutomaticOreMiningDropRedirector.dropXpAtPlayer(player, pendingExperience);
                pendingExperience = 0;
            }
        }

        private void flushDrops(EntityPlayerMP player) {
            if (!pendingDrops.isEmpty()) {
                AutomaticOreMiningDropRedirector.dropItemsAtPlayer(player, new ArrayList<>(pendingDrops));
                pendingDrops.clear();
            }
        }

        private boolean canMerge(ItemStack target, ItemStack stack) {
            return target.getCount() < target.getMaxStackSize()
                    && ItemStack.areItemsEqual(target, stack)
                    && ItemStack.areItemStackTagsEqual(target, stack);
        }
    }
}
