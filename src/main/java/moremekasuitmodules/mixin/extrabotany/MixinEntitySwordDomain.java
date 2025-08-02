package moremekasuitmodules.mixin.extrabotany;

import com.meteor.extrabotany.api.ExtraBotanyAPI;
import com.meteor.extrabotany.common.core.config.ConfigHandler;
import com.meteor.extrabotany.common.entity.gaia.EntityGaiaIII;
import com.meteor.extrabotany.common.entity.gaia.EntitySwordDomain;
import com.meteor.extrabotany.common.entity.gaia.EntityVoidHerrscher;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import vazkii.botania.common.Botania;
import vazkii.botania.common.core.helper.MathHelper;

import java.util.List;
import java.util.UUID;

@Mixin(value = EntitySwordDomain.class,remap = false)
public abstract class MixinEntitySwordDomain extends Entity {

    public MixinEntitySwordDomain(World worldIn) {
        super(worldIn);
    }

    @Shadow
    public abstract UUID getUUID();

    @Shadow
    public abstract void setUUID(UUID u);

    @Shadow
    protected abstract List<EntityGaiaIII> getHostAround();

    @Shadow
    protected abstract List<EntityVoidHerrscher> getVoidAround();

    @Shadow
    public abstract int getCount();

    @Shadow
    protected abstract void keepInsideArena(EntityPlayer player);

    @Shadow
    public abstract BlockPos getSource();

    /**
     * @author sddsd2332
     * @reason 完全覆盖便于修复
     */
    @Override
    public void onUpdate() {
        super.onUpdate();
        if (getHostAround().isEmpty() && getVoidAround().isEmpty()) {
            setDead();
        }
        setPosition(posX, Math.max(posY - 0.01F, getCount()), posZ);
        float m = 0.45F;
        if (getUUID() == null) {
            setDead();
        }else {
            //插入修复
            EntityPlayer player = world.getPlayerEntityByUUID(getUUID());
            //如果玩家不为空
            if (player != null) {
                //如果玩家存活
                if (player.isEntityAlive()) {
                    if (ticksExisted > 70) {
                        keepInsideArena(player);
                        if (MathHelper.pointDistancePlane(player.posX, player.posZ, getSource().getX(), getSource().getZ()) > 20F) {
                            player.attemptTeleport(getSource().getX(), getSource().getY(), getSource().getZ());
                        }
                        for (int i = 0; i < 2 * ConfigHandler.PARTICLE; i++) {
                            Botania.proxy.wispFX(getSource().getX(), getSource().getY() + 3, getSource().getZ(), 1F, 0.9F, 0F, 0.25F, (float) (Math.random() - 0.5F) * m, (float) (Math.random() - 0.5F) * m, (float) (Math.random() - 0.5F) * m);
                        }
                        if (ticksExisted % 50 == 0) {
                            player.attackEntityFrom(DamageSource.MAGIC, 1F);
                        }
                        if (this.ticksExisted == 200) {
                            ExtraBotanyAPI.dealTrueDamage(player, player, 1F);
                        }
                        if (this.ticksExisted > 201) {
                            for (int i = 0; i < 5 * ConfigHandler.PARTICLE; i++) {
                                Botania.proxy.wispFX(posX, posY, posZ, (float) Math.random(), (float) Math.random(), (float) Math.random(), 0.25F, (float) (Math.random() - 0.5F) * m, (float) (Math.random() - 0.5F) * m, (float) (Math.random() - 0.5F) * m);
                            }
                            setDead();
                        }
                    }
                } else {
                    setDead();
                }
            } else {
                setDead();
            }
        }
    }


    /**
     * @author sddsd2332
     * @reason 怎么有人不判断NBT是不是空的就写保存啊
     */
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
