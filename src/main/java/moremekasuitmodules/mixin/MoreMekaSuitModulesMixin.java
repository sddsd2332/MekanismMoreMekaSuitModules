package moremekasuitmodules.mixin;

import mekanism.common.Mekanism;
import net.minecraftforge.fml.common.Loader;
import zone.rong.mixinbooter.ILateMixinLoader;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BooleanSupplier;

@SuppressWarnings({"unused", "SameParameterValue"})
public class MoreMekaSuitModulesMixin implements ILateMixinLoader {

    private static final Map<String, BooleanSupplier> MIXIN_CONFIGS = new LinkedHashMap<>();

    static {
        addMixinCFG("mixins.moremekasuitmodules_mekanism.json");
        addModdedMixinCFG("mixins.moremekasuitmodules_iceandfire.json", "iceandfire");
        addModdedMixinCFG("mixins.moremekasuitmodules_gregtech.json", "gregtech");
        addModdedMixinCFG("mixins.moremekasuitmodules_advancedRocketry.json","advancedrocketry");
        addModdedMixinCFG("mixins.moremekasuitmodules_lolipickaxe.json","lolipickaxe");
        addModdedMixinCFG("mixins.moremekasuitmodules_botania.json","botania");
        addModdedMixinCFG("mixins.moremekasuitmodules_appliedenergistics2.json","appliedenergistics2");
        addModdedMixinCFG("mixins.moremekasuitmodules_matteroverdrive.json","matteroverdrive");
        addModdedMixinCFG("mixins.moremekasuitmodules_thaumcraft.json","thaumcraft");
        addModdedMixinCFG("mixins.moremekasuitmodules_extrabotany.json","extrabotany");
    }

    @Override
    public List<String> getMixinConfigs() {
        return new ArrayList<>(MIXIN_CONFIGS.keySet());
    }

    @Override
    public boolean shouldMixinConfigQueue(final String mixinConfig) {
        BooleanSupplier supplier = MIXIN_CONFIGS.get(mixinConfig);
        if (supplier == null) {
            Mekanism.logger.warn(Mekanism.LOG_TAG + "Mixin config {} is not found in config map! It will never be loaded.", mixinConfig);
            return false;
        }
        return supplier.getAsBoolean();
    }


    private static boolean modLoaded(final String modID) {
        return Loader.isModLoaded(modID);
    }

    private static void addModdedMixinCFG(final String mixinConfig, final String modID) {
        addMixinCFG(mixinConfig, () -> modLoaded(modID));
    }

    private static void addMixinCFG(final String mixinConfig) {
        addMixinCFG(mixinConfig, () -> true);
    }

    private static void addMixinCFG(final String mixinConfig, final BooleanSupplier conditions) {
        MIXIN_CONFIGS.put(mixinConfig, conditions);
    }
}
