package moremekasuitmodules.common.integration.botania.gear;

import mekanism.api.annotations.ParametersAreNotNullByDefault;
import mekanism.api.gear.ICustomModule;
import mekanism.api.gear.IModule;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import vazkii.botania.api.mana.ManaItemHandler;

@ParametersAreNotNullByDefault
public class ModuleAdvancedBandofAuraUnit implements ICustomModule<ModuleAdvancedBandofAuraUnit> {

    @Override
    public void tickServer(IModule<ModuleAdvancedBandofAuraUnit> module, Player player) {
        addMana(module.getContainer(), player);
    }

    private void addMana(ItemStack stack, Player player) {
        ManaItemHandler.instance().dispatchManaExact(stack, player, 15, true);
    }
}
