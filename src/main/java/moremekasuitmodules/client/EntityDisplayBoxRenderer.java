package moremekasuitmodules.client;

import mekanism.api.gear.IModule;
import mekanism.common.content.gear.ModuleHelper;
import moremekasuitmodules.common.MekaSuitMoreModules;
import moremekasuitmodules.common.content.gear.mekanism.mekasuit.ModuleEntityDisplayBoxUnit;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.culling.ICamera;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.GLU;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@SideOnly(Side.CLIENT)
public class EntityDisplayBoxRenderer {

    private static final double MIN_HORIZONTAL_PADDING = 0.2D;
    private static final double HORIZONTAL_PADDING_SCALE = 0.18D;
    private static final double VERTICAL_PADDING_SCALE = 0.08D;
    private static final double TOP_PADDING_SCALE = 0.12D;
    private static final double SCREEN_PADDING = 2.0D;
    private static final int LABEL_GAP = 4;
    private static final int LABEL_LINE_GAP = 1;
    private static final int LABEL_SCREEN_MARGIN = 1;
    private static final int HEALTH_BAR_WIDTH = 3;
    private static final int HEALTH_BAR_GAP = 3;
    private static final int HEALTH_BAR_BORDER = 1;
    private static final int HEALTH_TEXT_GAP = 2;
    private static final float HEALTH_TEXT_MAX_DISTANCE = 24.0F;

    private final Minecraft minecraft = Minecraft.getMinecraft();
    private final List<DisplayBox> displayBoxes = new ArrayList<>();
    private final FloatBuffer modelView = GLAllocation.createDirectFloatBuffer(16);
    private final FloatBuffer projection = GLAllocation.createDirectFloatBuffer(16);
    private final IntBuffer viewport = GLAllocation.createDirectIntBuffer(16);
    private final FloatBuffer projected = BufferUtils.createFloatBuffer(3);

    private int boxColor = ModuleEntityDisplayBoxUnit.DEFAULT_BOX_COLOR;
    private int nameColor = ModuleEntityDisplayBoxUnit.DEFAULT_NAME_COLOR;
    private int distanceColor = ModuleEntityDisplayBoxUnit.DEFAULT_DISTANCE_COLOR;
    private ModuleEntityDisplayBoxUnit.HealthDisplay healthDisplay = ModuleEntityDisplayBoxUnit.HealthDisplay.OFF;

    @SubscribeEvent
    public void renderWorldLast(RenderWorldLastEvent event) {
        displayBoxes.clear();
        EntityPlayer player = minecraft.player;
        if (player == null || minecraft.world == null || minecraft.gameSettings.showDebugInfo) {
            return;
        }
        IModule<ModuleEntityDisplayBoxUnit> module = getEnabledModule(player);
        if (module == null) {
            return;
        }
        ModuleEntityDisplayBoxUnit unit = module.getCustomInstance();
        int radius = unit.getRange();
        if (radius <= 0) {
            return;
        }
        boxColor = unit.getBoxColor();
        nameColor = unit.getNameColor();
        distanceColor = unit.getDistanceColor();
        healthDisplay = unit.getHealthDisplay();
        int maxBoxes = unit.getMaxBoxes();

        GlStateManager.getFloat(GL11.GL_MODELVIEW_MATRIX, modelView);
        GlStateManager.getFloat(GL11.GL_PROJECTION_MATRIX, projection);
        GlStateManager.glGetInteger(GL11.GL_VIEWPORT, viewport);

        double radiusSq = radius * radius;
        AxisAlignedBB searchBox = player.getEntityBoundingBox().grow(radius);
        List<EntityLivingBase> targets = minecraft.world.getEntitiesWithinAABB(EntityLivingBase.class, searchBox, target -> isValidTarget(player, target, radiusSq));
        if (targets.isEmpty()) {
            return;
        }
        targets.sort(Comparator.comparingDouble(player::getDistanceSq));
        RenderManager renderManager = minecraft.getRenderManager();
        ScaledResolution resolution = new ScaledResolution(minecraft);
        ICamera camera = new Frustum();
        camera.setPosition(renderManager.viewerPosX, renderManager.viewerPosY, renderManager.viewerPosZ);
        for (EntityLivingBase target : targets) {
            if (displayBoxes.size() >= maxBoxes) {
                break;
            }
            DisplayBox box = projectTarget(target, player, renderManager, resolution, camera, event.getPartialTicks());
            if (box != null) {
                displayBoxes.add(box);
            }
        }
    }

    @SubscribeEvent
    public void renderOverlay(RenderGameOverlayEvent.Post event) {
        if (event.getType() != RenderGameOverlayEvent.ElementType.ALL || displayBoxes.isEmpty()) {
            return;
        }
        FontRenderer font = minecraft.fontRenderer;
        if (font == null) {
            return;
        }
        GlStateManager.pushMatrix();
        GlStateManager.disableDepth();
        RenderHelper.disableStandardItemLighting();
        for (DisplayBox box : displayBoxes) {
            drawBox(box);
            drawHealth(font, box, event.getResolution());
            drawLabels(font, box, event.getResolution());
        }
        GlStateManager.enableDepth();
        GlStateManager.popMatrix();
    }

    private IModule<ModuleEntityDisplayBoxUnit> getEnabledModule(EntityPlayer player) {
        ItemStack helmet = player.getItemStackFromSlot(EntityEquipmentSlot.HEAD);
        IModule<ModuleEntityDisplayBoxUnit> module = ModuleHelper.get().load(helmet, MekaSuitMoreModules.ENTITY_DISPLAY_BOX_UNIT);
        return module != null && module.isEnabled() ? module : null;
    }

    private boolean isValidTarget(EntityPlayer player, EntityLivingBase target, double radiusSq) {
        if (target == null || target == player || !target.isEntityAlive()) {
            return false;
        }
        if (target instanceof EntityPlayer && ((EntityPlayer) target).isSpectator()) {
            return false;
        }
        return player.getDistanceSq(target) <= radiusSq;
    }

    private DisplayBox projectTarget(EntityLivingBase target, EntityPlayer player, RenderManager renderManager, ScaledResolution resolution, ICamera camera, float partialTicks) {
        AxisAlignedBB bb = getPaddedBoundingBox(target, partialTicks);
        if (!camera.isBoundingBoxInFrustum(bb)) {
            return null;
        }
        ScreenBounds bounds = new ScreenBounds();
        if (!project(bounds, bb.minX, bb.minY, bb.minZ, renderManager, resolution)
                | !project(bounds, bb.minX, bb.minY, bb.maxZ, renderManager, resolution)
                | !project(bounds, bb.minX, bb.maxY, bb.minZ, renderManager, resolution)
                | !project(bounds, bb.minX, bb.maxY, bb.maxZ, renderManager, resolution)
                | !project(bounds, bb.maxX, bb.minY, bb.minZ, renderManager, resolution)
                | !project(bounds, bb.maxX, bb.minY, bb.maxZ, renderManager, resolution)
                | !project(bounds, bb.maxX, bb.maxY, bb.minZ, renderManager, resolution)
                | !project(bounds, bb.maxX, bb.maxY, bb.maxZ, renderManager, resolution)) {
            return null;
        }
        if (!bounds.isValid(resolution)) {
            return null;
        }
        return new DisplayBox(
                bounds.minX - SCREEN_PADDING,
                bounds.minY - SCREEN_PADDING,
                bounds.maxX + SCREEN_PADDING,
                bounds.maxY + SCREEN_PADDING,
                target.getName(),
                player.getDistance(target),
                target.getHealth(),
                target.getMaxHealth()
        );
    }

    private AxisAlignedBB getPaddedBoundingBox(EntityLivingBase target, float partialTicks) {
        double x = target.lastTickPosX + (target.posX - target.lastTickPosX) * partialTicks;
        double y = target.lastTickPosY + (target.posY - target.lastTickPosY) * partialTicks;
        double z = target.lastTickPosZ + (target.posZ - target.lastTickPosZ) * partialTicks;
        double offsetX = x - target.posX;
        double offsetY = y - target.posY;
        double offsetZ = z - target.posZ;
        AxisAlignedBB bb = target.getRenderBoundingBox().offset(offsetX, offsetY, offsetZ);
        double horizontalPadding = Math.max(MIN_HORIZONTAL_PADDING, target.width * HORIZONTAL_PADDING_SCALE);
        double verticalPadding = Math.max(0.05D, target.height * VERTICAL_PADDING_SCALE);
        double topPadding = Math.max(0.1D, target.height * TOP_PADDING_SCALE);
        return new AxisAlignedBB(
                bb.minX - horizontalPadding,
                bb.minY - verticalPadding,
                bb.minZ - horizontalPadding,
                bb.maxX + horizontalPadding,
                bb.maxY + topPadding,
                bb.maxZ + horizontalPadding
        );
    }

    private boolean project(ScreenBounds bounds, double x, double y, double z, RenderManager renderManager, ScaledResolution resolution) {
        projected.clear();
        boolean success = GLU.gluProject(
                (float) (x - renderManager.viewerPosX),
                (float) (y - renderManager.viewerPosY),
                (float) (z - renderManager.viewerPosZ),
                modelView,
                projection,
                viewport,
                projected
        );
        if (!success || projected.get(2) < 0.0F || projected.get(2) > 1.0F) {
            return false;
        }
        double screenX = projected.get(0) / resolution.getScaleFactor();
        double screenY = (minecraft.displayHeight - projected.get(1)) / resolution.getScaleFactor();
        bounds.include(screenX, screenY);
        return true;
    }

    private void drawBox(DisplayBox box) {
        int left = (int) Math.floor(box.left);
        int top = (int) Math.floor(box.top);
        int right = (int) Math.ceil(box.right);
        int bottom = (int) Math.ceil(box.bottom);
        Gui.drawRect(left, top, right + 1, top + 1, boxColor);
        Gui.drawRect(left, bottom, right + 1, bottom + 1, boxColor);
        Gui.drawRect(left, top, left + 1, bottom + 1, boxColor);
        Gui.drawRect(right, top, right + 1, bottom + 1, boxColor);
    }

    private void drawLabels(FontRenderer font, DisplayBox box, ScaledResolution resolution) {
        int left = (int) Math.floor(box.left);
        int top = (int) Math.floor(box.top);
        int right = (int) Math.ceil(box.right);
        int bottom = (int) Math.ceil(box.bottom);
        String distance = String.format("%.1fm", box.distance);
        int nameWidth = font.getStringWidth(box.name);
        int distanceWidth = font.getStringWidth(distance);
        int boxWidth = right - left;
        if (boxWidth >= nameWidth + distanceWidth + LABEL_GAP) {
            int textY = top - font.FONT_HEIGHT - LABEL_LINE_GAP;
            drawStringClamped(font, box.name, left, textY, nameColor, resolution);
            drawStringClamped(font, distance, right - distanceWidth, textY, distanceColor, resolution);
        } else {
            int centerX = (left + right) / 2;
            int nameY = top - font.FONT_HEIGHT * 2 - LABEL_LINE_GAP * 2;
            int distanceY = top - font.FONT_HEIGHT - LABEL_LINE_GAP;
            if (nameY < LABEL_SCREEN_MARGIN) {
                nameY = bottom + LABEL_LINE_GAP;
                distanceY = nameY + font.FONT_HEIGHT + LABEL_LINE_GAP;
            }
            drawStringClamped(font, box.name, centerX - nameWidth / 2, nameY, nameColor, resolution);
            drawStringClamped(font, distance, centerX - distanceWidth / 2, distanceY, distanceColor, resolution);
        }
    }

    private void drawHealth(FontRenderer font, DisplayBox box, ScaledResolution resolution) {
        if (healthDisplay == ModuleEntityDisplayBoxUnit.HealthDisplay.OFF || box.maxHealth <= 0.0F) {
            return;
        }
        int left = (int) Math.floor(box.left);
        int top = (int) Math.floor(box.top);
        int bottom = (int) Math.ceil(box.bottom);
        if (healthDisplay.shouldDrawBar()) {
            drawHealthBar(box, resolution, left, top, bottom);
        }
        if (healthDisplay.shouldDrawText() && box.distance <= HEALTH_TEXT_MAX_DISTANCE) {
            drawHealthText(font, box, resolution, left, top);
        }
    }

    private void drawHealthBar(DisplayBox box, ScaledResolution resolution, int left, int top, int bottom) {
        int barRight = left - HEALTH_BAR_GAP;
        int barLeft = barRight - HEALTH_BAR_WIDTH;
        if (barLeft < LABEL_SCREEN_MARGIN) {
            barLeft = Math.min(left + HEALTH_BAR_GAP, resolution.getScaledWidth() - HEALTH_BAR_WIDTH - LABEL_SCREEN_MARGIN);
            barRight = barLeft + HEALTH_BAR_WIDTH;
        }
        int clampedTop = Math.max(LABEL_SCREEN_MARGIN, top);
        int clampedBottom = Math.min(resolution.getScaledHeight() - LABEL_SCREEN_MARGIN, bottom);
        if (clampedBottom <= clampedTop) {
            return;
        }
        Gui.drawRect(barLeft - HEALTH_BAR_BORDER, clampedTop - HEALTH_BAR_BORDER, barRight + HEALTH_BAR_BORDER, clampedBottom + HEALTH_BAR_BORDER, 0xAA000000);
        Gui.drawRect(barLeft, clampedTop, barRight, clampedBottom, 0xAA202020);
        float healthRatio = Math.max(0.0F, Math.min(1.0F, box.health / box.maxHealth));
        int fillHeight = Math.max(1, Math.round((clampedBottom - clampedTop) * healthRatio));
        int fillTop = clampedBottom - fillHeight;
        Gui.drawRect(barLeft, fillTop, barRight, clampedBottom, getHealthColor(healthRatio));
    }

    private void drawHealthText(FontRenderer font, DisplayBox box, ScaledResolution resolution, int left, int top) {
        String health = formatHealth(box.health) + "/" + formatHealth(box.maxHealth);
        int textWidth = font.getStringWidth(health);
        int textX = left - HEALTH_BAR_GAP - HEALTH_BAR_WIDTH - HEALTH_TEXT_GAP - textWidth;
        if (textX < LABEL_SCREEN_MARGIN) {
            textX = left + HEALTH_BAR_GAP + HEALTH_BAR_WIDTH + HEALTH_TEXT_GAP;
        }
        drawStringClamped(font, health, textX, top, getHealthColor(box.health / box.maxHealth), resolution);
    }

    private String formatHealth(float health) {
        if (Math.abs(health - Math.round(health)) < 0.05F) {
            return Integer.toString(Math.round(health));
        }
        return String.format("%.1f", health);
    }

    private int getHealthColor(float healthRatio) {
        float ratio = Math.max(0.0F, Math.min(1.0F, healthRatio));
        if (ratio > 0.5F) {
            return 0xFF3CFE9A;
        }
        return ratio > 0.25F ? 0xFFFFFF55 : 0xFFFF5555;
    }

    private void drawStringClamped(FontRenderer font, String text, int x, int y, int color, ScaledResolution resolution) {
        int maxX = resolution.getScaledWidth() - font.getStringWidth(text) - LABEL_SCREEN_MARGIN;
        int maxY = resolution.getScaledHeight() - font.FONT_HEIGHT - LABEL_SCREEN_MARGIN;
        int clampedX = Math.max(LABEL_SCREEN_MARGIN, Math.min(x, maxX));
        int clampedY = Math.max(LABEL_SCREEN_MARGIN, Math.min(y, maxY));
        font.drawString(text, clampedX, clampedY, color);
    }

    private static class ScreenBounds {
        private double minX = Double.POSITIVE_INFINITY;
        private double minY = Double.POSITIVE_INFINITY;
        private double maxX = Double.NEGATIVE_INFINITY;
        private double maxY = Double.NEGATIVE_INFINITY;

        private void include(double x, double y) {
            minX = Math.min(minX, x);
            minY = Math.min(minY, y);
            maxX = Math.max(maxX, x);
            maxY = Math.max(maxY, y);
        }

        private boolean isValid(ScaledResolution resolution) {
            return minX < maxX && minY < maxY && maxX >= 0 && maxY >= 0 && minX <= resolution.getScaledWidth() && minY <= resolution.getScaledHeight();
        }
    }

    private static class DisplayBox {
        private final double left;
        private final double top;
        private final double right;
        private final double bottom;
        private final String name;
        private final float distance;
        private final float health;
        private final float maxHealth;

        private DisplayBox(double left, double top, double right, double bottom, String name, float distance, float health, float maxHealth) {
            this.left = left;
            this.top = top;
            this.right = right;
            this.bottom = bottom;
            this.name = name;
            this.distance = distance;
            this.health = health;
            this.maxHealth = maxHealth;
        }
    }
}
