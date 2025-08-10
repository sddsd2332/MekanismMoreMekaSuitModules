package moremekasuitmodules.common.integration.iceandfire;

import mekanism.api.MekanismIMC;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;

public class iceandfireImcQueue {

    public iceandfireImcQueue(){

    }

    public static void imcQueue(InterModEnqueueEvent event){
        MekanismIMC.addMekaSuitHelmetModules(iceAndFireModules.SMART_SHIELDING_UNIT);
    }
}
