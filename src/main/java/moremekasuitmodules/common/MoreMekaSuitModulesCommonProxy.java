package moremekasuitmodules.common;

import appeng.api.AEApi;
import appeng.api.features.IWirelessTermHandler;
import mekanism.common.MekanismItems;
import mekanism.common.base.IGuiProvider;
import moremekasuitmodules.common.network.to_client.PacketOreVisualScan;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Optional;

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
        if (Loader.isModLoaded("appliedenergistics2")){
            AEregistries();
        }

    }

    @Optional.Method(modid = "appliedenergistics2")
    public void AEregistries(){
        AEApi.instance().registries().wireless().registerWirelessHandler((IWirelessTermHandler)MekanismItems.MEKASUIT_HELMET);
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
