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
public abstract class MixinTileEntityGravitationalAnomaly extends MOTileEntity implements IScannable, IMOTickable, IGravitationalAnomaly, ITickable {

    /**
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

    /**
     * @author sddsd2332
     * @reason 如果生物带有重力调整模块，则不会被吸入
     */
    @Inject(method = "manageEntityGravitation", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/EntityLivingBase;getArmorInventoryList()Ljava/lang/Iterable;"), cancellable = true)
    public void GravitationalModulation(World world, float ticks, CallbackInfo ci) {
        double range = getMaxRange() + 1;
        AxisAlignedBB bb = new AxisAlignedBB(getPos().getX() - range, getPos().getY() - range, getPos().getZ() - range, getPos().getX() + range, getPos().getY() + range, getPos().getZ() + range);
        List<Entity> entities = world.getEntitiesWithinAABB(Entity.class, bb);
        for (Entity entity : entities) {
            if (entity instanceof EntityLivingBase) {
                AtomicBoolean se = new AtomicBoolean(false);
                entity.getArmorInventoryList().forEach(i -> {
                    if (i.getItem() instanceof IModuleContainerItem item && item.isModuleEnabled(i, MekanismModules.GRAVITATIONAL_MODULATING_UNIT)) {
                        se.set(true);
                    }
                });
                if (se.get()) {
                    ci.cancel();
                }
            }
        }
    }
}


