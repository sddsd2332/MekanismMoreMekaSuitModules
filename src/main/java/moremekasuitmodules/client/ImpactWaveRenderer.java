package moremekasuitmodules.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.client.event.EntityViewRenderEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@SideOnly(Side.CLIENT)
public class ImpactWaveRenderer {

    private static final List<ImpactWave> WAVES = new ArrayList<>();
    private static final int SEGMENTS = 96;
    private static final float MAX_SHAKE_DEGREES = 3.5F;

    public static void addWave(double x, double y, double z, float radius, int color, int durationTicks, int sourceEntityId, float fallDistance) {
        WAVES.add(new ImpactWave(x, y, z, radius, color, Math.max(6, durationTicks), sourceEntityId));
        spawnImpactParticles(x, y, z, radius, fallDistance);
    }

    @SubscribeEvent
    public void onRenderWorldLast(RenderWorldLastEvent event) {
        if (WAVES.isEmpty()) {
            return;
        }
        Minecraft minecraft = Minecraft.getMinecraft();
        if (minecraft.player == null || minecraft.world == null) {
            WAVES.clear();
            return;
        }

        float partialTicks = event.getPartialTicks();
        Vec3d camera = getCamera(minecraft, partialTicks);
        GlStateManager.pushMatrix();
        GlStateManager.translate(-camera.x, -camera.y, -camera.z);
        GlStateManager.disableTexture2D();
        GlStateManager.disableLighting();
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ONE, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GlStateManager.disableCull();
        GlStateManager.depthMask(false);
        GlStateManager.disableDepth();
        GL11.glLineWidth(2.4F);

        Iterator<ImpactWave> iterator = WAVES.iterator();
        while (iterator.hasNext()) {
            ImpactWave wave = iterator.next();
            if (wave.isFinished()) {
                iterator.remove();
                continue;
            }
            renderWave(wave, partialTicks);
        }

        GL11.glLineWidth(1.0F);
        GlStateManager.enableDepth();
        GlStateManager.depthMask(true);
        GlStateManager.enableCull();
        GlStateManager.disableBlend();
        GlStateManager.enableLighting();
        GlStateManager.enableTexture2D();
        GlStateManager.popMatrix();
    }

    @SubscribeEvent
    public void onCameraSetup(EntityViewRenderEvent.CameraSetup event) {
        Minecraft minecraft = Minecraft.getMinecraft();
        if (minecraft.player == null || minecraft.world == null || WAVES.isEmpty()) {
            return;
        }
        if (WAVES.stream().allMatch(wave -> wave.sourceEntityId == minecraft.player.getEntityId())) {
            return;
        }
        Vec3d playerPos = minecraft.player.getPositionEyes((float) event.getRenderPartialTicks());
        float shake = 0.0F;
        long time = minecraft.world.getTotalWorldTime();
        for (ImpactWave wave : WAVES) {
            if (wave.sourceEntityId == minecraft.player.getEntityId()) {
                continue;
            }
            float progress = wave.getProgress(minecraft);
            if (progress <= 0.0F || progress >= 1.0F) {
                continue;
            }
            double dx = playerPos.x - wave.x;
            double dz = playerPos.z - wave.z;
            double horizontalDistance = Math.sqrt(dx * dx + dz * dz);
            if (horizontalDistance > wave.radius) {
                continue;
            }
            double currentRadius = wave.radius * progress * progress;
            double ringDistance = Math.abs(horizontalDistance - currentRadius);
            double ringWidth = Math.max(0.75D, wave.radius * 0.16D);
            if (ringDistance > ringWidth) {
                continue;
            }
            float distanceFalloff = 1.0F - (float) (horizontalDistance / Math.max(1.0D, wave.radius));
            float ringFalloff = 1.0F - (float) (ringDistance / ringWidth);
            shake = Math.max(shake, ringFalloff * (0.35F + 0.65F * distanceFalloff));
        }
        if (shake <= 0.0F) {
            return;
        }

        double t = (time + event.getRenderPartialTicks()) * 1.85D;
        float amount = shake * MAX_SHAKE_DEGREES;
        event.setYaw(event.getYaw() + (float) Math.sin(t * 1.7D) * amount * 0.45F);
        event.setPitch(event.getPitch() + (float) Math.cos(t * 2.1D) * amount * 0.35F);
        event.setRoll(event.getRoll() + (float) Math.sin(t * 2.9D) * amount);
    }

    private void renderWave(ImpactWave wave, float partialTicks) {
        float progress = wave.getProgress(Minecraft.getMinecraft(), partialTicks);
        float eased = progress * progress;
        double radius = wave.radius * eased;
        double innerRadius = Math.max(0.05D, radius - 0.28D - wave.radius * 0.04D);
        float alpha = getAlpha(wave.color) * 0.72F;
        float r = ((wave.color >> 16) & 0xFF) / 255.0F;
        float g = ((wave.color >> 8) & 0xFF) / 255.0F;
        float b = (wave.color & 0xFF) / 255.0F;

        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();
        buffer.begin(GL11.GL_QUAD_STRIP, DefaultVertexFormats.POSITION_COLOR);
        for (int i = 0; i <= SEGMENTS; i++) {
            double angle = Math.PI * 2.0D * i / SEGMENTS;
            double sin = Math.sin(angle);
            double cos = Math.cos(angle);
            buffer.pos(wave.x + cos * radius, wave.y, wave.z + sin * radius).color(r, g, b, alpha * 0.35F).endVertex();
            buffer.pos(wave.x + cos * innerRadius, wave.y + 0.018D, wave.z + sin * innerRadius).color(r, g, b, alpha).endVertex();
        }
        tessellator.draw();
        spawnWaveFrontSmoke(wave, radius, progress);
    }

    private float getAlpha(int color) {
        int alpha = (color >> 24) & 0xFF;
        return (alpha == 0 ? 0x88 : alpha) / 255.0F;
    }

    private Vec3d getCamera(Minecraft minecraft, float partialTicks) {
        double x = minecraft.player.lastTickPosX + (minecraft.player.posX - minecraft.player.lastTickPosX) * partialTicks;
        double y = minecraft.player.lastTickPosY + (minecraft.player.posY - minecraft.player.lastTickPosY) * partialTicks;
        double z = minecraft.player.lastTickPosZ + (minecraft.player.posZ - minecraft.player.lastTickPosZ) * partialTicks;
        return new Vec3d(x, y, z);
    }

    private static void spawnImpactParticles(double x, double y, double z, float radius, float fallDistance) {
        Minecraft minecraft = Minecraft.getMinecraft();
        if (minecraft.world == null) {
            return;
        }
        float strength = Math.max(0.0F, Math.min(1.0F, (fallDistance - 6.0F) / 64.0F));
        int count = 48 + Math.round(strength * 220.0F);
        double burstRadius = 0.35D + strength * 0.75D;
        double height = 0.7D + strength * 2.2D;
        for (int i = 0; i < count; i++) {
            double angle = Math.PI * 2.0D * i / count;
            double speed = 0.025D + minecraft.world.rand.nextDouble() * (0.055D + 0.08D * strength);
            double spread = Math.sqrt(minecraft.world.rand.nextDouble()) * burstRadius;
            double px = x + Math.cos(angle) * spread;
            double pz = z + Math.sin(angle) * spread;
            double py = y + 0.05D + minecraft.world.rand.nextDouble() * height;
            double mx = Math.cos(angle) * speed;
            double mz = Math.sin(angle) * speed;
            double my = 0.01D + minecraft.world.rand.nextDouble() * (0.045D + 0.07D * strength);
            minecraft.world.spawnParticle(EnumParticleTypes.SMOKE_LARGE, px, py, pz, mx, my, mz);
            if (i % 2 == 0) {
                minecraft.world.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, px, py + 0.05D, pz, mx * 0.7D, my * 0.8D, mz * 0.7D);
            }
        }
        int verticalCount = Math.round(20 + strength * 90.0F);
        for (int i = 0; i < verticalCount; i++) {
            double angle = minecraft.world.rand.nextDouble() * Math.PI * 2.0D;
            double ring = 0.45D + minecraft.world.rand.nextDouble() * 0.75D;
            double py = y + minecraft.world.rand.nextDouble() * (height + 0.8D);
            double px = x + Math.cos(angle) * ring;
            double pz = z + Math.sin(angle) * ring;
            minecraft.world.spawnParticle(EnumParticleTypes.SMOKE_LARGE, px, py, pz, Math.cos(angle) * 0.025D, 0.025D + minecraft.world.rand.nextDouble() * 0.04D, Math.sin(angle) * 0.025D);
        }
    }

    private void spawnWaveFrontSmoke(ImpactWave wave, double currentRadius, float progress) {
        Minecraft minecraft = Minecraft.getMinecraft();
        if (minecraft.world == null || currentRadius < 0.5D || progress <= 0.02F || progress >= 1.0F) {
            return;
        }
        int count = Math.max(1, Math.round(wave.radius / 4.0F));
        double ringWidth = Math.max(0.35D, wave.radius * 0.035D);
        for (int i = 0; i < count; i++) {
            if (minecraft.world.rand.nextFloat() > 0.65F) {
                continue;
            }
            double angle = minecraft.world.rand.nextDouble() * Math.PI * 2.0D;
            double distance = currentRadius + (minecraft.world.rand.nextDouble() - 0.5D) * ringWidth;
            if (distance < 0.2D || distance > wave.radius) {
                continue;
            }
            double x = wave.x + Math.cos(angle) * distance;
            double z = wave.z + Math.sin(angle) * distance;
            double y = wave.y + 0.05D + minecraft.world.rand.nextDouble() * 0.25D;
            double speed = 0.015D + minecraft.world.rand.nextDouble() * 0.025D;
            minecraft.world.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, x, y, z,
                    Math.cos(angle) * speed, 0.015D + minecraft.world.rand.nextDouble() * 0.025D, Math.sin(angle) * speed);
        }
    }

    private static class ImpactWave {

        private final double x;
        private final double y;
        private final double z;
        private final float radius;
        private final int color;
        private final long startWorldTick;
        private final int durationTicks;
        private final int sourceEntityId;

        private ImpactWave(double x, double y, double z, float radius, int color, int durationTicks, int sourceEntityId) {
            this.x = x;
            this.y = y;
            this.z = z;
            this.radius = radius;
            this.color = color;
            Minecraft minecraft = Minecraft.getMinecraft();
            this.startWorldTick = minecraft.world == null ? 0L : minecraft.world.getTotalWorldTime();
            this.durationTicks = Math.max(1, durationTicks);
            this.sourceEntityId = sourceEntityId;
        }

        private float getProgress(Minecraft minecraft) {
            return getProgress(minecraft, 0.0F);
        }

        private float getProgress(Minecraft minecraft, float partialTicks) {
            if (minecraft.world == null) {
                return 1.0F;
            }
            return Math.min(1.0F, Math.max(0.0F, (minecraft.world.getTotalWorldTime() - startWorldTick + partialTicks) / durationTicks));
        }

        private boolean isFinished() {
            Minecraft minecraft = Minecraft.getMinecraft();
            return minecraft.world == null || minecraft.world.getTotalWorldTime() - startWorldTick > durationTicks;
        }
    }
}
