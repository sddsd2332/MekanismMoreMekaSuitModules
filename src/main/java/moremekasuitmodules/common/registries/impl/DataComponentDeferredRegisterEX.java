package moremekasuitmodules.common.registries.impl;

import com.mojang.serialization.Codec;
import mekanism.common.registration.MekanismDeferredHolder;
import mekanism.common.registration.impl.DataComponentDeferredRegister;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.network.codec.ByteBufCodecs;

public class DataComponentDeferredRegisterEX extends DataComponentDeferredRegister {

    public DataComponentDeferredRegisterEX(String namespace) {
        super(namespace);
    }

    public MekanismDeferredHolder<DataComponentType<?>, DataComponentType<Double>> registerDouble(String name) {
        return simple(name, builder -> builder.persistent(Codec.DOUBLE)
                .networkSynchronized(ByteBufCodecs.DOUBLE));
    }


}
