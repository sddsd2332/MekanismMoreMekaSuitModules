package moremekasuitmodules.mixin.appliedenergistics2;

import appeng.api.AEApi;
import appeng.api.config.Settings;
import appeng.api.config.SortDir;
import appeng.api.config.SortOrder;
import appeng.api.config.ViewItems;
import appeng.api.features.ILocatable;
import appeng.api.features.IWirelessTermHandler;
import appeng.api.util.IConfigManager;
import appeng.core.sync.GuiBridge;
import appeng.util.ConfigManager;
import mekanism.api.gear.IModule;
import mekanism.common.MekanismItems;
import mekanism.common.item.armor.ItemMekaSuitArmor;
import mekanism.common.item.armor.ItemMekaSuitHelmet;
import mekanism.common.util.LangUtils;
import moremekasuitmodules.common.MekaSuitMoreModules;
import moremekasuitmodules.common.content.gear.integration.appliedenergistics2.ModuleSmartWirelessUnit;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(ItemMekaSuitHelmet.class)
public abstract class MixinItemMekaSuitHelmet extends ItemMekaSuitArmor implements IWirelessTermHandler {

    public MixinItemMekaSuitHelmet(int renderIndex, EntityEquipmentSlot slot) {
        super(renderIndex, slot);
    }

    @SideOnly(Side.CLIENT)
    @Inject(method = "addInformation", at = @At("TAIL"), remap = false)
    public void aeModule(ItemStack stack, World world, List<String> tooltip, CallbackInfo ci) {
        addaemodule(stack, tooltip);
    }

    @Unique
    public void addaemodule(ItemStack stack, List<String> tooltip) {
        if (isModuleEnabled(stack, MekaSuitMoreModules.SMART_WIRELESS_UNIT)) {
            String unparsedKey = getEncryptionKey(stack);
            if (unparsedKey.isEmpty()) {
                tooltip.add(LangUtils.localize("chat.appliedenergistics2.DeviceNotLinked"));
            } else {
                long parsedKey = Long.parseLong(unparsedKey);
                ILocatable securityStation = AEApi.instance().registries().locatable().getLocatableBy(parsedKey);
                if (securityStation == null) {
                    tooltip.add(LangUtils.localize("chat.appliedenergistics2.StationCanNotBeLocated"));
                } else {
                    tooltip.add(LangUtils.localize("chat.appliedenergistics2.DeviceLinked"));
                }
            }
        }
    }

    @Override
    public boolean canHandle(ItemStack itemStack) {
        return itemStack.getItem() == MekanismItems.MEKASUIT_HELMET && hasModule(itemStack, MekaSuitMoreModules.SMART_WIRELESS_UNIT);
    }

    @Override
    public boolean usePower(EntityPlayer entityPlayer, double amount, ItemStack itemStack) {
        return getEnergy(itemStack) >= amount - 0.5 && isModuleEnabled(itemStack, MekaSuitMoreModules.SMART_WIRELESS_UNIT) && entityPlayer.getItemStackFromSlot(EntityEquipmentSlot.HEAD).getItem() == MekanismItems.MEKASUIT_HELMET;
    }

    @Override
    public boolean hasPower(EntityPlayer entityPlayer, double amount, ItemStack itemStack) {
        return getEnergy(itemStack) > amount && isModuleEnabled(itemStack, MekaSuitMoreModules.SMART_WIRELESS_UNIT) && entityPlayer.getItemStackFromSlot(EntityEquipmentSlot.HEAD).getItem() == MekanismItems.MEKASUIT_HELMET;
    }

    @Override
    public IConfigManager getConfigManager(ItemStack itemStack) {
        final ConfigManager out = new ConfigManager((manager, settingName, newValue) -> {
            final NBTTagCompound data = openNbtData(itemStack);
            manager.writeToNBT(data);
        });
        out.registerSetting(Settings.SORT_BY, SortOrder.NAME);
        out.registerSetting(Settings.VIEW_MODE, ViewItems.ALL);
        out.registerSetting(Settings.SORT_DIRECTION, SortDir.ASCENDING);
        out.readFromNBT(openNbtData(itemStack).copy());
        return out;
    }

    @Override
    public IGuiHandler getGuiHandler(ItemStack itemStack) {
        IModule<ModuleSmartWirelessUnit> module = getModule(itemStack, MekaSuitMoreModules.SMART_WIRELESS_UNIT);
        if (module != null && module.isEnabled()) {
            switch (module.getCustomInstance().getMode()) {
                case WIRELESS_TERMINAL -> {
                    return GuiBridge.GUI_WIRELESS_TERM;
                }
                case WIRELESS_CRAFTING_TERMINAL -> {
                    return GuiBridge.GUI_WIRELESS_CRAFTING_TERMINAL;
                }
                case WIRELESS_PATTERN_TERMINAL -> {
                    return GuiBridge.GUI_WIRELESS_PATTERN_TERMINAL;
                }
                case WIRELESS_FLUID_TERMINAL -> {
                    return GuiBridge.GUI_WIRELESS_FLUID_TERMINAL;
                }
            }
        }
        return GuiBridge.GUI_WIRELESS_TERM;
    }

    @Override
    public String getEncryptionKey(ItemStack itemStack) {
        final NBTTagCompound tag = openNbtData(itemStack);
        return tag.getString("encryptionKey");
    }

    @Override
    public void setEncryptionKey(ItemStack item, String encKey, String name) {
        final NBTTagCompound tag = openNbtData(item);
        tag.setString("encryptionKey", encKey);
        tag.setString("name", name);
    }


    @Unique
    private static NBTTagCompound openNbtData(ItemStack i) {
        NBTTagCompound compound = i.getTagCompound();
        if (compound == null) {
            i.setTagCompound(compound = new NBTTagCompound());
        }

        return compound;
    }
}
