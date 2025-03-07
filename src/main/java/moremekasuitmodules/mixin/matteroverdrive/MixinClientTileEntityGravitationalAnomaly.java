package moremekasuitmodules.mixin.matteroverdrive;

import matteroverdrive.api.IScannable;
import matteroverdrive.api.gravity.IGravitationalAnomaly;
import matteroverdrive.tile.IMOTickable;
import matteroverdrive.tile.MOTileEntity;
import matteroverdrive.tile.TileEntityGravitationalAnomaly;
import mekanism.common.MekanismModules;
import mekanism.common.content.gear.IModuleContainerItem;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

@Mixin(TileEntityGravitationalAnomaly.class)
public abstract class MixinClientTileEntityGravitationalAnomaly extends MOTileEntity implements IScannable, IMOTickable, IGravitationalAnomaly, ITickable {

    /** 为什么是客户端？
     * @author sddsd2332
     * @reason 如果玩家带有重力调整模块，则不会被吸入
     */
    @Inject(method = "manageClientEntityGravitation", at = @At(value = "INVOKE", target = "Lmatteroverdrive/tile/TileEntityGravitationalAnomaly;getAcceleration(D)D"), remap = false, cancellable = true)
    public void GravitationalModulation(World world, CallbackInfo ci) {
        Minecraft minecraft = Minecraft.getMinecraft();
        EntityPlayer player = minecraft.player;
        ItemStack chestStack = player.getItemStackFromSlot(EntityEquipmentSlot.CHEST);
        if (chestStack.getItem() instanceof IModuleContainerItem item && item.isModuleEnabled(chestStack, MekanismModules.GRAVITATIONAL_MODULATING_UNIT)) {
            ci.cancel();
        }
    }


}


