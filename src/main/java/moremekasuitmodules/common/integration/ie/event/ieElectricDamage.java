package moremekasuitmodules.common.integration.ie.event;

import blusunrize.immersiveengineering.common.util.IEDamageSources;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;

public class ieElectricDamage {

    public static void onIEElectricDamage(LivingIncomingDamageEvent event) {
        if (event.getSource() instanceof IEDamageSources.ElectricDamageSource damageSource) {
            damageSource.dmg = 0;
            event.setCanceled(true);
        }
    }
}

