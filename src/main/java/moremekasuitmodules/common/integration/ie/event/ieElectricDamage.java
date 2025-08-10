package moremekasuitmodules.common.integration.ie.event;

import blusunrize.immersiveengineering.common.util.IEDamageSources;
import net.minecraftforge.event.entity.living.LivingAttackEvent;

public class ieElectricDamage {

    public static void onIEElectricDamage(LivingAttackEvent event) {
        if (event.getSource() instanceof IEDamageSources.ElectricDamageSource damageSource) {
            damageSource.dmg = 0;
            event.setCanceled(true);
        }
    }
}

