package moremekasuitmodules.common.content.gear;

import mekanism.api.annotations.ParametersAreNotNullByDefault;
import mekanism.api.gear.ICustomModule;
import mekanism.api.gear.IModule;
import mekanism.api.gear.IModuleContainer;
import mekanism.common.Mekanism;
import mekanism.common.integration.curios.CuriosIntegration;
import moremekasuitmodules.common.MoreMekaSuitModules;
import moremekasuitmodules.common.util.MoreMekaSuitModulesUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.IItemHandler;

import java.util.ArrayList;
import java.util.List;

@ParametersAreNotNullByDefault
public record ModuleInfiniteChemicalAndFluidSupplyUnit(boolean isArmor, boolean isInventory, boolean isOffhand,
                                                       boolean isCurios) implements ICustomModule<ModuleInfiniteChemicalAndFluidSupplyUnit> {

    public static final ResourceLocation SUPPLY_ARMOR = MoreMekaSuitModules.rl("supply_armor");
    public static final ResourceLocation SUPPLY_INVENTORY = MoreMekaSuitModules.rl("supply_inventory");
    public static final ResourceLocation SUPPLY_OFFHAND = MoreMekaSuitModules.rl("supply_offhand");
    public static final ResourceLocation SUPPLY_CURIOS = MoreMekaSuitModules.rl("supply_curios");

    public ModuleInfiniteChemicalAndFluidSupplyUnit(IModule<ModuleInfiniteChemicalAndFluidSupplyUnit> module) {
        this(module.getBooleanConfigOrFalse(SUPPLY_ARMOR), module.getBooleanConfigOrFalse(SUPPLY_INVENTORY), module.getBooleanConfigOrFalse(SUPPLY_OFFHAND), Mekanism.hooks.curios.isLoaded() && module.getBooleanConfigOrFalse(SUPPLY_CURIOS));
    }

    @Override
    public void tickClient(IModule<ModuleInfiniteChemicalAndFluidSupplyUnit> module, IModuleContainer moduleContainer, ItemStack stack, Player player) {
        this.tickServer(module, moduleContainer, stack, player);
    }

    @Override
    public void tickServer(IModule<ModuleInfiniteChemicalAndFluidSupplyUnit> module, IModuleContainer moduleContainer, ItemStack item, Player player) {
        if (module.isEnabled()) {
            List<ItemStack> inventory = new ArrayList<>();
            if (isArmor) {
                inventory.addAll(player.getInventory().armor);
            }
            if (isInventory) {
                inventory.addAll(player.getInventory().items);
            }
            if (isOffhand) {
                inventory.addAll(player.getInventory().offhand);
            }
            if (Mekanism.hooks.curios.isLoaded()) {
                if (isCurios) {
                    getCurios(inventory, player);
                }
            }

            if (!inventory.isEmpty()) {
                inventory.forEach(stack -> {
                    MoreMekaSuitModulesUtils.addFluidStack(stack, true);
                    MoreMekaSuitModulesUtils.addChemicals(stack, true);
                });
            }
        }
    }


    public void getCurios(List<ItemStack> stacks, Player player) {
        IItemHandler invOptional = CuriosIntegration.getCuriosInventory(player);
        if (invOptional != null) {
            for (int i = 0, slots = invOptional.getSlots(); i < slots; i++) {
                stacks.add(invOptional.getStackInSlot(i));
            }
        }
    }

}
