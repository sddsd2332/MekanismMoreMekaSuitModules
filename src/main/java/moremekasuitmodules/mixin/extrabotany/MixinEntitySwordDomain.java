package moremekasuitmodules.mixin.extrabotany;

import com.meteor.extrabotany.common.entity.gaia.EntitySwordDomain;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.UUID;

@Mixin(value = EntitySwordDomain.class, remap = false)
public abstract class MixinEntitySwordDomain {

    @Shadow
    public abstract UUID getUUID();

    @Shadow
    public abstract void setUUID(UUID u);

    @Redirect(
            method = "onUpdate",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/World;getPlayerEntityByUUID(Ljava/util/UUID;)Lnet/minecraft/entity/player/EntityPlayer;"
            )
    )
    private EntityPlayer killDomainWhenPlayerMissing(World world, UUID uuid) {
        EntityPlayer player = world.getPlayerEntityByUUID(uuid);
        if (player == null || player.isDead) {
            ((Entity) (Object) this).setDead();
        }
        return player;
    }

    @Inject(method = "writeEntityToNBT", at = @At(value = "INVOKE", target = "Lnet/minecraft/nbt/NBTTagCompound;setUniqueId(Ljava/lang/String;Ljava/util/UUID;)V"), cancellable = true)
    public void fixSave(NBTTagCompound cmp, CallbackInfo ci) {
        if (getUUID() != null) {
            cmp.setUniqueId("playerlist", getUUID());
        }
        ci.cancel();
    }

    @Inject(method = "readEntityFromNBT", at = @At(value = "INVOKE", target = "Lcom/meteor/extrabotany/common/entity/gaia/EntitySwordDomain;setUUID(Ljava/util/UUID;)V"), cancellable = true)
    public void fixRead(NBTTagCompound cmp, CallbackInfo ci) {
        if (cmp.hasKey("playerlist", 8)) {
            setUUID(cmp.getUniqueId("playerlist"));
        }
        ci.cancel();
    }
}
