package moremekasuitmodules.mixin.universaltweaks;

import mekanism.common.content.gear.IModuleContainerItem;
import mod.acgaming.universaltweaks.bugfixes.blocks.blockoverlay.UTBlockOverlay;
import moremekasuitmodules.common.MekaSuitMoreModules;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(UTBlockOverlay.class)
public class MixinUTBlockOverlay {

    @Inject(method = "renderNearbyBlocks", at = @At("HEAD"), cancellable = true)
    private static void getMekaSuitModules(float partialTicks, CallbackInfo ci) {
        Minecraft mc = Minecraft.getMinecraft();
        EntityPlayer player = mc.player;
        if (player != null) {
            ItemStack stack = player.getItemStackFromSlot(EntityEquipmentSlot.CHEST);
            if (!stack.isEmpty()) {
                if (stack.getItem() instanceof IModuleContainerItem item) {
                    if (item.isModuleEnabled(stack, MekaSuitMoreModules.QUANTUM_RECONSTRUCTION_UNIT)) {
                        ci.cancel();
                    }
                }
            }
        }
    }
}
