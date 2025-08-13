package moremekasuitmodules.client.render.hud;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import mekanism.api.gear.IHUDElement;
import mekanism.api.gear.IModuleHelper;
import mekanism.api.text.TextComponentUtil;
import mekanism.client.render.MekanismRenderer;
import moremekasuitmodules.client.render.hud.MoreMekaSuitModulesHUD.DelayedString;
import mekanism.common.config.MekanismConfig;
import mekanism.common.item.gear.ItemMekaSuitArmor;
import mekanism.common.util.text.TextUtils;
import moremekasuitmodules.common.ShieldProviderHandler;
import moremekasuitmodules.common.util.MoreMekaSuitModulesUtils;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Unique;

import java.util.List;

public class HUDRenderer {

    @Unique
    private static final ResourceLocation shield_icon = MoreMekaSuitModulesUtils.getResource(MoreMekaSuitModulesUtils.ResourceType.GUI_HUD, "hud_mekasuit_shield.png");

    private long lastTick = -1;
    private float prevRotationYaw;
    private float prevRotationPitch;

    public void renderHUD(Minecraft minecraft, GuiGraphics guiGraphics, Font font, List<DelayedString> delayedDraws, DeltaTracker delta, int screenWidth, int screenHeight,
                          int maxTextHeight, boolean reverseHud) {
        Player player = minecraft.player;
        update(minecraft.level, player);
        if (MekanismConfig.client.hudOpacity.get() < 0.05F) {
            return;
        }
        PoseStack pose = guiGraphics.pose();
        pose.pushPose();
        float yawJitter = -absSqrt(player.yHeadRot - prevRotationYaw);
        float pitchJitter = -absSqrt(player.getXRot() - prevRotationPitch);
        pose.translate(yawJitter, pitchJitter, 0);
        renderMekaSuitEnergyShieldIcons(player, font, guiGraphics, delayedDraws);
        pose.popPose();
    }


    private void renderMekaSuitEnergyShieldIcons(Player player, Font font, GuiGraphics guiGraphics, List<DelayedString> delayedDraws) {
        PoseStack pose = guiGraphics.pose();
        pose.pushPose();
        pose.translate(10, 10, 0);
        Matrix4f matrix = new Matrix4f(pose.last().pose());
        ItemStack stack = player.getItemBySlot(EquipmentSlot.HEAD);
        if (stack.getItem() instanceof ItemMekaSuitArmor armor && armor.getType().equals(ArmorItem.Type.HELMET)) {
            ShieldProviderHandler.ArmorSummery summery = new ShieldProviderHandler.ArmorSummery().getSummery(player);
            if (summery != null) {
                double ratio = summery.protectionPoints / summery.maxProtectionPoints;
                Component text = TextComponentUtil.build(TextComponentUtil.getString((float) summery.protectionPoints + "/" + (float) summery.maxProtectionPoints)).append(" (").append(TextUtils.getPercent(ratio)).append(")");
                renderHUDElement(font, guiGraphics, matrix, delayedDraws, 0, 18, IModuleHelper.INSTANCE.hudElement(shield_icon, text, ratio > 0.2 ? IHUDElement.HUDColor.REGULAR : (ratio > 0.1 ? IHUDElement.HUDColor.WARNING : IHUDElement.HUDColor.DANGER)), false);
            }
        }
        pose.popPose();
    }

    private void renderHUDElement(Font font, GuiGraphics guiGraphics, Matrix4f matrix, List<DelayedString> delayedDraws, int x, int y, IHUDElement element,
                                  boolean iconRight) {
        int color = element.getColor();
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        MekanismRenderer.color(guiGraphics, color);
        guiGraphics.blit(element.getIcon(), iconRight ? x + font.width(element.getText()) + 2 : x, y, 0, 0, 16, 16, 16, 16);
        MekanismRenderer.resetColor(guiGraphics);
        delayedDraws.add(new DelayedString(matrix, element.getText(), iconRight ? x : x + 18, y + 5, color, false));
    }

    private void update(Level level, Player player) {
        // if we're just now rendering the HUD after a pause, reset the pitch/yaw trackers
        if (lastTick == -1 || level.getGameTime() - lastTick > 1) {
            prevRotationYaw = player.getYRot();
            prevRotationPitch = player.getXRot();
        }
        lastTick = level.getGameTime();
        float yawDiff = player.yHeadRot - prevRotationYaw;
        float pitchDiff = player.getXRot() - prevRotationPitch;
        float jitter = MekanismConfig.client.hudJitter.get();
        prevRotationYaw += yawDiff / jitter;
        prevRotationPitch += pitchDiff / jitter;
    }

    private static float absSqrt(float val) {
        float ret = (float) Math.sqrt(Math.abs(val));
        return val < 0 ? -ret : ret;
    }
}
