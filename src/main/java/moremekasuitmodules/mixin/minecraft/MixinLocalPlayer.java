package moremekasuitmodules.mixin.minecraft;

import com.mojang.authlib.GameProfile;
import mekanism.common.content.gear.IModuleContainerItem;
import moremekasuitmodules.common.registries.MekaSuitMoreModules;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LocalPlayer.class)
@OnlyIn(Dist.CLIENT)
public abstract class MixinLocalPlayer extends AbstractClientPlayer {

    public MixinLocalPlayer(ClientLevel clientLevel, GameProfile gameProfile) {
        super(clientLevel, gameProfile);
    }

    @Inject(method = "moveTowardsClosestSpace", at = @At(value = "HEAD"), cancellable = true)
    public void isPhaseThroughBlocks(double x, double z, CallbackInfo ci) {
        if (isModule((LocalPlayer)(Object)this)){
            ci.cancel();
        }
    }

    @Unique
    public boolean isModule(Player player) {
        //确保玩家是存活的 且玩家不在地面上
        if (player != null && player.isAlive() && !player.noPhysics) {
            //获取胸部盔甲
            ItemStack stack = player.getItemBySlot(EquipmentSlot.CHEST);
            //如果是模块类型物品
            if (!stack.isEmpty() && stack.getItem() instanceof IModuleContainerItem item) {
                //如果启用了量子重建单元
                //取消在方块内被推出
                return item.isModuleEnabled(stack, MekaSuitMoreModules.QUANTUM_RECONSTRUCTION_UNIT);
            }
        }
        return false;
    }
}
