package moremekasuitmodules.client.gui.qio;

import mekanism.client.gui.qio.GuiQIOItemViewer;
import mekanism.common.lib.frequency.Frequency.FrequencyIdentity;
import mekanism.common.lib.frequency.IFrequencyItem;
import moremekasuitmodules.client.gui.element.tab.GuiModuleQIOFrequencyTab;
import moremekasuitmodules.common.inventory.container.item.PortableModuleQIODashboardContainer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public class GuiPortableModuleQIODashboard extends GuiQIOItemViewer<PortableModuleQIODashboardContainer> {


    public GuiPortableModuleQIODashboard(PortableModuleQIODashboardContainer container, Inventory inv, Component title) {
        super(container, inv, title);
    }

    @Override
    protected void addGuiElements() {
        super.addGuiElements();
        addRenderableWidget(new GuiModuleQIOFrequencyTab(this, menu.getHand()));
    }


    @Override
    public GuiQIOItemViewer<PortableModuleQIODashboardContainer> recreate(PortableModuleQIODashboardContainer container) {
        return new GuiPortableModuleQIODashboard(container, inv, title);
    }

    @Override
    public FrequencyIdentity getFrequency() {
        return ((IFrequencyItem) menu.getStack().getItem()).getFrequencyIdentity(menu.getStack());
    }
}
