package moremekasuitmodules.common.registries;

import mekanism.common.inventory.container.type.MekanismItemContainerType;
import mekanism.common.item.gear.ItemMekaSuitArmor;
import mekanism.common.registration.impl.ContainerTypeDeferredRegister;
import mekanism.common.registration.impl.ContainerTypeRegistryObject;
import mekanism.common.registries.MekanismItems;
import moremekasuitmodules.common.MoreMekaSuitModules;
import moremekasuitmodules.common.inventory.container.item.ModuleQIOFrequencySelectItemContainer;
import moremekasuitmodules.common.inventory.container.item.PortableModuleQIODashboardContainer;

public class MekaSuitMoreModulesContainerTypes {

    private MekaSuitMoreModulesContainerTypes() {
    }

    public static final ContainerTypeDeferredRegister CONTAINER_TYPES = new ContainerTypeDeferredRegister(MoreMekaSuitModules.MODID);

    public static final ContainerTypeRegistryObject<ModuleQIOFrequencySelectItemContainer> MODULE_QIO_FREQUENCY_SELECT = CONTAINER_TYPES.register("module_qio_frequency_select", ItemMekaSuitArmor.class, ModuleQIOFrequencySelectItemContainer::new);
    public static final ContainerTypeRegistryObject<PortableModuleQIODashboardContainer> MODULE_QIO = CONTAINER_TYPES.register(MekanismItems.MEKASUIT_HELMET, () -> MekanismItemContainerType.item(ItemMekaSuitArmor.class, PortableModuleQIODashboardContainer::new));
}
