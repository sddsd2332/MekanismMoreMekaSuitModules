package moremekasuitmodules.common.integration;

import net.neoforged.fml.ModList;

import java.util.function.Predicate;

public final class MoreMekaSuitModulesHooks {

    public record IntegrationInfo(String modid, boolean isLoaded) {
        private IntegrationInfo(String modid, Predicate<String> loadedCheck) {
            this(modid, loadedCheck.test(modid));
        }

    }

    public static final String DRACONIC_EVOLUTION_MOD_ID = "draconicevolution";
    public static final String ICE_AND_FIRE_MOD_ID = "iceandfire";
    public static final String BOTANIA_MOD_ID = "botania";
    public static final String IE_MOD_ID = "immersiveengineering";

    public IntegrationInfo DraconicEvolutionLoaded;
    public IntegrationInfo IceAndFireLoaded;
    public IntegrationInfo BotaniaLoaded;
    public IntegrationInfo IELoaded;

    public MoreMekaSuitModulesHooks() {
        ModList modList = ModList.get();
        Predicate<String> loadedCheck = modList == null ? modid -> false : modList::isLoaded;
        DraconicEvolutionLoaded = new IntegrationInfo(DRACONIC_EVOLUTION_MOD_ID, loadedCheck);
        IceAndFireLoaded = new IntegrationInfo(ICE_AND_FIRE_MOD_ID, loadedCheck);
        BotaniaLoaded = new IntegrationInfo(BOTANIA_MOD_ID, loadedCheck);
        IELoaded = new IntegrationInfo(IE_MOD_ID, loadedCheck);
    }


}
