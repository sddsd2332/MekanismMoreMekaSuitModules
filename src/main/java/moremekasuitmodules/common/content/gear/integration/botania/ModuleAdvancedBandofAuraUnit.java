package moremekasuitmodules.common.content.gear.integration.botania;

import mekanism.api.annotations.ParametersAreNotNullByDefault;
import mekanism.api.gear.ICustomModule;
import mekanism.api.gear.IModule;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Optional;
import vazkii.botania.api.mana.ManaItemHandler;

@ParametersAreNotNullByDefault
public class ModuleAdvancedBandofAuraUnit implements ICustomModule<ModuleAdvancedBandofAuraUnit> {

    @Override
    public void tickServer(IModule<ModuleAdvancedBandofAuraUnit> module, EntityPlayer player) {
        if (Loader.isModLoaded("botania")) {
            addMana(module.getContainer(), player);
        }
    }

    @Optional.Method(modid = "botania")
    private void addMana(ItemStack stack, EntityPlayer player) {
        ManaItemHandler.dispatchManaExact(stack, player, 5, true);
    }
}
