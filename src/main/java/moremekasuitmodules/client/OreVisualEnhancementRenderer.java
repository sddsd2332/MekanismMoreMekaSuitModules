package moremekasuitmodules.client;

import mekanism.api.gear.IModule;
import mekanism.common.content.gear.ModuleHelper;
import moremekasuitmodules.common.MekaSuitMoreModules;
import moremekasuitmodules.common.MoreMekaSuitModules;
import moremekasuitmodules.common.content.gear.mekanism.mekasuit.ModuleOreVisualEnhancementUnit;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.IResource;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.commons.io.IOUtils;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL14;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@SideOnly(Side.CLIENT)
public class OreVisualEnhancementRenderer {

    private static final double BOX_GROW = 0.002D;
    private static final long WAVE_DURATION_MS = 2_000L;
    private static final long FADE_DURATION_MS = 500L;
    private static final double INITIAL_WAVE_RADIUS = 1.5D;
    private static final long SCANNER_EFFECT_DURATION_MS = 2_000L;
    private static final int SCANNER_TIME_OFFSET_MS = 200;
    private static final ResourceLocation SCANNER_VERTEX_SHADER = new ResourceLocation(MoreMekaSuitModules.MODID, "shaders/ore_visual_scanner.vsh");
    private static final ResourceLocation SCANNER_FRAGMENT_SHADER = new ResourceLocation(MoreMekaSuitModules.MODID, "shaders/ore_visual_scanner.fsh");

    private final Minecraft minecraft = Minecraft.getMinecraft();
    private final FloatBuffer float1Buffer = BufferUtils.createFloatBuffer(1);
    private final FloatBuffer float3Buffer = BufferUtils.createFloatBuffer(3);
    private final FloatBuffer float4Buffer = BufferUtils.createFloatBuffer(4);
    private final FloatBuffer float16Buffer = BufferUtils.createFloatBuffer(16);
    private final Matrix4f projectionMatrix = new Matrix4f();
    private final Matrix4f modelViewMatrix = new Matrix4f();
    private final Matrix4f mvpMatrix = new Matrix4f();
    private final Vector4f tempCorner = new Vector4f();
    private final Vector3f topLeft = new Vector3f();
    private final Vector3f topRight = new Vector3f();
    private final Vector3f bottomLeft = new Vector3f();
    private final Vector3f bottomRight = new Vector3f();

    private int vertexShader;
    private int fragmentShader;
    private int shaderProgram;
    private int camPosUniform;
    private int centerUniform;
    private int radiusUniform;
    private int zNearUniform;
    private int zFarUniform;
    private int aspectUniform;
    private int scanColorUniform;
    private int depthTexUniform;
    private int framebufferObject;
    private int framebufferDepthTexture;
    private long currentScanStart = -1L;
    private long renderedScanStart = -1L;
    private long currentMiningWaveStart = -1L;
    private long renderedMiningWaveStart = -1L;
    private Vec3d scannerCenter = Vec3d.ZERO;
    private double scannerTargetRadius = 1.0D;
    private int scannerColor = ModuleOreVisualEnhancementUnit.DEFAULT_BOX_COLOR;
    private Vec3d miningWaveCenter = Vec3d.ZERO;
    private double miningWaveTargetRadius = 1.0D;
    private int miningWaveColor = ModuleOreVisualEnhancementUnit.DEFAULT_BOX_COLOR;
    private long miningWaveDurationMs = SCANNER_EFFECT_DURATION_MS;

    private static final Vector4f CORNER_TOP_LEFT = new Vector4f(-1.0F, 1.0F, 1.0F, 1.0F);
    private static final Vector4f CORNER_TOP_RIGHT = new Vector4f(1.0F, 1.0F, 1.0F, 1.0F);
    private static final Vector4f CORNER_BOTTOM_LEFT = new Vector4f(-1.0F, -1.0F, 1.0F, 1.0F);
    private static final Vector4f CORNER_BOTTOM_RIGHT = new Vector4f(1.0F, -1.0F, 1.0F, 1.0F);

    @SubscribeEvent
    public void onRenderTick(TickEvent.RenderTickEvent event) {
        if (event.phase == TickEvent.Phase.START) {
            preRenderScannerEffect();
        }
    }

    @SubscribeEvent
    public void renderWorldLast(RenderWorldLastEvent event) {
        EntityPlayer player = minecraft.player;
        if (player == null || minecraft.world == null || minecraft.gameSettings.showDebugInfo) {
            return;
        }
        IModule<ModuleOreVisualEnhancementUnit> module = getEnabledModule(player);
        if (module == null || module.getCustomInstance().getRange() <= 0) {
            return;
        }
        List<OreVisualScanClientCache.OreCluster> allClusters = OreVisualScanClientCache.getClusters();

        RenderManager renderManager = minecraft.getRenderManager();
        double cameraX = renderManager.viewerPosX;
        double cameraY = renderManager.viewerPosY;
        double cameraZ = renderManager.viewerPosZ;
        int fallbackColor = module.getCustomInstance().getBoxColor();
        int textColor = module.getCustomInstance().getTextColor();
        BlockPos scanCenter = OreVisualScanClientCache.getScanCenter();
        long scanStart = OreVisualScanClientCache.getLastUpdateTime();
        long now = System.currentTimeMillis();
        long elapsed = now - scanStart;
        double maxDistance = Math.max(1.0D, module.getCustomInstance().getRange());
        double waveRadius = getWaveRadius(elapsed, maxDistance);
        updateScannerEffect(scanCenter, scanStart, fallbackColor, getRenderDistance());
        updateMiningWaveEffect();
        if (allClusters.isEmpty()) {
            renderScannerEffects(event.getPartialTicks());
            return;
        }
        Vec3d eyes = player.getPositionEyes(event.getPartialTicks());
        Vec3d look = player.getLook(event.getPartialTicks()).normalize();
        List<OreVisualScanClientCache.OreCluster> clusters = new ArrayList<>();

        for (OreVisualScanClientCache.OreCluster cluster : allClusters) {
            double distanceFromScan = Math.sqrt(cluster.getDistanceSq(scanCenter));
            if (distanceFromScan > waveRadius) {
                continue;
            }
            clusters.add(cluster);
        }

        GlStateManager.pushMatrix();
        GlStateManager.disableTexture2D();
        GlStateManager.enableBlend();
        GlStateManager.disableDepth();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        GlStateManager.glLineWidth(2.0F);

        for (OreVisualScanClientCache.OreCluster cluster : clusters) {
            double distanceFromScan = cluster.getDistanceSq(scanCenter);
            float fade = getFade(elapsed, Math.sqrt(distanceFromScan), maxDistance);
            float focus = getFocusFactor(eyes, look, cluster.center);
            int color = cluster.color == 0 ? fallbackColor : cluster.color;
            drawClusterOutline(cluster, color, alpha(color) * fade * focus, cameraX, cameraY, cameraZ);
        }

        GlStateManager.enableDepth();
        GlStateManager.enableTexture2D();
        GlStateManager.popMatrix();

        renderScannerEffects(event.getPartialTicks());

        for (OreVisualScanClientCache.OreCluster cluster : clusters) {
            double distanceFromScan = Math.sqrt(cluster.getDistanceSq(scanCenter));
            float fade = getFade(elapsed, distanceFromScan, maxDistance);
            float focus = getFocusFactor(eyes, look, cluster.center);
            drawLabel(player, cluster, applyAlpha(textColor, fade * focus), cameraX, cameraY, cameraZ, event.getPartialTicks());
        }
    }

    private IModule<ModuleOreVisualEnhancementUnit> getEnabledModule(EntityPlayer player) {
        ItemStack helmet = player.getItemStackFromSlot(EntityEquipmentSlot.HEAD);
        IModule<ModuleOreVisualEnhancementUnit> module = ModuleHelper.get().load(helmet, MekaSuitMoreModules.ORE_VISUAL_ENHANCEMENT_UNIT);
        return module != null && module.isEnabled() ? module : null;
    }

    private void drawLabel(EntityPlayer player, OreVisualScanClientCache.OreCluster cluster, int color, double cameraX, double cameraY, double cameraZ, float partialTicks) {
        FontRenderer font = minecraft.fontRenderer;
        if (font == null) {
            return;
        }
        double x = cluster.center.x - cameraX;
        double y = cluster.center.y - cameraY;
        double z = cluster.center.z - cameraZ;
        double distance = player.getPositionEyes(partialTicks).distanceTo(cluster.center);
        String label = cluster.displayName + " " + String.format("%.1fm", distance);

        GlStateManager.pushMatrix();
        RenderHelper.disableStandardItemLighting();
        GlStateManager.translate(x, y, z);
        GlStateManager.rotate(-minecraft.getRenderManager().playerViewY, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(minecraft.getRenderManager().playerViewX, 1.0F, 0.0F, 0.0F);
        float scale = Math.max(0.018F, Math.min(0.035F, (float) distance * 0.0015F + 0.018F));
        GlStateManager.scale(-scale, -scale, scale);
        GlStateManager.disableDepth();
        GlStateManager.enableBlend();
        int width = font.getStringWidth(label);
        font.drawString(label, -width / 2, 0, color);
        GlStateManager.enableDepth();
        GlStateManager.disableBlend();
        GlStateManager.popMatrix();
    }

    private void drawClusterOutline(OreVisualScanClientCache.OreCluster cluster, int color, float alpha, double cameraX, double cameraY, double cameraZ) {
        if (alpha <= 0.0F) {
            return;
        }
        int red = (color >> 16) & 0xFF;
        int green = (color >> 8) & 0xFF;
        int blue = color & 0xFF;
        int lineAlpha = Math.max(0, Math.min(255, Math.round(alpha * 255.0F)));
        Set<EdgeKey> faceEdges = new HashSet<>();
        for (BlockPos pos : cluster.positions) {
            int x1 = pos.getX();
            int y1 = pos.getY();
            int z1 = pos.getZ();
            int x2 = pos.getX() + 1;
            int y2 = pos.getY() + 1;
            int z2 = pos.getZ() + 1;
            if (!cluster.positions.contains(pos.down())) {
                addYFaceEdges(faceEdges, y1, x1, z1, x2, z2);
            }
            if (!cluster.positions.contains(pos.up())) {
                addYFaceEdges(faceEdges, y2, x1, z1, x2, z2);
            }
            if (!cluster.positions.contains(pos.north())) {
                addZFaceEdges(faceEdges, z1, x1, y1, x2, y2);
            }
            if (!cluster.positions.contains(pos.south())) {
                addZFaceEdges(faceEdges, z2, x1, y1, x2, y2);
            }
            if (!cluster.positions.contains(pos.west())) {
                addXFaceEdges(faceEdges, x1, y1, z1, y2, z2);
            }
            if (!cluster.positions.contains(pos.east())) {
                addXFaceEdges(faceEdges, x2, y1, z1, y2, z2);
            }
        }

        Set<EdgeKey> outlineEdges = new HashSet<>();
        for (EdgeKey edge : faceEdges) {
            outlineEdges.add(edge.withoutFaceGroup());
        }

        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();
        buffer.begin(GL11.GL_LINES, DefaultVertexFormats.POSITION_COLOR);
        for (EdgeKey edge : outlineEdges) {
            addLine(buffer, red, green, blue, lineAlpha,
                  expand(edge.x1, cluster.center.x) - cameraX,
                  expand(edge.y1, cluster.center.y) - cameraY,
                  expand(edge.z1, cluster.center.z) - cameraZ,
                  expand(edge.x2, cluster.center.x) - cameraX,
                  expand(edge.y2, cluster.center.y) - cameraY,
                  expand(edge.z2, cluster.center.z) - cameraZ);
        }
        tessellator.draw();
    }

    private void addYFaceEdges(Set<EdgeKey> edges, int y, int x1, int z1, int x2, int z2) {
        addFaceEdge(edges, 1, x1, y, z1, x2, y, z1);
        addFaceEdge(edges, 1, x2, y, z1, x2, y, z2);
        addFaceEdge(edges, 1, x2, y, z2, x1, y, z2);
        addFaceEdge(edges, 1, x1, y, z2, x1, y, z1);
    }

    private void addZFaceEdges(Set<EdgeKey> edges, int z, int x1, int y1, int x2, int y2) {
        addFaceEdge(edges, 2, x1, y1, z, x2, y1, z);
        addFaceEdge(edges, 2, x2, y1, z, x2, y2, z);
        addFaceEdge(edges, 2, x2, y2, z, x1, y2, z);
        addFaceEdge(edges, 2, x1, y2, z, x1, y1, z);
    }

    private void addXFaceEdges(Set<EdgeKey> edges, int x, int y1, int z1, int y2, int z2) {
        addFaceEdge(edges, 3, x, y1, z1, x, y2, z1);
        addFaceEdge(edges, 3, x, y2, z1, x, y2, z2);
        addFaceEdge(edges, 3, x, y2, z2, x, y1, z2);
        addFaceEdge(edges, 3, x, y1, z2, x, y1, z1);
    }

    private void addFaceEdge(Set<EdgeKey> edges, int faceGroup, int x1, int y1, int z1, int x2, int y2, int z2) {
        EdgeKey edge = new EdgeKey(faceGroup, x1, y1, z1, x2, y2, z2);
        if (!edges.add(edge)) {
            edges.remove(edge);
        }
    }

    private double expand(int value, double center) {
        if (value < center) {
            return value - BOX_GROW;
        }
        if (value > center) {
            return value + BOX_GROW;
        }
        return value;
    }

    private void addLine(BufferBuilder buffer, int red, int green, int blue, int alpha, double x1, double y1, double z1, double x2, double y2, double z2) {
        buffer.pos(x1, y1, z1).color(red, green, blue, alpha).endVertex();
        buffer.pos(x2, y2, z2).color(red, green, blue, alpha).endVertex();
    }

    private float getFocusFactor(Vec3d eyes, Vec3d look, Vec3d center) {
        Vec3d target = center.subtract(eyes).normalize();
        double dot = Math.max(0.0D, look.dotProduct(target));
        double focused = dot * dot * dot * dot;
        return (float) (0.45D + focused * 0.55D);
    }

    private float red(int color) {
        return ((color >> 16) & 0xFF) / 255.0F;
    }

    private float green(int color) {
        return ((color >> 8) & 0xFF) / 255.0F;
    }

    private float blue(int color) {
        return (color & 0xFF) / 255.0F;
    }

    private float alpha(int color) {
        int alpha = (color >> 24) & 0xFF;
        return (alpha == 0 ? 255 : alpha) / 255.0F;
    }

    private double getWaveRadius(long elapsed, double maxDistance) {
        double progress = Math.max(0.0D, Math.min(1.0D, elapsed / (double) WAVE_DURATION_MS));
        double eased = progress * progress;
        return INITIAL_WAVE_RADIUS + (maxDistance - INITIAL_WAVE_RADIUS) * eased;
    }

    private double getRenderDistance() {
        return Math.max(16.0D, minecraft.gameSettings.renderDistanceChunks * 16.0D);
    }

    private float getFade(long elapsed, double distanceFromScan, double maxDistance) {
        double revealTime = WAVE_DURATION_MS * Math.sqrt(Math.max(0.0D, Math.min(1.0D, (distanceFromScan - INITIAL_WAVE_RADIUS) / Math.max(1.0D, maxDistance - INITIAL_WAVE_RADIUS))));
        return (float) Math.max(0.0D, Math.min(1.0D, (elapsed - revealTime) / FADE_DURATION_MS));
    }

    private int applyAlpha(int color, float alphaMultiplier) {
        int alpha = (color >> 24) & 0xFF;
        if (alpha == 0) {
            alpha = 255;
        }
        alpha = Math.max(0, Math.min(255, Math.round(alpha * alphaMultiplier)));
        return (color & 0x00FFFFFF) | (alpha << 24);
    }

    private void updateScannerEffect(BlockPos center, long scanStart, int color, double targetRadius) {
        if (scanStart <= 0L || renderedScanStart == scanStart) {
            return;
        }
        if (!ensureShader()) {
            return;
        }
        renderedScanStart = scanStart;
        currentScanStart = scanStart;
        scannerCenter = new Vec3d(center.getX() + 0.5D, center.getY() + 0.5D, center.getZ() + 0.5D);
        scannerTargetRadius = Math.max(1.0D, targetRadius);
        scannerColor = color;
    }

    private void updateMiningWaveEffect() {
        OreVisualScanClientCache.MiningWave wave = OreVisualScanClientCache.getMiningWave();
        if (wave == null || wave.getStartTime() <= 0L || renderedMiningWaveStart == wave.getStartTime()) {
            return;
        }
        if (!ensureShader()) {
            return;
        }
        renderedMiningWaveStart = wave.getStartTime();
        currentMiningWaveStart = wave.getStartTime();
        BlockPos center = wave.getCenter();
        miningWaveCenter = new Vec3d(center.getX() + 0.5D, center.getY() + 0.5D, center.getZ() + 0.5D);
        miningWaveTargetRadius = Math.max(1.0D, wave.getRadius());
        miningWaveColor = wave.getColor();
        miningWaveDurationMs = Math.max(1L, wave.getDurationMs());
    }

    private void preRenderScannerEffect() {
        if ((currentScanStart < 0L && currentMiningWaveStart < 0L) || shaderProgram == 0 || minecraft.world == null || !OpenGlHelper.isFramebufferEnabled()) {
            return;
        }
        Framebuffer framebuffer = minecraft.getFramebuffer();
        long now = System.currentTimeMillis();
        if (currentScanStart >= 0L && now - currentScanStart > SCANNER_EFFECT_DURATION_MS) {
            currentScanStart = -1L;
        }
        if (currentMiningWaveStart >= 0L && now - currentMiningWaveStart > miningWaveDurationMs) {
            currentMiningWaveStart = -1L;
        }
        if (currentScanStart < 0L && currentMiningWaveStart < 0L) {
            uninstallDepthTexture(framebuffer);
            return;
        }
        if (framebufferDepthTexture == 0) {
            installDepthTexture(framebuffer);
        }
    }

    private void renderScannerEffects(float partialTicks) {
        if ((currentScanStart < 0L && currentMiningWaveStart < 0L) || shaderProgram == 0 || framebufferDepthTexture == 0 || minecraft.world == null) {
            return;
        }
        Entity viewer = minecraft.getRenderViewEntity();
        if (viewer == null) {
            return;
        }

        setupCorners();

        Framebuffer framebuffer = minecraft.getFramebuffer();
        GlStateManager.pushMatrix();
        GlStateManager.pushAttrib();
        GlStateManager.depthMask(false);
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);

        if (framebuffer.isStencilEnabled()) {
            OpenGlHelper.glFramebufferRenderbuffer(OpenGlHelper.GL_FRAMEBUFFER, GL30.GL_DEPTH_ATTACHMENT, OpenGlHelper.GL_RENDERBUFFER, framebuffer.depthBuffer);
            OpenGlHelper.glFramebufferRenderbuffer(OpenGlHelper.GL_FRAMEBUFFER, GL30.GL_STENCIL_ATTACHMENT, OpenGlHelper.GL_RENDERBUFFER, framebuffer.depthBuffer);
        } else {
            OpenGlHelper.glFramebufferRenderbuffer(OpenGlHelper.GL_FRAMEBUFFER, OpenGlHelper.GL_DEPTH_ATTACHMENT, OpenGlHelper.GL_RENDERBUFFER, framebuffer.depthBuffer);
        }

        GlStateManager.bindTexture(framebufferDepthTexture);

        int oldProgram = GlStateManager.glGetInteger(GL20.GL_CURRENT_PROGRAM);
        OpenGlHelper.glUseProgram(shaderProgram);
        OpenGlHelper.glUniform1i(depthTexUniform, 0);
        setUniform(camPosUniform, viewer.getPositionEyes(partialTicks));
        setUniform(zNearUniform, 0.05F);
        setUniform(zFarUniform, minecraft.gameSettings.renderDistanceChunks * 16.0F);
        setUniform(aspectUniform, framebuffer.framebufferTextureWidth / (float) framebuffer.framebufferTextureHeight);

        setupOverlayMatrices(framebuffer.framebufferTextureWidth, framebuffer.framebufferTextureHeight);

        if (currentScanStart >= 0L) {
            renderScannerPass(framebuffer, scannerCenter, computeScannerRadius(), scannerColor);
        }
        if (currentMiningWaveStart >= 0L) {
            renderScannerPass(framebuffer, miningWaveCenter, computeMiningWaveRadius(), miningWaveColor);
        }

        restoreOverlayMatrices();
        OpenGlHelper.glUseProgram(oldProgram);
        GlStateManager.bindTexture(0);

        if (framebuffer.isStencilEnabled()) {
            OpenGlHelper.glFramebufferTexture2D(OpenGlHelper.GL_FRAMEBUFFER, GL30.GL_DEPTH_STENCIL_ATTACHMENT, GL11.GL_TEXTURE_2D, framebufferDepthTexture, 0);
        } else {
            OpenGlHelper.glFramebufferTexture2D(OpenGlHelper.GL_FRAMEBUFFER, OpenGlHelper.GL_DEPTH_ATTACHMENT, GL11.GL_TEXTURE_2D, framebufferDepthTexture, 0);
        }

        GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GlStateManager.disableBlend();
        GlStateManager.depthMask(true);
        GlStateManager.popAttrib();
        GlStateManager.popMatrix();
    }

    private void renderScannerPass(Framebuffer framebuffer, Vec3d center, float radius, int color) {
        setUniform(centerUniform, center);
        setUniform(radiusUniform, radius);
        setColorUniform(scanColorUniform, color);

        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();
        buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_NORMAL);
        buffer.pos(0, framebuffer.framebufferTextureHeight, 0).tex(0, 0).normal(bottomLeft.x, bottomLeft.y, bottomLeft.z).endVertex();
        buffer.pos(framebuffer.framebufferTextureWidth, framebuffer.framebufferTextureHeight, 0).tex(1, 0).normal(bottomRight.x, bottomRight.y, bottomRight.z).endVertex();
        buffer.pos(framebuffer.framebufferTextureWidth, 0, 0).tex(1, 1).normal(topRight.x, topRight.y, topRight.z).endVertex();
        buffer.pos(0, 0, 0).tex(0, 1).normal(topLeft.x, topLeft.y, topLeft.z).endVertex();
        tessellator.draw();
    }

    private float computeScannerRadius() {
        double elapsed = Math.max(0.0D, System.currentTimeMillis() - currentScanStart);
        double target = Math.max(1.0D, scannerTargetRadius);
        double offset = SCANNER_TIME_OFFSET_MS;
        double duration = SCANNER_EFFECT_DURATION_MS;
        double factor = 1.0D / ((duration + offset) * (duration + offset) - offset * offset);
        double a = -target * offset * offset * factor;
        double c = target * factor;
        return (float) Math.max(0.0D, a + (elapsed + offset) * (elapsed + offset) * c);
    }

    private float computeMiningWaveRadius() {
        double elapsed = Math.max(0.0D, System.currentTimeMillis() - currentMiningWaveStart);
        double target = Math.max(1.0D, miningWaveTargetRadius);
        double offset = SCANNER_TIME_OFFSET_MS;
        double duration = Math.max(1.0D, miningWaveDurationMs);
        double factor = 1.0D / ((duration + offset) * (duration + offset) - offset * offset);
        double a = -target * offset * offset * factor;
        double c = target * factor;
        return (float) Math.max(0.0D, a + (elapsed + offset) * (elapsed + offset) * c);
    }

    private boolean ensureShader() {
        if (shaderProgram != 0) {
            return true;
        }
        try {
            IResourceManager resourceManager = minecraft.getResourceManager();
            vertexShader = loadShader(resourceManager, GL20.GL_VERTEX_SHADER, SCANNER_VERTEX_SHADER);
            fragmentShader = loadShader(resourceManager, GL20.GL_FRAGMENT_SHADER, SCANNER_FRAGMENT_SHADER);
            shaderProgram = linkProgram(vertexShader, fragmentShader);
            camPosUniform = OpenGlHelper.glGetUniformLocation(shaderProgram, "camPos");
            centerUniform = OpenGlHelper.glGetUniformLocation(shaderProgram, "center");
            radiusUniform = OpenGlHelper.glGetUniformLocation(shaderProgram, "radius");
            zNearUniform = OpenGlHelper.glGetUniformLocation(shaderProgram, "zNear");
            zFarUniform = OpenGlHelper.glGetUniformLocation(shaderProgram, "zFar");
            aspectUniform = OpenGlHelper.glGetUniformLocation(shaderProgram, "aspect");
            scanColorUniform = OpenGlHelper.glGetUniformLocation(shaderProgram, "scanColor");
            depthTexUniform = OpenGlHelper.glGetUniformLocation(shaderProgram, "depthTex");
            return true;
        } catch (Exception ignored) {
            deleteShader();
            return false;
        }
    }

    private int loadShader(IResourceManager resourceManager, int type, ResourceLocation location) throws Exception {
        int shader = OpenGlHelper.glCreateShader(type);
        try (IResource resource = resourceManager.getResource(location); InputStream stream = resource.getInputStream()) {
            byte[] bytes = IOUtils.toByteArray(stream);
            ByteBuffer buffer = BufferUtils.createByteBuffer(bytes.length);
            buffer.put(bytes);
            buffer.rewind();
            OpenGlHelper.glShaderSource(shader, buffer);
        }
        OpenGlHelper.glCompileShader(shader);
        if (OpenGlHelper.glGetShaderi(shader, GL20.GL_COMPILE_STATUS) == GL11.GL_FALSE) {
            throw new Exception(OpenGlHelper.glGetShaderInfoLog(shader, 4096));
        }
        return shader;
    }

    private int linkProgram(int vertexShader, int fragmentShader) throws Exception {
        int program = OpenGlHelper.glCreateProgram();
        OpenGlHelper.glAttachShader(program, vertexShader);
        OpenGlHelper.glAttachShader(program, fragmentShader);
        OpenGlHelper.glLinkProgram(program);
        if (OpenGlHelper.glGetProgrami(program, GL20.GL_LINK_STATUS) == GL11.GL_FALSE) {
            throw new Exception(OpenGlHelper.glGetProgramInfoLog(program, 4096));
        }
        return program;
    }

    private void deleteShader() {
        if (shaderProgram != 0) {
            OpenGlHelper.glDeleteProgram(shaderProgram);
            shaderProgram = 0;
        }
        if (vertexShader != 0) {
            OpenGlHelper.glDeleteShader(vertexShader);
            vertexShader = 0;
        }
        if (fragmentShader != 0) {
            OpenGlHelper.glDeleteShader(fragmentShader);
            fragmentShader = 0;
        }
    }

    private void installDepthTexture(Framebuffer framebuffer) {
        int oldFramebuffer = GlStateManager.glGetInteger(GL30.GL_FRAMEBUFFER_BINDING);
        framebufferObject = framebuffer.framebufferObject;
        if (framebuffer.isStencilEnabled()) {
            framebufferDepthTexture = createTexture(framebuffer.framebufferTextureWidth, framebuffer.framebufferTextureHeight, GL30.GL_DEPTH24_STENCIL8, GL30.GL_DEPTH_STENCIL, GL30.GL_UNSIGNED_INT_24_8);
            OpenGlHelper.glBindFramebuffer(OpenGlHelper.GL_FRAMEBUFFER, framebufferObject);
            OpenGlHelper.glFramebufferTexture2D(OpenGlHelper.GL_FRAMEBUFFER, GL30.GL_DEPTH_STENCIL_ATTACHMENT, GL11.GL_TEXTURE_2D, framebufferDepthTexture, 0);
        } else {
            framebufferDepthTexture = createTexture(framebuffer.framebufferTextureWidth, framebuffer.framebufferTextureHeight, GL14.GL_DEPTH_COMPONENT24, GL11.GL_DEPTH_COMPONENT, GL11.GL_UNSIGNED_INT);
            OpenGlHelper.glBindFramebuffer(OpenGlHelper.GL_FRAMEBUFFER, framebufferObject);
            OpenGlHelper.glFramebufferTexture2D(OpenGlHelper.GL_FRAMEBUFFER, OpenGlHelper.GL_DEPTH_ATTACHMENT, GL11.GL_TEXTURE_2D, framebufferDepthTexture, 0);
        }
        OpenGlHelper.glBindFramebuffer(OpenGlHelper.GL_FRAMEBUFFER, oldFramebuffer);
    }

    private void uninstallDepthTexture(Framebuffer framebuffer) {
        if (framebufferDepthTexture == 0) {
            return;
        }
        OpenGlHelper.glBindFramebuffer(OpenGlHelper.GL_FRAMEBUFFER, framebufferObject);
        if (framebuffer.isStencilEnabled()) {
            OpenGlHelper.glFramebufferRenderbuffer(OpenGlHelper.GL_FRAMEBUFFER, GL30.GL_DEPTH_ATTACHMENT, OpenGlHelper.GL_RENDERBUFFER, framebuffer.depthBuffer);
            OpenGlHelper.glFramebufferRenderbuffer(OpenGlHelper.GL_FRAMEBUFFER, GL30.GL_STENCIL_ATTACHMENT, OpenGlHelper.GL_RENDERBUFFER, framebuffer.depthBuffer);
        } else {
            OpenGlHelper.glFramebufferRenderbuffer(OpenGlHelper.GL_FRAMEBUFFER, OpenGlHelper.GL_DEPTH_ATTACHMENT, OpenGlHelper.GL_RENDERBUFFER, framebuffer.depthBuffer);
        }
        TextureUtil.deleteTexture(framebufferDepthTexture);
        framebufferObject = 0;
        framebufferDepthTexture = 0;
    }

    private int createTexture(int width, int height, int internalFormat, int format, int type) {
        int texture = TextureUtil.glGenTextures();
        GlStateManager.bindTexture(texture);
        GlStateManager.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL11.GL_REPEAT);
        GlStateManager.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL11.GL_REPEAT);
        GlStateManager.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
        GlStateManager.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
        GlStateManager.glTexParameteri(GL11.GL_TEXTURE_2D, GL14.GL_DEPTH_TEXTURE_MODE, GL11.GL_LUMINANCE);
        GlStateManager.glTexParameteri(GL11.GL_TEXTURE_2D, GL14.GL_TEXTURE_COMPARE_FUNC, GL11.GL_LEQUAL);
        GlStateManager.glTexImage2D(GL11.GL_TEXTURE_2D, 0, internalFormat, width, height, 0, format, type, null);
        GlStateManager.bindTexture(0);
        return texture;
    }

    private void setupCorners() {
        getMatrix(GL11.GL_PROJECTION_MATRIX, projectionMatrix);
        getMatrix(GL11.GL_MODELVIEW_MATRIX, modelViewMatrix);
        Matrix4f.mul(projectionMatrix, modelViewMatrix, mvpMatrix);
        mvpMatrix.invert();
        setupCorner(CORNER_TOP_LEFT, topLeft);
        setupCorner(CORNER_TOP_RIGHT, topRight);
        setupCorner(CORNER_BOTTOM_LEFT, bottomLeft);
        setupCorner(CORNER_BOTTOM_RIGHT, bottomRight);
    }

    private void setupCorner(Vector4f corner, Vector3f into) {
        Matrix4f.transform(mvpMatrix, corner, tempCorner);
        tempCorner.scale(1.0F / tempCorner.w);
        into.set(tempCorner);
        into.normalise();
    }

    private void setupOverlayMatrices(int width, int height) {
        GlStateManager.matrixMode(GL11.GL_PROJECTION);
        GlStateManager.pushMatrix();
        GlStateManager.loadIdentity();
        GlStateManager.ortho(0, width, height, 0, 1000, 3000);
        GlStateManager.matrixMode(GL11.GL_MODELVIEW);
        GlStateManager.pushMatrix();
        GlStateManager.loadIdentity();
        GlStateManager.translate(0, 0, -2000);
        GlStateManager.viewport(0, 0, width, height);
    }

    private void restoreOverlayMatrices() {
        GlStateManager.matrixMode(GL11.GL_PROJECTION);
        GlStateManager.popMatrix();
        GlStateManager.matrixMode(GL11.GL_MODELVIEW);
        GlStateManager.popMatrix();
    }

    private void getMatrix(int matrix, Matrix4f into) {
        float16Buffer.position(0);
        GlStateManager.getFloat(matrix, float16Buffer);
        float16Buffer.position(0);
        into.load(float16Buffer);
    }

    private void setUniform(int uniform, float value) {
        float1Buffer.clear();
        float1Buffer.put(value);
        float1Buffer.rewind();
        OpenGlHelper.glUniform1(uniform, float1Buffer);
    }

    private void setUniform(int uniform, Vec3d value) {
        float3Buffer.clear();
        float3Buffer.put((float) value.x);
        float3Buffer.put((float) value.y);
        float3Buffer.put((float) value.z);
        float3Buffer.rewind();
        OpenGlHelper.glUniform3(uniform, float3Buffer);
    }

    private void setColorUniform(int uniform, int color) {
        int alpha = (color >> 24) & 0xFF;
        if (alpha == 0) {
            alpha = 255;
        }
        float4Buffer.clear();
        float4Buffer.put(red(color));
        float4Buffer.put(green(color));
        float4Buffer.put(blue(color));
        float4Buffer.put(alpha / 255.0F);
        float4Buffer.rewind();
        OpenGlHelper.glUniform4(uniform, float4Buffer);
    }

    private static class EdgeKey {

        private final int faceGroup;
        private final int x1;
        private final int y1;
        private final int z1;
        private final int x2;
        private final int y2;
        private final int z2;

        private EdgeKey(int faceGroup, int x1, int y1, int z1, int x2, int y2, int z2) {
            this.faceGroup = faceGroup;
            if (compare(x1, y1, z1, x2, y2, z2) <= 0) {
                this.x1 = x1;
                this.y1 = y1;
                this.z1 = z1;
                this.x2 = x2;
                this.y2 = y2;
                this.z2 = z2;
            } else {
                this.x1 = x2;
                this.y1 = y2;
                this.z1 = z2;
                this.x2 = x1;
                this.y2 = y1;
                this.z2 = z1;
            }
        }

        private EdgeKey withoutFaceGroup() {
            return new EdgeKey(0, x1, y1, z1, x2, y2, z2);
        }

        private static int compare(int x1, int y1, int z1, int x2, int y2, int z2) {
            if (x1 != x2) {
                return Integer.compare(x1, x2);
            }
            if (y1 != y2) {
                return Integer.compare(y1, y2);
            }
            return Integer.compare(z1, z2);
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (!(obj instanceof EdgeKey other)) {
                return false;
            }
            return faceGroup == other.faceGroup && x1 == other.x1 && y1 == other.y1 && z1 == other.z1 && x2 == other.x2 && y2 == other.y2 && z2 == other.z2;
        }

        @Override
        public int hashCode() {
            int result = faceGroup;
            result = 31 * result + x1;
            result = 31 * result + y1;
            result = 31 * result + z1;
            result = 31 * result + x2;
            result = 31 * result + y2;
            result = 31 * result + z2;
            return result;
        }
    }
}
