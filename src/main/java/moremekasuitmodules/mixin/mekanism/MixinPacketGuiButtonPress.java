package moremekasuitmodules.mixin.mekanism;

import mekanism.common.network.to_server.PacketGuiButtonPress;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.InteractionHand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(PacketGuiButtonPress.class)
public class MixinPacketGuiButtonPress {

    @Redirect(method = "lambda$handle$2", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/FriendlyByteBuf;writeEnum(Ljava/lang/Enum;)Lnet/minecraft/network/FriendlyByteBuf;"), remap = false)
    private FriendlyByteBuf moduleTryOpenGui(FriendlyByteBuf instance, Enum<?> pValue) {
        instance.writeNullable(pValue, FriendlyByteBuf::writeEnum);
        return instance;
    }

    @Redirect(method = "encode", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/FriendlyByteBuf;writeEnum(Ljava/lang/Enum;)Lnet/minecraft/network/FriendlyByteBuf;", ordinal = 4))
    private FriendlyByteBuf moduleEncode(FriendlyByteBuf instance, Enum<?> pValue) {
        instance.writeNullable(pValue, FriendlyByteBuf::writeEnum);
        return instance;
    }

    @Redirect(method = "decode", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/FriendlyByteBuf;readEnum(Ljava/lang/Class;)Ljava/lang/Enum;", ordinal = 4))
    private static Enum<?> moduleDecode(FriendlyByteBuf instance, Class<?> pEnumClass) {
        return instance.readNullable(b -> b.readEnum(InteractionHand.class));
    }
}
