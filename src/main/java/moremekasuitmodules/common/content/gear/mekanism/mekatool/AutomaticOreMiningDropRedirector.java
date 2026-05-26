package moremekasuitmodules.common.content.gear.mekanism.mekatool;

import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public class AutomaticOreMiningDropRedirector {

    private static final ThreadLocal<RedirectTarget> TARGET = new ThreadLocal<>();
    private static final ThreadLocal<Boolean> SPAWNING_REDIRECTED = ThreadLocal.withInitial(() -> Boolean.FALSE);
    private static final Map<World, List<RedirectTarget>> SHORT_LIVED_TARGETS = new HashMap<>();

    public static void runAtPlayer(EntityPlayerMP player, Runnable action) {
        runAtPlayer(player, null, action);
    }

    public static void runAtPlayer(EntityPlayerMP player, BlockPos sourcePos, Runnable action) {
        callAtPlayer(player, () -> {
            action.run();
            return null;
        }, sourcePos);
    }

    public static <T> T callAtPlayer(EntityPlayerMP player, Supplier<T> action) {
        return callAtPlayer(player, action, null);
    }

    public static <T> T callAtPlayer(EntityPlayerMP player, Supplier<T> action, BlockPos sourcePos) {
        RedirectTarget previous = TARGET.get();
        TARGET.set(new RedirectTarget(player, sourcePos));
        try {
            return action.get();
        } finally {
            if (previous == null) {
                TARGET.remove();
            } else {
                TARGET.set(previous);
            }
        }
    }

    public static void rememberForCurrentTick(EntityPlayerMP player, BlockPos minedPos) {
        pruneExpired(player.world);
        SHORT_LIVED_TARGETS.computeIfAbsent(player.world, ignored -> new ArrayList<>()).add(new RedirectTarget(player, minedPos, player.world.getTotalWorldTime()));
    }

    public static void clearWorld(World world) {
        SHORT_LIVED_TARGETS.remove(world);
    }

    public static void clearAll() {
        SHORT_LIVED_TARGETS.clear();
    }

    public static void dropXpAtPlayer(EntityPlayerMP player, int amount) {
        if (amount <= 0 || player.world.isRemote || !player.world.getGameRules().getBoolean("doTileDrops")) {
            return;
        }
        RedirectTarget target = new RedirectTarget(player, null);
        spawnRedirectedEntity(target, new EntityXPOrb(player.world, target.pos.x, target.pos.y + 0.25D, target.pos.z, amount));
    }

    public static void dropItemsAtPlayer(EntityPlayerMP player, List<ItemStack> stacks) {
        if (stacks.isEmpty() || player.world.isRemote || !player.world.getGameRules().getBoolean("doTileDrops")) {
            return;
        }
        RedirectTarget target = new RedirectTarget(player, null);
        for (ItemStack stack : stacks) {
            spawnItemAtTarget(target, stack);
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onHarvestDrops(BlockEvent.HarvestDropsEvent event) {
        RedirectTarget target = TARGET.get();
        if (target == null || event.getWorld() != target.world || !target.matchesSource(event.getPos())) {
            return;
        }
        List<ItemStack> drops = new ArrayList<>(event.getDrops());
        event.getDrops().clear();
        if (!event.getWorld().getGameRules().getBoolean("doTileDrops") || event.getWorld().restoringBlockSnapshots) {
            return;
        }
        float chance = event.getDropChance();
        for (ItemStack drop : drops) {
            if (drop.isEmpty() || chance < 1.0F && event.getWorld().rand.nextFloat() > chance) {
                continue;
            }
            AutomaticOreMiningTracker.addDrop(target.player, drop.copy());
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onEntityJoinWorld(EntityJoinWorldEvent event) {
        if (SPAWNING_REDIRECTED.get()) {
            return;
        }
        RedirectTarget target = TARGET.get();
        Entity entity = event.getEntity();
        if (!(entity instanceof EntityItem || entity instanceof EntityXPOrb)) {
            return;
        }
        if (target == null || event.getWorld() != target.world || !target.matchesEntity(entity)) {
            target = getShortLivedTarget(event.getWorld(), entity);
        }
        if (target == null) {
            return;
        }
        event.setCanceled(true);
        if (entity instanceof EntityItem) {
            EntityItem item = (EntityItem) entity;
            EntityItem redirected = new EntityItem(target.world, target.pos.x, target.pos.y + 0.25D, target.pos.z, item.getItem().copy());
            redirected.setPickupDelay(item.pickupDelay);
            redirected.setOwner(item.getOwner());
            redirected.setThrower(item.getThrower());
            spawnRedirectedEntity(target, redirected);
        } else if (entity instanceof EntityXPOrb) {
            EntityXPOrb xp = (EntityXPOrb) entity;
            spawnRedirectedEntity(target, new EntityXPOrb(target.world, target.pos.x, target.pos.y + 0.25D, target.pos.z, xp.getXpValue()));
        }
    }

    private RedirectTarget getShortLivedTarget(World world, Entity entity) {
        pruneExpired(world);
        List<RedirectTarget> targets = SHORT_LIVED_TARGETS.get(world);
        if (targets == null) {
            return null;
        }
        RedirectTarget closest = null;
        double closestDistance = Double.MAX_VALUE;
        for (RedirectTarget target : targets) {
            double distance = target.getSourceDistanceSq(entity);
            if (distance <= 4.0D && distance < closestDistance) {
                closest = target;
                closestDistance = distance;
            }
        }
        return closest;
    }

    private static void pruneExpired(World world) {
        long now = world.getTotalWorldTime();
        Iterator<Map.Entry<World, List<RedirectTarget>>> iterator = SHORT_LIVED_TARGETS.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<World, List<RedirectTarget>> entry = iterator.next();
            if (entry.getKey() != world) {
                continue;
            }
            entry.getValue().removeIf(target -> entry.getKey() != target.world || now > target.tick);
            if (entry.getValue().isEmpty()) {
                iterator.remove();
            }
        }
    }

    private static void spawnItemAtTarget(RedirectTarget target, ItemStack stack) {
        if (stack.isEmpty()) {
            return;
        }
        EntityItem item = new EntityItem(target.world, target.pos.x, target.pos.y + 0.25D, target.pos.z, stack);
        item.setDefaultPickupDelay();
        spawnRedirectedEntity(target, item);
    }

    private static void spawnRedirectedEntity(RedirectTarget target, Entity entity) {
        Boolean previous = SPAWNING_REDIRECTED.get();
        SPAWNING_REDIRECTED.set(Boolean.TRUE);
        try {
            target.world.spawnEntity(entity);
        } finally {
            SPAWNING_REDIRECTED.set(previous);
        }
    }

    private static class RedirectTarget {
        private final World world;
        private final EntityPlayerMP player;
        private final Vec3d pos;
        private final BlockPos sourcePos;
        private final long tick;

        private RedirectTarget(EntityPlayerMP player, BlockPos sourcePos) {
            this(player, sourcePos, -1L);
        }

        private RedirectTarget(EntityPlayerMP player, BlockPos sourcePos, long tick) {
            this.world = player.world;
            this.player = player;
            this.pos = player.getPositionVector();
            this.sourcePos = sourcePos;
            this.tick = tick;
        }

        private boolean matchesSource(BlockPos pos) {
            return sourcePos == null || sourcePos.equals(pos);
        }

        private boolean matchesEntity(Entity entity) {
            return sourcePos == null || getSourceDistanceSq(entity) <= 4.0D;
        }

        private double getSourceDistanceSq(Entity entity) {
            return sourcePos == null ? 0.0D : entity.getDistanceSq(sourcePos);
        }
    }
}
