package moremekasuitmodules.client.gui.element.tab;

import mekanism.client.SpecialColors;
import mekanism.client.gui.IGuiWrapper;
import mekanism.client.gui.element.GuiInsetElement;
import mekanism.client.render.MekanismRenderer;
import mekanism.common.MekanismLang;
import mekanism.common.tile.base.TileEntityMekanism;
import mekanism.common.util.MekanismUtils;
import moremekasuitmodules.common.MoreMekaSuitModules;
import moremekasuitmodules.common.network.to_server.PacketModuleGuiButtonPress;
import moremekasuitmodules.common.network.to_server.PacketModuleGuiButtonPress.ClickedItemButton;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import org.jetbrains.annotations.NotNull;

public class GuiModuleQIOFrequencyTab extends GuiInsetElement<TileEntityMekanism> {


    private static final ResourceLocation FREQUENCY = MekanismUtils.getResource(MekanismUtils.ResourceType.GUI, "frequency.png");
    private final InteractionHand currentHand;

    public GuiModuleQIOFrequencyTab(IGuiWrapper gui, InteractionHand hand) {
        super(FREQUENCY, gui, null, -26, 6, 26, 18, true);
        currentHand = hand;
    }

    @Override
    protected void colorTab(GuiGraphics guiGraphics) {
        MekanismRenderer.color(guiGraphics, SpecialColors.TAB_QIO_FREQUENCY);
    }

    @Override
    public void renderToolTip(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY) {
        super.renderToolTip(guiGraphics, mouseX, mouseY);
        displayTooltips(guiGraphics, mouseX, mouseY, MekanismLang.SET_FREQUENCY.translate());
    }

    @Override
    public void onClick(double mouseX, double mouseY, int button) {
        MoreMekaSuitModules.packetHandler().sendToServer(new PacketModuleGuiButtonPress(ClickedItemButton.QIO_FREQUENCY_SELECT, currentHand));
    }
}
