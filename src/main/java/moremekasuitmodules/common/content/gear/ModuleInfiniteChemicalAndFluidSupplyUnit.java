package moremekasuitmodules.common.content.gear;

import mekanism.api.annotations.ParametersAreNotNullByDefault;
import mekanism.api.gear.ICustomModule;
import mekanism.api.gear.IModule;
import mekanism.api.gear.config.IModuleConfigItem;
import mekanism.api.gear.config.ModuleBooleanData;
import mekanism.api.gear.config.ModuleConfigItemCreator;
import mekanism.common.Mekanism;
import mekanism.common.integration.curios.CuriosIntegration;
import moremekasuitmodules.common.MoreMekaSuitModulesLang;
import moremekasuitmodules.common.util.MoreMekaSuitModulesUtils;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@ParametersAreNotNullByDefault
public class ModuleInfiniteChemicalAndFluidSupplyUnit implements ICustomModule<ModuleInfiniteChemicalAndFluidSupplyUnit> {

    private IModuleConfigItem<Boolean> isArmor;
    private IModuleConfigItem<Boolean> isInventory;
    private IModuleConfigItem<Boolean> isOffhand;
    private IModuleConfigItem<Boolean> isCurios;


    @Override
    public void init(IModule<ModuleInfiniteChemicalAndFluidSupplyUnit> module, ModuleConfigItemCreator configItemCreator) {
        isArmor = configItemCreator.createConfigItem("armor_supply", MoreMekaSuitModulesLang.MODULE_SUPPLY_ARMOR, new ModuleBooleanData());
        isInventory = configItemCreator.createConfigItem("inventory_supply", MoreMekaSuitModulesLang.MODULE_SUPPLY_INVENTORY, new ModuleBooleanData(false));
        isOffhand = configItemCreator.createConfigItem("offhand_supply", MoreMekaSuitModulesLang.MODULE_SUPPLY_OFFHAND, new ModuleBooleanData(false));
        if (Mekanism.hooks.CuriosLoaded) {
            isCurios = configItemCreator.createConfigItem("curios_supply", MoreMekaSuitModulesLang.MODULE_SUPPLY_CURIOS, new ModuleBooleanData(false));
        }

    }

    @Override
    public void tickClient(IModule<ModuleInfiniteChemicalAndFluidSupplyUnit> module, Player player) {
        this.tickServer(module, player);
    }

    @Override
    public void tickServer(IModule<ModuleInfiniteChemicalAndFluidSupplyUnit> module, Player player) {
        if (module.isEnabled()) {
            List<ItemStack> inventory = new ArrayList<>();
            if (isArmor.get()) {
                inventory.addAll(player.getInventory().armor);
            }
            if (isInventory.get()) {
                inventory.addAll(player.getInventory().items);
            }
            if (isOffhand.get()) {
                inventory.addAll(player.getInventory().offhand);
            }
            if (Mekanism.hooks.CuriosLoaded) {
                if (isCurios.get()) {
                    getCurios(inventory, player);
                }
            }
            if (!inventory.isEmpty()) {
                inventory.forEach(stack -> {
                    MoreMekaSuitModulesUtils.addFluidStack(stack, true);
                    MoreMekaSuitModulesUtils.addGasStack(stack, true);
                    MoreMekaSuitModulesUtils.addInfuseStack(stack, true);
                    MoreMekaSuitModulesUtils.addPigmentStack(stack, true);
                    MoreMekaSuitModulesUtils.addSlurryStack(stack, true);
                });
            }
        }
    }

    public void getCurios(List<ItemStack> stacks, Player player) {
        Optional<? extends IItemHandler> invOptional = CuriosIntegration.getCuriosInventory(player);
        if (invOptional.isPresent()) {
            IItemHandler inv = invOptional.get();
            for (int i = 0, slots = inv.getSlots(); i < slots; i++) {
                stacks.add(inv.getStackInSlot(i));
            }
        }
    }


}
