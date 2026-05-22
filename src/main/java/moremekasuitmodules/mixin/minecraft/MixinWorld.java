package moremekasuitmodules.mixin.minecraft;

import moremekasuitmodules.common.content.gear.mekanism.mekasuit.OpticalCamouflageHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(World.class)
public abstract class MixinWorld {

    @Redirect(method = "getNearestAttackablePlayer(DDDDDLcom/google/common/base/Function;Lcom/google/common/base/Predicate;)Lnet/minecraft/entity/player/EntityPlayer;",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/EntityPlayer;isEntityAlive()Z"))
    private boolean skipFullyCamouflagedAttackablePlayers(EntityPlayer player) {
        return player.isEntityAlive() && !OpticalCamouflageHelper.isFullyCamouflaged(player);
    }
}
