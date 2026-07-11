package moremekasuitmodules.common;

import mekanism.common.base.IGuiProvider;
import moremekasuitmodules.common.integration.AppliedEnergisticsCompat;
import moremekasuitmodules.common.network.to_client.PacketOreVisualScan;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.List;


public class MoreMekaSuitModulesCommonProxy implements IGuiProvider {


    @Override
    public Container getServerGui(int i, EntityPlayer entityPlayer, World world, BlockPos blockPos) {
        return null;
    }

    @Override
    public Object getClientGui(int i, EntityPlayer entityPlayer, World world, BlockPos blockPos) {
        return null;
    }

    public void init() {
        if (AppliedEnergisticsCompat.shouldLoadLegacyWireless()){
            AppliedEnergisticsCompat.registerLegacyWireless();
        }

    }


    public void preInit() {
    }

    public void registerItemRenders() {
    }

    public void handleOreVisualScan(BlockPos center, List<PacketOreVisualScan.OreEntry> entries) {
    }

    public void handleOreVisualRemove(BlockPos pos) {
    }

    public void handleOreMiningWave(BlockPos center, int radius, int color, int durationTicks) {
    }

    public void handleColoredLightning(int renderer, Vec3d start, Vec3d end, int segments, int color) {
    }

    public void handlePlayerRescueSync(int entityId, float health) {
    }

    public void handleImpactWave(double x, double y, double z, float radius, int color, int durationTicks, int sourceEntityId, float fallDistance) {
    }

    public int getOreVisualClientEntryCount() {
        return 0;
    }
}
