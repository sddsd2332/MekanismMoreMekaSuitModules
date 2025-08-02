package moremekasuitmodules.mixin.lolipickaxe;

import com.anotherstar.util.LoliPickaxeUtil;
import mekanism.common.content.gear.IModuleContainerItem;
import moremekasuitmodules.common.MekaSuitMoreModules;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LoliPickaxeUtil.class)
public class MixinLoliPickaxeUtil {

    /**
     * @author sddsd2332
     * @reason 如果生物带有无限模块，则取消循环设置生物清除
     */
    @Inject(method = "delayKill", at = @At("HEAD"), remap = false, cancellable = true)
    private static void delayKill(EntityLivingBase entity, CallbackInfo ci) {
        if (isInfiniteModule(entity)) {
            ci.cancel();
        }
    }


    @Unique
    private static boolean isInfiniteModule(EntityLivingBase base) {
        if (base != null) {
            ItemStack head = base.getItemStackFromSlot(EntityEquipmentSlot.HEAD);
            if (head.getItem() instanceof IModuleContainerItem item) {
                return item.hasModule(head, MekaSuitMoreModules.INFINITE_INTERCEPTION_AND_RESCUE_SYSTEM_UNIT) || item.isModuleEnabled(head, MekaSuitMoreModules.ADVANCED_INTERCEPTION_SYSTEM_UNIT) || item.isModuleEnabled(head,MekaSuitMoreModules.EMERGENCY_RESCUE_UNIT);
            }
        }
        return false;
    }
}
