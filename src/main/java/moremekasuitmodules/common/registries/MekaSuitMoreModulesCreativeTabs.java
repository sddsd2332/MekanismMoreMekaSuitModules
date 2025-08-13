package moremekasuitmodules.common.registries;

import mekanism.api.MekanismAPI;
import mekanism.api.gear.ModuleData;
import mekanism.common.content.gear.IModuleContainerItem;
import mekanism.common.content.gear.IModuleItem;
import mekanism.common.content.gear.ModuleContainer;
import mekanism.common.content.gear.ModuleHelper;
import mekanism.common.registration.MekanismDeferredHolder;
import mekanism.common.registration.impl.CreativeTabDeferredRegister;
import mekanism.common.registries.MekanismCreativeTabs;
import mekanism.common.util.StorageUtils;
import moremekasuitmodules.common.MoreMekaSuitModules;
import moremekasuitmodules.common.MoreMekaSuitModulesLang;
import moremekasuitmodules.common.config.MoreModulesConfig;
import moremekasuitmodules.common.util.MoreMekaSuitModulesUtils;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTab.ItemDisplayParameters;
import net.minecraft.world.item.CreativeModeTab.Output;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class MekaSuitMoreModulesCreativeTabs {

    public static final CreativeTabDeferredRegister CREATIVE_TABS = new CreativeTabDeferredRegister(MoreMekaSuitModules.MODID);


    public static final MekanismDeferredHolder<CreativeModeTab, CreativeModeTab> MoreModules = CREATIVE_TABS.registerMain(MoreMekaSuitModulesLang.MEKANISM_MORE_MODULES,
            MekaSuitMoreModulesItem.MODULE_INFINITE_INTERCEPTION_AND_RESCUE_SYSTEM, builder ->
                    builder.withTabsBefore(MekanismCreativeTabs.MEKANISM.getId())
                            .displayItems((displayParameters, output) -> {
                                addAllModule(displayParameters, output);
                                CreativeTabDeferredRegister.addToDisplay(MekaSuitMoreModulesItem.ITEMS, output);
                                if (MoreMekaSuitModules.hooks.DraconicEvolutionLoaded.isLoaded()) {
                                }
                                if (MoreMekaSuitModules.hooks.IceAndFireLoaded.isLoaded()) {
                                    //    CreativeTabDeferredRegister.addToDisplay(iceAndFireModulesItem.ITEMS, output);
                                }
                                if (MoreMekaSuitModules.hooks.BotaniaLoaded.isLoaded()) {
                                    //    CreativeTabDeferredRegister.addToDisplay(botaniaModulesItem.ITEMS, output);
                                }
                            })
    );

    //将所有的模块添加到到meka上,并显示到本mod的创造栏上,便于快速测试
    private static void addAllModule(ItemDisplayParameters parameters, Output output) {
        if (MoreModulesConfig.config.isLoaded()) {
            if (MoreModulesConfig.config.addALLModueltoMekaSuit.get()) {
                List<Item> items = new ArrayList<>();
                List<Item> moduleItems = new ArrayList<>();
                BuiltInRegistries.ITEM.forEach(item -> {
                    if (item instanceof IModuleContainerItem) {
                        items.add(item);
                    }
                    if (item instanceof IModuleItem) {
                        moduleItems.add(item);
                    }
                });
                for (Item asitem : items) {
                    ItemStack stack = new ItemStack(asitem);
                    for (ModuleData<?> module : MekanismAPI.MODULE_REGISTRY) {
                        if (ModuleHelper.get().getSupported(stack.getItem()).contains(module)) {
                            ModuleContainer container = ModuleHelper.get().getModuleContainer(stack);
                            if (container != null) {
                                for (Item moduleItem : moduleItems) {
                                    if (moduleItem instanceof IModuleItem item && item.getModuleData().value().equals(module)) {
                                        Holder<ModuleData<?>> moduleDataHolder = item.getModuleData();
                                        container.addModule(parameters.holders(), stack, moduleDataHolder, module.getMaxStackSize());
                                    }
                                }
                            }
                        }
                    }
                    StorageUtils.getFilledEnergyVariant(stack);
                    MoreMekaSuitModulesUtils.addSupportedFluidsOrChemicals(stack);
                    output.accept(stack);
                }
            }
        }
    }
}
