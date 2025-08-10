package moremekasuitmodules.common.integration;

import mekanism.common.registries.MekanismCreativeTabs;
import moremekasuitmodules.common.integration.botania.botaniaImcQueue;
import moremekasuitmodules.common.integration.botania.botaniaModules;
import moremekasuitmodules.common.integration.botania.botaniaModulesItem;
import moremekasuitmodules.common.integration.iceandfire.iceAndFireModules;
import moremekasuitmodules.common.integration.iceandfire.iceAndFireModulesItem;
import moremekasuitmodules.common.integration.iceandfire.iceandfireImcQueue;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModList;

public final class MoreMekaSuitModulesHooks {

    public static final String DRACONIC_EVOLUTION_MOD_ID = "draconicevolution";
    public static final String ICE_AND_FIRE_MOD_ID = "iceandfire";
    public static final String BOTANIA_MOD_ID = "botania";
    public static final String IE_MOD_ID = "immersiveengineering";

    public boolean DraconicEvolutionLoaded;
    public boolean IceAndFireLoaded;
    public boolean BotaniaLoaded;
    public boolean IELoaded;

    public MoreMekaSuitModulesHooks() {
    }

    public void hookCommonSetup() {
        ModList modList = ModList.get();
        DraconicEvolutionLoaded = modList.isLoaded(DRACONIC_EVOLUTION_MOD_ID);
        IceAndFireLoaded = modList.isLoaded(ICE_AND_FIRE_MOD_ID);
        BotaniaLoaded = modList.isLoaded(BOTANIA_MOD_ID);
        IELoaded = modList.isLoaded(IE_MOD_ID);
    }



}
