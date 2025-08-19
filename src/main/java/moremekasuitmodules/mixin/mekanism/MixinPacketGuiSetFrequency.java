package moremekasuitmodules.mixin.mekanism;

import mekanism.common.network.to_server.PacketGuiSetFrequency;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.InteractionHand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(PacketGuiSetFrequency.class)
public class MixinPacketGuiSetFrequency {

    @Redirect(method = "encode", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/FriendlyByteBuf;writeEnum(Ljava/lang/Enum;)Lnet/minecraft/network/FriendlyByteBuf;", ordinal = 1))
    private FriendlyByteBuf moduleEncode(FriendlyByteBuf instance, Enum<?> pValue) {
        instance.writeNullable(pValue, FriendlyByteBuf::writeEnum);
        return instance;
    }

    @Redirect(method = "decode", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/FriendlyByteBuf;readEnum(Ljava/lang/Class;)Ljava/lang/Enum;", ordinal = 1))
    private static Enum<?> moduleDecode(FriendlyByteBuf instance, Class<?> pEnumClass) {
        return instance.readNullable(b -> b.readEnum(InteractionHand.class));
    }
}
