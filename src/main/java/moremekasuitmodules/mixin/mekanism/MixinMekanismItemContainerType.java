package moremekasuitmodules.mixin.mekanism;

import mekanism.common.inventory.container.type.MekanismItemContainerType;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.InteractionHand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = MekanismItemContainerType.class, remap = false)
public class MixinMekanismItemContainerType {

    @Redirect(method = "lambda$item$0", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/FriendlyByteBuf;readEnum(Ljava/lang/Class;)Ljava/lang/Enum;"))
    private static Enum<?> addModule0(FriendlyByteBuf instance, Class<?> pEnumClass) {
        return instance.readNullable(b -> b.readEnum(InteractionHand.class));
    }

    @Redirect(method = "lambda$item$1", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/FriendlyByteBuf;readEnum(Ljava/lang/Class;)Ljava/lang/Enum;"))
    private static Enum<?> addModule1(FriendlyByteBuf instance, Class<?> pEnumClass) {
        return instance.readNullable(b -> b.readEnum(InteractionHand.class));
    }

}
