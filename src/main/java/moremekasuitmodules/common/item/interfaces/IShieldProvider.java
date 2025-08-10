package moremekasuitmodules.common.item.interfaces;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

//护盾提供程序
//使用DR2版本，但是只保留部分
public interface IShieldProvider {

    //护盾保护点数
    double getProtectionPoints(ItemStack stack);

    //护盾能量恢复速率
    double getRecoveryRate(ItemStack stack);

    //恢复护盾点数需要多少能量
    int getEnergyPerProtectionPoint();

    //能量消耗使用
    void modifyEnergy(ItemStack stack, int modify, LivingEntity entity);

    //是否启用护盾
    boolean isEnableShield(ItemStack stack, LivingEntity entity);

    //物品当前能量
    int getEnergyStored(ItemStack stack);

    //物品最大能量
    int getMaxEnergyStored(ItemStack stack);
}
