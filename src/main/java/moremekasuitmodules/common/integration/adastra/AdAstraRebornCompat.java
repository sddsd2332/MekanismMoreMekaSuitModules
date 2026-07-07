package moremekasuitmodules.common.integration.adastra;

import earth.terrarium.adastra.api.events.AdAstraEvents;
import moremekasuitmodules.common.CommonPlayerTickHandler;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.world.WorldServer;

public final class AdAstraRebornCompat {

    private static boolean registered;

    private AdAstraRebornCompat() {
    }

    public static void register() {
        if (registered) {
            return;
        }
        AdAstraEvents.EntityOxygenEvent.register(AdAstraRebornCompat::hasEntityOxygen);
        AdAstraEvents.TemperatureTickEvent.register(AdAstraRebornCompat::allowTemperature);
        AdAstraEvents.HotTemperatureTickEvent.register(AdAstraRebornCompat::allowHotTemperature);
        AdAstraEvents.ColdTemperatureTickEvent.register(AdAstraRebornCompat::allowColdTemperature);
        AdAstraEvents.AcidRainTickEvent.register(AdAstraRebornCompat::allowAcidRain);
        registered = true;
    }

    private static boolean hasEntityOxygen(Entity entity, boolean hasOxygen) {
        if (hasOxygen || !(entity instanceof EntityLivingBase)) {
            return hasOxygen;
        }
        return CommonPlayerTickHandler.hasFullSealedMekaSuit((EntityLivingBase) entity);
    }

    private static boolean allowTemperature(WorldServer world, EntityLivingBase entity) {
        return !CommonPlayerTickHandler.hasFullSealUnit(entity);
    }

    private static boolean allowHotTemperature(WorldServer world, EntityLivingBase entity) {
        return !CommonPlayerTickHandler.hasFullSealUnit(entity);
    }

    private static boolean allowColdTemperature(WorldServer world, EntityLivingBase entity) {
        return !CommonPlayerTickHandler.hasFullSealUnit(entity);
    }

    private static boolean allowAcidRain(WorldServer world, EntityLivingBase entity) {
        return !CommonPlayerTickHandler.hasFullSealUnit(entity);
    }
}
