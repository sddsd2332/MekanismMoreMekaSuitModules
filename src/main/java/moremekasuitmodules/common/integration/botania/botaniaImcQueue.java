package moremekasuitmodules.common.integration.botania;

import mekanism.api.MekanismIMC;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;

public class botaniaImcQueue {

    public botaniaImcQueue(){

    }

    public static void imcQueue(InterModEnqueueEvent event) {
        MekanismIMC.addMekaSuitBodyarmorModules(botaniaModules.BAND_OF_AURA_UNIT, botaniaModules.BASIC_BAND_OF_AURA_UNIT, botaniaModules.ADVANCED_BAND_OF_AURA_UNIT, botaniaModules.ELITE_BAND_OF_AURA_UNIT, botaniaModules.ULTIMATE_BAND_OF_AURA_UNIT, botaniaModules.CREATIVE_BAND_OF_AURA_UNIT);
    }
}
