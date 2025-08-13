package moremekasuitmodules.common.content.gear;

import mekanism.api.annotations.ParametersAreNotNullByDefault;
import mekanism.api.gear.ICustomModule;
import mekanism.api.gear.IModule;
import moremekasuitmodules.common.MoreMekaSuitModules;
import net.minecraft.resources.ResourceLocation;

@ParametersAreNotNullByDefault
public record ModuleInfiniteInterceptionAndRescueSystemUnit(boolean damagesource, boolean damagesourceIndirect, boolean chunkRemove) implements ICustomModule<ModuleInfiniteInterceptionAndRescueSystemUnit> {

    public static final ResourceLocation DAMAGE_SOURCE = MoreMekaSuitModules.rl("damage_1");
    public static final ResourceLocation DAMAGE_SOURCE_INDIRECT = MoreMekaSuitModules.rl("damage_2");
    public static final ResourceLocation CHUNK_REMOVE = MoreMekaSuitModules.rl("chunk_remove");

    public ModuleInfiniteInterceptionAndRescueSystemUnit(IModule<ModuleInfiniteInterceptionAndRescueSystemUnit> module) {
        this(module.getBooleanConfigOrFalse(DAMAGE_SOURCE), module.getBooleanConfigOrFalse(DAMAGE_SOURCE_INDIRECT), module.getBooleanConfigOrFalse(CHUNK_REMOVE));
    }

    public boolean getSource() {
        return damagesource;
    }
    public boolean getSourceIndirect() {
        return damagesourceIndirect;
    }

    public boolean getChunkRemove() {
        return chunkRemove;
    }

}
