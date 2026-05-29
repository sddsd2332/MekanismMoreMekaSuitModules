package moremekasuitmodules.common.content.gear.mekanism;

import mekanism.common.Mekanism;
import mekanism.common.network.PacketLightningRender.LightningPreset;
import mekanism.common.network.PacketLightningRender.LightningRenderMessage;
import moremekasuitmodules.common.MoreMekaSuitModules;
import moremekasuitmodules.common.network.to_client.PacketColoredLightningRender;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;

public final class MekaLightningEffectHelper {

    private static final int ENTITY_IMPACT_BOLTS = 5;
    private static final int ENTITY_BOLT_SEGMENTS = 8;
    private static final int BLOCK_IMPACT_BOLT_SEGMENTS = 6;
    private static final int BLOCK_CHAIN_BOLT_SEGMENTS = 10;

    private MekaLightningEffectHelper() {
    }

    public static void renderEntityImpact(EntityLivingBase target, int seed) {
        if (target == null || target.world == null || target.world.isRemote) {
            return;
        }
        AxisAlignedBB box = target.getEntityBoundingBox();
        Vec3d center = center(box);
        for (int i = 0; i < ENTITY_IMPACT_BOLTS; i++) {
            Vec3d[] span = entityBoxSpan(box, center, i);
            send(target.world, Objects.hash("entity", target.getEntityId(), target.ticksExisted, seed, i), span[0], span[1], ENTITY_BOLT_SEGMENTS);
        }
    }

    public static void renderBlockChain(World world, BlockPos from, BlockPos to, int seed) {
        renderBlockChain(world, from, to, seed, -1);
    }

    public static void renderBlockChain(World world, BlockPos from, BlockPos to, int seed, int color) {
        if (world == null || world.isRemote || from == null || to == null) {
            return;
        }
        Vec3d start = center(from);
        Vec3d end = center(to);
        send(world, Objects.hash("block_chain", from, to, world.getTotalWorldTime(), seed), start, end, BLOCK_CHAIN_BOLT_SEGMENTS, color);
    }

    public static void renderBlockImpact(World world, BlockPos pos, int seed) {
        renderBlockImpact(world, pos, seed, -1);
    }

    public static void renderBlockImpact(World world, BlockPos pos, int seed, int color) {
        if (world == null || world.isRemote || pos == null) {
            return;
        }
        Vec3d start = center(pos);
        Vec3d end = randomBlockEdgePoint(pos);
        send(world, Objects.hash("block_impact", pos, world.getTotalWorldTime(), seed), start, end, BLOCK_IMPACT_BOLT_SEGMENTS, color);
    }

    private static void send(World world, int renderer, Vec3d start, Vec3d end, int segments) {
        send(world, renderer, start, end, segments, -1);
    }

    private static void send(World world, int renderer, Vec3d start, Vec3d end, int segments, int color) {
        if (color != -1) {
            MoreMekaSuitModules.packetHandler.sendToAllTracking(
                    new PacketColoredLightningRender.Message(renderer, start, end, segments, color),
                    world.provider.getDimension(),
                    (start.x + end.x) * 0.5D,
                    (start.y + end.y) * 0.5D,
                    (start.z + end.z) * 0.5D
            );
            return;
        }
        Mekanism.packetHandler.sendToAllTracking(
                new LightningRenderMessage(LightningPreset.TOOL_AOE, renderer, start, end, segments),
                world.provider.getDimension(),
                (start.x + end.x) * 0.5D,
                (start.y + end.y) * 0.5D,
                (start.z + end.z) * 0.5D
        );
    }

    private static Vec3d[] entityBoxSpan(AxisAlignedBB box, Vec3d center, int index) {
        return switch (index % ENTITY_IMPACT_BOLTS) {
            case 0 -> new Vec3d[]{
                    new Vec3d(center.x, box.minY, center.z),
                    new Vec3d(center.x, box.maxY, center.z)
            };
            case 1 -> new Vec3d[]{
                    new Vec3d(box.minX, center.y, center.z),
                    new Vec3d(box.maxX, center.y, center.z)
            };
            case 2 -> new Vec3d[]{
                    new Vec3d(center.x, center.y, box.minZ),
                    new Vec3d(center.x, center.y, box.maxZ)
            };
            case 3 -> new Vec3d[]{
                    new Vec3d(box.minX, box.minY, box.minZ),
                    new Vec3d(box.maxX, box.maxY, box.maxZ)
            };
            default -> new Vec3d[]{
                    new Vec3d(box.minX, box.minY, box.maxZ),
                    new Vec3d(box.maxX, box.maxY, box.minZ)
            };
        };
    }

    private static Vec3d center(AxisAlignedBB box) {
        return new Vec3d(
                (box.minX + box.maxX) * 0.5D,
                (box.minY + box.maxY) * 0.5D,
                (box.minZ + box.maxZ) * 0.5D
        );
    }

    private static Vec3d center(BlockPos pos) {
        return new Vec3d(pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D);
    }

    private static Vec3d randomBlockEdgePoint(BlockPos pos) {
        ThreadLocalRandom random = ThreadLocalRandom.current();
        return new Vec3d(
                pos.getX() + 0.5D + random.nextDouble(-0.45D, 0.45D),
                pos.getY() + 0.5D + random.nextDouble(-0.45D, 0.45D),
                pos.getZ() + 0.5D + random.nextDouble(-0.45D, 0.45D)
        );
    }
}
