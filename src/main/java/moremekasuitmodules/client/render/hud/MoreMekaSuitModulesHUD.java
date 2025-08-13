package moremekasuitmodules.client.render.hud;

import com.mojang.blaze3d.vertex.PoseStack;
import mekanism.client.gui.GuiUtils;
import mekanism.common.config.MekanismConfig;
import mekanism.common.tags.MekanismTags;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.LayeredDraw;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;

import java.util.ArrayList;
import java.util.List;

public class MoreMekaSuitModulesHUD implements LayeredDraw.Layer {

    public static final MoreMekaSuitModulesHUD INSTANCE = new MoreMekaSuitModulesHUD();

    private final HUDRenderer hudRenderer = new HUDRenderer();

    private MoreMekaSuitModulesHUD() {
    }

    @Override
    public void render(@NotNull GuiGraphics graphics, @NotNull DeltaTracker delta) {
        Minecraft minecraft = Minecraft.getInstance();
        Player player = minecraft.player;
        if (player != null && !player.isSpectator() && !minecraft.options.hideGui && MekanismConfig.client.enableHUD.get()) {
            Font font = minecraft.font;
            List<DelayedString> delayedDraws = null;

            boolean reverseHud = MekanismConfig.client.reverseHUD.get();
            int maxTextHeight = graphics.guiHeight();
            if (player.getItemBySlot(EquipmentSlot.HEAD).is(MekanismTags.Items.MEKASUIT_HUD_RENDERER)) {
                if (delayedDraws == null) {
                    delayedDraws = new ArrayList<>();
                }
                hudRenderer.renderHUD(minecraft, graphics, font, delayedDraws, delta, graphics.guiWidth(), graphics.guiHeight(), maxTextHeight, reverseHud);
            }

            if (delayedDraws != null && !delayedDraws.isEmpty()) {
                for (DelayedString delayedDraw : delayedDraws) {
                    delayedDraw.draw(graphics, font);
                }
                //Flush once at the end of the draws
                graphics.flush();
            }
        }
    }


    public record DelayedString(Matrix4f matrix, Component component, float x, float y, int color, boolean dropShadow) {

        public DelayedString(PoseStack pose, Component component, float x, float y, int color, boolean dropShadow) {
            this(new Matrix4f(pose.last().pose()), component, x, y, color, dropShadow);
        }

        public void draw(GuiGraphics graphics, Font font) {
            GuiUtils.drawStringNoFlush(graphics, matrix, font, component, x, y, color, dropShadow);
        }
    }
}
