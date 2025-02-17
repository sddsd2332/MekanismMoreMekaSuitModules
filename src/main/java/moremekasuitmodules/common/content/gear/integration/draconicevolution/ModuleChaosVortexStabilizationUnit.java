package moremekasuitmodules.common.content.gear.integration.draconicevolution;

import com.brandon3055.draconicevolution.DEConfig;
import com.brandon3055.draconicevolution.entity.EntityChaosImplosion;
import mekanism.api.annotations.ParametersAreNotNullByDefault;
import mekanism.api.gear.ICustomModule;
import mekanism.api.gear.IModule;
import mekanism.common.Mekanism;
import mekanism.common.content.gear.IModuleContainerItem;
import mekanism.common.integration.MekanismHooks;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.Optional;

import java.util.List;

@ParametersAreNotNullByDefault
public class ModuleChaosVortexStabilizationUnit implements ICustomModule<ModuleChaosVortexStabilizationUnit> {

    @Override
    public void tickServer(IModule<ModuleChaosVortexStabilizationUnit> module, EntityPlayer player) {
        if (Mekanism.hooks.DraconicEvolution) {
            removedVortex(module, player);
        }
    }


    @Optional.Method(modid = MekanismHooks.DraconicEvolution_MOD_ID)
    private void removedVortex(IModule<ModuleChaosVortexStabilizationUnit> module, EntityPlayer player) {
        if (!DEConfig.disableChaosIslandExplosion) {
            List<EntityChaosImplosion> entitys = player.world.getEntitiesWithinAABB(EntityChaosImplosion.class, player.getEntityBoundingBox().grow(10, 10, 10));
            for (EntityChaosImplosion entity : entitys) {
                if (entity.getDistance(player) > 0.001) {
                    if (module.getContainer().getItem() instanceof IModuleContainerItem item) {
                        entity.setDead();
                        item.removeModule(module.getContainer(), module.getData());
                    }
                }
            }
        }
    }

}
