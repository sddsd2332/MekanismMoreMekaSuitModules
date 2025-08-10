package moremekasuitmodules.client.render.hud;

import mekanism.common.config.MekanismConfig;
import mekanism.common.tags.MekanismTags;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.client.gui.overlay.ForgeGui;
import net.minecraftforge.client.gui.overlay.IGuiOverlay;

public class MoreMekaSuitModulesHUD implements IGuiOverlay {

    public static final MoreMekaSuitModulesHUD INSTANCE = new MoreMekaSuitModulesHUD();

    private final HUDRenderer hudRenderer = new HUDRenderer();

    private MoreMekaSuitModulesHUD() {
    }


    @Override
    public void render(ForgeGui gui, GuiGraphics guiGraphics, float partialTicks, int screenWidth, int screenHeight) {
        Minecraft minecraft = gui.getMinecraft();
        Player player = minecraft.player;
        Font font = gui.getFont();
        boolean reverseHud = MekanismConfig.client.reverseHUD.get();
        if (!minecraft.options.hideGui && player != null && !player.isSpectator() && MekanismConfig.client.enableHUD.get()) {
            if (player.getItemBySlot(EquipmentSlot.HEAD).is(MekanismTags.Items.MEKASUIT_HUD_RENDERER)) {
                hudRenderer.renderHUD(minecraft, guiGraphics, font, partialTicks, screenWidth, screenHeight, screenHeight, reverseHud);
            }
        }
    }
}
