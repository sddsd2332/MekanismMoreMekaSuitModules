package moremekasuitmodules.client;

import mekanism.client.render.MekanismRenderer;
import mekanism.common.Mekanism;
import mekanism.common.config.MekanismConfig;
import mekanism.common.lib.Color;
import mekanism.common.lib.effect.BoltEffect;
import mekanism.common.lib.effect.BoltEffect.BoltRenderInfo;
import mekanism.common.lib.effect.BoltEffect.SpawnFunction;
import moremekasuitmodules.client.key.GMUTKeyHandler;
import moremekasuitmodules.common.MekaSuitMoreModulesItem;
import moremekasuitmodules.common.MoreMekaSuitModules;
import moremekasuitmodules.common.MoreMekaSuitModulesCommonProxy;
import moremekasuitmodules.common.config.MoreModulesConfig;
import moremekasuitmodules.common.network.to_client.PacketOreVisualScan;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.Item;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;

@SideOnly(Side.CLIENT)
public class MoreMekaSuitModulesClientProxy extends MoreMekaSuitModulesCommonProxy {

    public void init() {
        super.init();
        MinecraftForge.EVENT_BUS.register(new ClientTickHandler());
        MinecraftForge.EVENT_BUS.register(new RenderTickHandler());
        MinecraftForge.EVENT_BUS.register(new EntityDisplayBoxRenderer());
        MinecraftForge.EVENT_BUS.register(new OreVisualEnhancementRenderer());
        MinecraftForge.EVENT_BUS.register(new OreVisualEnhancementClientEvents());
        MinecraftForge.EVENT_BUS.register(new ImpactWaveRenderer());
        new GMUTKeyHandler();
    }

    @Override
    public void registerItemRenders() {
        registerItemRender(MekaSuitMoreModulesItem.MODULE_EMERGENCY_RESCUE);
        registerItemRender(MekaSuitMoreModulesItem.MODULE_ADVANCED_INTERCEPTION_SYSTEM);
        if (Mekanism.hooks.GC || Mekanism.hooks.AR) {
            registerItemRender(MekaSuitMoreModulesItem.MODULE_SEAL);
        }

        if (Mekanism.hooks.GC) {
            registerItemRender(MekaSuitMoreModulesItem.MODULE_THERMAL_PROTECTION);
        }
        if (Loader.isModLoaded("immersiveengineering") || Mekanism.hooks.GTCEULoaded) {
            registerItemRender(MekaSuitMoreModulesItem.MODULE_INSULATED);
        }
        if (Loader.isModLoaded("forestry")) {
            registerItemRender(MekaSuitMoreModulesItem.MODULE_BEE_CONTROL);
        }
        if (Loader.isModLoaded("thaumcraft")) {
            registerItemRender(MekaSuitMoreModulesItem.MODULE_WARP_CLEAR_BASE);
            registerItemRender(MekaSuitMoreModulesItem.MODULE_WARP_CLEAR_ADVANCED);
            registerItemRender(MekaSuitMoreModulesItem.MODULE_WARP_CLEAR_ULTIMATE);
            registerItemRender(MekaSuitMoreModulesItem.MODULE_MAGIC_OPTIMIZATION);
            registerItemRender(MekaSuitMoreModulesItem.MODULE_GOGGLES_OF_REVEALING);
        }
        if (Loader.isModLoaded("toughasnails")) {
            registerItemRender(MekaSuitMoreModulesItem.MODULE_INTELLIGENT_TEMPERATURE_REGULATION);
            registerItemRender(MekaSuitMoreModulesItem.MODULE_AUTOMATIC_LIQUID_SUPPLY);
        }
        registerItemRender(MekaSuitMoreModulesItem.MODULE_GRAVITATIONAL_MODULATING_ADDITIONAL);

        if (Mekanism.hooks.DraconicEvolution) {
            registerItemRender(MekaSuitMoreModulesItem.MODULE_ENERGY_SHIELD);
            registerItemRender(MekaSuitMoreModulesItem.MODULE_CHAOS_RESISTANCE);
            registerItemRender(MekaSuitMoreModulesItem.MODULE_CHAOS_VORTEX_STABILIZATION);
        }
        if (Loader.isModLoaded("iceandfire")) {
            registerItemRender(MekaSuitMoreModulesItem.MODULE_SMART_SHIELDING);
        }
        registerItemRender(MekaSuitMoreModulesItem.MODULE_INFINITE_ENERGY_SUPPLY);
        if (MoreModulesConfig.current().config.InfiniteInterception.val()){
            registerItemRender(MekaSuitMoreModulesItem.MODULE_INFINITE_INTERCEPTION_AND_RESCUE_SYSTEM);
        }

        if (Loader.isModLoaded("botania")){
            registerItemRender(MekaSuitMoreModulesItem.MODULE_BAND_OF_AURA);
            registerItemRender(MekaSuitMoreModulesItem.MODULE_BASIC_BAND_OF_AURA);
            registerItemRender(MekaSuitMoreModulesItem.MODULE_ADVANCED_BAND_OF_AURA);
            registerItemRender(MekaSuitMoreModulesItem.MODULE_ELITE_BAND_OF_AURA);
            registerItemRender(MekaSuitMoreModulesItem.MODULE_ULTIMATE_BAND_OF_AURA);
            registerItemRender(MekaSuitMoreModulesItem.MODULE_CREATIVE_BAND_OF_AURA);
        }

        if (Loader.isModLoaded("appliedenergistics2")){
            registerItemRender(MekaSuitMoreModulesItem.MODULE_SMART_WIRELESS);
        }
        registerItemRender(MekaSuitMoreModulesItem.MODULE_INFINITE_GAS_SUPPLY);
        registerItemRender(MekaSuitMoreModulesItem.MODULE_AUTOMATIC_ATTACK);
        registerItemRender(MekaSuitMoreModulesItem.MODULE_COUNTERATTACK);
        registerItemRender(MekaSuitMoreModulesItem.MODULE_POWER_ENHANCEMENT);
        registerItemRender(MekaSuitMoreModulesItem.MODULE_HIGH_SPEED_COOLING);
        registerItemRender(MekaSuitMoreModulesItem.MODULE_QUANTUM_RECONSTRUCTION);
        registerItemRender(MekaSuitMoreModulesItem.HP_BOOTS_UNIT);
        registerItemRender(MekaSuitMoreModulesItem.MODULE_OPTICAL_CAMOUFLAGE);
        registerItemRender(MekaSuitMoreModulesItem.MODULE_ENTITY_DISPLAY_BOX);
        registerItemRender(MekaSuitMoreModulesItem.MODULE_WALL_CLING);
        registerItemRender(MekaSuitMoreModulesItem.MODULE_ORE_VISUAL_ENHANCEMENT);
        registerItemRender(MekaSuitMoreModulesItem.MODULE_AUTOMATIC_ORE_MINING);
        registerItemRender(MekaSuitMoreModulesItem.MODULE_LOOTING_AMPLIFICATION);
        registerItemRender(MekaSuitMoreModulesItem.MODULE_KNOCKBACK_CONTROL);
        registerItemRender(MekaSuitMoreModulesItem.MODULE_EXECUTION);
        registerItemRender(MekaSuitMoreModulesItem.MODULE_AUTOMATIC_EXTINGUISH);
        registerItemRender(MekaSuitMoreModulesItem.MODULE_SMELTING);
        registerItemRender(MekaSuitMoreModulesItem.MODULE_IMPACT_WAVE);
    }

    @Override
    public void preInit() {
        MinecraftForge.EVENT_BUS.register(this);
    }

    public void registerItemRender(Item item) {
        MekanismRenderer.registerItemRender(MoreMekaSuitModules.MODID, item);
    }

    @Override
    public void handleOreVisualScan(BlockPos center, List<PacketOreVisualScan.OreEntry> entries) {
        OreVisualScanClientCache.update(center, entries);
    }

    @Override
    public void handleOreVisualRemove(BlockPos pos) {
        OreVisualScanClientCache.remove(pos);
    }

    @Override
    public void handleOreMiningWave(BlockPos center, int radius, int color, int durationTicks) {
        OreVisualScanClientCache.startMiningWave(center, radius, color, durationTicks);
    }

    @Override
    public void handleColoredLightning(int renderer, Vec3d start, Vec3d end, int segments, int color) {
        if (!MekanismConfig.current().client.renderToolAOEParticles.val()) {
            return;
        }
        BoltRenderInfo renderInfo = BoltRenderInfo.electricity().color(Color.argb(normalizeLightningColor(color)));
        BoltEffect bolt = new BoltEffect(renderInfo, start, end, segments).size(0.015F).lifespan(12).spawn(SpawnFunction.NO_DELAY);
        mekanism.client.render.RenderTickHandler.renderBolt(renderer, bolt);
    }

    private int normalizeLightningColor(int color) {
        if ((color & 0xFF000000) == 0) {
            return color | 0xCC000000;
        }
        return color;
    }

    @Override
    public void handlePlayerRescueSync(int entityId, float health) {
        Minecraft minecraft = Minecraft.getMinecraft();
        if (minecraft.world == null) {
            return;
        }
        Entity entity = minecraft.world.getEntityByID(entityId);
        if (entity instanceof EntityLivingBase living) {
            living.isDead = false;
            living.deathTime = 0;
            living.hurtTime = 0;
            living.maxHurtTime = 0;
            if (health > 0.0F) {
                living.setHealth(health);
            }
        }
    }

    @Override
    public void handleImpactWave(double x, double y, double z, float radius, int color, int durationTicks, int sourceEntityId, float fallDistance) {
        ImpactWaveRenderer.addWave(x, y, z, radius, color, durationTicks, sourceEntityId, fallDistance);
    }

    @Override
    public int getOreVisualClientEntryCount() {
        return OreVisualScanClientCache.getEntries().size();
    }
}
