package moremekasuitmodules.mixin.minecraft;

import com.google.common.collect.Sets;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.ai.attributes.Attributes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Set;

@Mixin(LivingEntity.class)
public abstract class MixinLivingEntity {

    @Shadow
    public abstract AttributeMap getAttributes();

    @Shadow
    public abstract float getMaxHealth();

    @Shadow
    public abstract float getHealth();

    @Shadow
    public abstract void setHealth(float p_21154_);


    @Inject(method = "tick", at = @At("TAIL"))
    public void fixHealth(CallbackInfo ci) {
        refreshDirtyAttributes();
    }

    @Unique
    private void refreshDirtyAttributes() {
        Set<AttributeInstance> set = Sets.newHashSet(getAttributes().attributes.values());
        for (AttributeInstance attributeinstance : set) {
            this.onAttributeUpdated(attributeinstance.getAttribute());
        }
        set.clear();
    }

    @Unique
    private void onAttributeUpdated(Attribute attribute) {
        if (attribute.getDescriptionId().equals(Attributes.MAX_HEALTH.getDescriptionId())) {
            float f = getMaxHealth();
            if (this.getHealth() > f) {
                this.setHealth(f);
            }
        }
    }
}
