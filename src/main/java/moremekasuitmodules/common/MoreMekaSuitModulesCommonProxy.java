package moremekasuitmodules.common;

import appeng.api.AEApi;
import appeng.api.features.IWirelessTermHandler;
import mekanism.common.MekanismItems;
import mekanism.common.base.IGuiProvider;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Optional;


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
}
