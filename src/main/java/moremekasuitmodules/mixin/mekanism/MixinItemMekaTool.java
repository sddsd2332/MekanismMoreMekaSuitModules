package moremekasuitmodules.mixin.mekanism;

import mekanism.api.math.FloatingLongSupplier;
import mekanism.common.item.ItemEnergized;
import mekanism.common.item.gear.ItemMekaTool;
import mekanism.common.util.StorageUtils;
import moremekasuitmodules.common.config.MoreModulesConfig;
import moremekasuitmodules.common.util.MoreMekaSuitModulesUtils;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(value = ItemMekaTool.class, remap = false)
public abstract class MixinItemMekaTool extends ItemEnergized {

    public MixinItemMekaTool(FloatingLongSupplier chargeRateSupplier, FloatingLongSupplier maxEnergySupplier, Properties properties) {
        super(chargeRateSupplier, maxEnergySupplier, properties);
    }

    @Override
    public void addItems(CreativeModeTab.Output tabOutput) {
        super.addItems(tabOutput);
        if (MoreModulesConfig.config.addALLModueltoMekaSuit.get()) {
            ItemStack fullStack = new ItemStack((ItemMekaTool) (Object) this);
            MoreMekaSuitModulesUtils.setAllModule(fullStack);
            StorageUtils.getFilledEnergyVariant(fullStack, getMaxEnergy(fullStack));
            MoreMekaSuitModulesUtils.AddSupportedFluidsOrChemicals(fullStack);
            tabOutput.accept(fullStack);
        }
    }
}
