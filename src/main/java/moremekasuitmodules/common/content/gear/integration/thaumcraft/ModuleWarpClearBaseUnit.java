package moremekasuitmodules.common.content.gear.integration.thaumcraft;

import mekanism.api.annotations.ParametersAreNotNullByDefault;
import mekanism.api.gear.ICustomModule;
import mekanism.api.gear.IModule;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Optional;
import thaumcraft.api.ThaumcraftApi;
import thaumcraft.api.capabilities.IPlayerWarp;
import thaumcraft.api.capabilities.IPlayerWarp.EnumWarpType;
import thaumcraft.api.capabilities.ThaumcraftCapabilities;

@ParametersAreNotNullByDefault
public class ModuleWarpClearBaseUnit implements ICustomModule<ModuleWarpClearBaseUnit> {

    @Override
    public void tickServer(IModule<ModuleWarpClearBaseUnit> module, EntityPlayer player) {
        if (Loader.isModLoaded("thaumcraft")) {
            ClearWarpBase(module,player);
        }
    }

    @Optional.Method(modid = "thaumcraft")
    private void ClearWarpBase(IModule<ModuleWarpClearBaseUnit> module,EntityPlayer player) {
        if (!player.hasCapability(ThaumcraftCapabilities.WARP, null)) {
            return;
        }
        IPlayerWarp warp = player.getCapability(ThaumcraftCapabilities.WARP, null);
        if (warp != null && warp.get(EnumWarpType.TEMPORARY) != 0) {
            module.useEnergy(player, warp.get(EnumWarpType.TEMPORARY));
            warp.set(EnumWarpType.TEMPORARY, player.isCreative() ? 0 : warp.get(EnumWarpType.TEMPORARY) - 1);
            ThaumcraftApi.internalMethods.addWarpToPlayer(player, player.isCreative() ? warp.get(EnumWarpType.TEMPORARY) : -1, EnumWarpType.TEMPORARY);
        }
    }

}
