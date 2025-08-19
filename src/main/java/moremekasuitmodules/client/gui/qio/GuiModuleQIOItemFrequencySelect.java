package moremekasuitmodules.client.gui.qio;

import mekanism.client.gui.GuiMekanism;
import mekanism.client.gui.element.button.MekanismImageButton;
import mekanism.common.MekanismLang;
import mekanism.common.content.qio.QIOFrequency;
import mekanism.common.lib.frequency.FrequencyType;
import moremekasuitmodules.client.gui.element.custom.GuiModuleFrequencySelector;
import moremekasuitmodules.client.gui.element.custom.GuiModuleFrequencySelector.IGuiColorFrequencySelector;
import moremekasuitmodules.client.gui.element.custom.GuiModuleFrequencySelector.IItemGuiFrequencySelector;
import moremekasuitmodules.common.MoreMekaSuitModules;
import moremekasuitmodules.common.inventory.container.item.ModuleQIOFrequencySelectItemContainer;
import moremekasuitmodules.common.network.to_server.PacketModuleGuiButtonPress;
import moremekasuitmodules.common.network.to_server.PacketModuleGuiButtonPress.ClickedItemButton;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import org.jetbrains.annotations.NotNull;

public class GuiModuleQIOItemFrequencySelect extends GuiMekanism<ModuleQIOFrequencySelectItemContainer> implements IGuiColorFrequencySelector<QIOFrequency>,
        IItemGuiFrequencySelector<QIOFrequency, ModuleQIOFrequencySelectItemContainer> {


    public GuiModuleQIOItemFrequencySelect(ModuleQIOFrequencySelectItemContainer moduleQIOFrequencySelectItemContainer, Inventory inv, Component title) {
        super(moduleQIOFrequencySelectItemContainer, inv, title);
        imageHeight -= 11;
        titleLabelY = 5;
    }

    @Override
    protected void addGuiElements() {
        super.addGuiElements();
        addRenderableWidget(new GuiModuleFrequencySelector<>(this, 17));
        addRenderableWidget(new MekanismImageButton(this, 6, 6, 14, getButtonLocation("back"),
                () -> MoreMekaSuitModules.packetHandler().sendToServer(new PacketModuleGuiButtonPress(ClickedItemButton.BACK_BUTTON, menu.getHand())), getOnHover(MekanismLang.BACK)));
    }

    @Override
    protected void drawForegroundText(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY) {
        renderTitleText(guiGraphics);
        super.drawForegroundText(guiGraphics, mouseX, mouseY);
    }

    @Override
    public FrequencyType<QIOFrequency> getFrequencyType() {
        return FrequencyType.QIO;
    }

    @Override
    public ModuleQIOFrequencySelectItemContainer getFrequencyContainer() {
        return menu;
    }

    @Override
    public void drawTitleText(GuiGraphics guiGraphics, Component text, float y) {
        //Adjust spacing for back button
        int leftShift = 15;
        int xSize = getXSize() - leftShift;
        int maxLength = xSize - 12;
        float textWidth = getStringWidth(text);
        float scale = Math.min(1, maxLength / textWidth);
        drawScaledCenteredText(guiGraphics, text, leftShift + xSize / 2F, y, titleTextColor(), scale);
    }

}
