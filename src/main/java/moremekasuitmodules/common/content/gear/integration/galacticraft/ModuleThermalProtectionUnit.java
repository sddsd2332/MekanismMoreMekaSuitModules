package moremekasuitmodules.common.content.gear.integration.galacticraft;

import mekanism.api.annotations.ParametersAreNotNullByDefault;
import mekanism.api.gear.ICustomModule;
import mekanism.api.gear.IModule;
import mekanism.common.Mekanism;
import mekanism.common.integration.MekanismHooks;
import micdoodle8.mods.galacticraft.core.GalacticraftCore;
import micdoodle8.mods.galacticraft.core.entities.player.GCCapabilities;
import micdoodle8.mods.galacticraft.core.entities.player.GCPlayerStats;
import micdoodle8.mods.galacticraft.core.network.PacketSimple;
import micdoodle8.mods.galacticraft.core.util.GCCoreUtil;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.Optional;

@ParametersAreNotNullByDefault
public class ModuleThermalProtectionUnit implements ICustomModule<ModuleThermalProtectionUnit> {

    @Override
    public void tickServer(IModule<ModuleThermalProtectionUnit> module, EntityPlayer player) {
        if (Mekanism.hooks.GC) {
            ThermalStatus(module,player);
        }
    }

    @Optional.Method(modid = MekanismHooks.GC_MOD_ID)
    private void ThermalStatus(IModule<ModuleThermalProtectionUnit> module,EntityPlayer player) {
        if (!player.hasCapability(GCCapabilities.GC_STATS_CAPABILITY, null)) {
            return;
        }
        GCPlayerStats stats = player.getCapability(GCCapabilities.GC_STATS_CAPABILITY, null);
        if (stats != null) {
            stats.setThermalLevelNormalising(true);
            normaliseThermalLevel(module,player, stats, 3);
        }
    }

    @Optional.Method(modid = MekanismHooks.GC_MOD_ID)
    public void normaliseThermalLevel(IModule<ModuleThermalProtectionUnit> module,EntityPlayer player, GCPlayerStats stats, int increment) {
        final int last = stats.getThermalLevel();
        if (stats.getThermalLevel() < 0) {
            module.useEnergy(player,100);
            stats.setThermalLevel(stats.getThermalLevel() + Math.min(increment, -stats.getThermalLevel()));
        } else if (stats.getThermalLevel() > 0) {
            module.useEnergy(player,100);
            stats.setThermalLevel(stats.getThermalLevel() - Math.min(increment, stats.getThermalLevel()));
        }
        if (stats.getThermalLevel() != last) {
            if (player instanceof EntityPlayerMP mp) {
                sendThermalLevelPacket(mp, stats);
            }

        }
    }

    @Optional.Method(modid = MekanismHooks.GC_MOD_ID)
    protected void sendThermalLevelPacket(EntityPlayerMP player, GCPlayerStats stats) {
        GalacticraftCore.packetPipeline.sendTo(new PacketSimple(PacketSimple.EnumSimplePacket.C_UPDATE_THERMAL_LEVEL, GCCoreUtil.getDimensionID(player.world), new Object[]{stats.getThermalLevel(), stats.isThermalLevelNormalising()}), player);
    }
}
