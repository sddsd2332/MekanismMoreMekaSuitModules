package moremekasuitmodules.mixin.appliedenergistics2;

import appeng.core.sync.GuiBridge;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraftforge.fml.common.network.IGuiHandler;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(GuiBridge.class)
public abstract class MixinGuiBridge implements IGuiHandler {

    @Redirect(method = "getServerGuiElement", at = @At(value = "FIELD", target = "Lnet/minecraft/entity/player/InventoryPlayer;mainInventory:Lnet/minecraft/util/NonNullList;", opcode = Opcodes.GETFIELD))
    private NonNullList<ItemStack> getArmorAndMainInventoryServer(InventoryPlayer instance) {
        return NonNullList.withSize(instance.mainInventory.size() + instance.armorInventory.size(), ItemStack.EMPTY);
    }

    @Redirect(method = "getClientGuiElement", at = @At(value = "FIELD", target = "Lnet/minecraft/entity/player/InventoryPlayer;mainInventory:Lnet/minecraft/util/NonNullList;", opcode = Opcodes.GETFIELD))
    private NonNullList<ItemStack> getArmorAndMainInventoryClient(InventoryPlayer instance) {
        return NonNullList.withSize(instance.mainInventory.size() + instance.armorInventory.size(), ItemStack.EMPTY);
    }
}
