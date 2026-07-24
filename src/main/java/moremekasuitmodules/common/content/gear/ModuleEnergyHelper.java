package moremekasuitmodules.common.content.gear;

import mekanism.api.gear.IModule;
import net.minecraft.entity.player.EntityPlayer;

public final class ModuleEnergyHelper {

    private ModuleEnergyHelper() {
    }

    public static boolean tryUseEnergy(IModule<?> module, EntityPlayer player, double amount) {
        if (amount <= 0 || player.isCreative()) {
            return true;
        }
        return Double.isFinite(amount) && module.canUseEnergy(player, amount, false)
                && module.useEnergy(player, amount) >= amount;
    }
}
