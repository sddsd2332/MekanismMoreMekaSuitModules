package earth.terrarium.adastra.api.events;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;

import java.util.ArrayList;
import java.util.List;

/**
 * Event system for Ad Astra gameplay mechanics.
 * <p>
 * This class provides hooks for third-party mods to interact with and modify Ad Astra's
 * environmental systems (oxygen, gravity, temperature) and other space-related mechanics.
 * <p>
 * All events use a simple listener pattern. Mods can register listeners using the static
 * {@code register()} method on each event interface.
 * <p>
 * Example usage:
 * <pre>{@code
 * AdAstraEvents.OxygenTickEvent.register((world, entity) -> {
 *     // Custom logic here
 *     return true; // Return false to cancel oxygen tick
 * });
 * }</pre>
 *
 * @since 1.12.2
 */
public final class AdAstraEvents {

    private static final List<OxygenTickEvent> OXYGEN_TICK_LISTENERS = new ArrayList<>();
    private static final List<EntityOxygenEvent> ENTITY_OXYGEN_LISTENERS = new ArrayList<>();
    private static final List<TemperatureTickEvent> TEMPERATURE_TICK_LISTENERS = new ArrayList<>();
    private static final List<HotTemperatureTickEvent> HOT_TEMPERATURE_TICK_LISTENERS = new ArrayList<>();
    private static final List<ColdTemperatureTickEvent> COLD_TEMPERATURE_TICK_LISTENERS = new ArrayList<>();
    private static final List<EntityGravityEvent> ENTITY_GRAVITY_LISTENERS = new ArrayList<>();
    private static final List<GravityTickEvent> GRAVITY_TICK_LISTENERS = new ArrayList<>();
    private static final List<ZeroGravityTickEvent> ZERO_GRAVITY_TICK_LISTENERS = new ArrayList<>();
    private static final List<AcidRainTickEvent> ACID_RAIN_TICK_LISTENERS = new ArrayList<>();
    private static final List<EnvironmentTickEvent> ENVIRONMENT_TICK_LISTENERS = new ArrayList<>();

    private AdAstraEvents() {
    }

    /**
     * Event fired when oxygen deprivation effects are about to be applied to an entity.
     * Listeners can cancel the oxygen tick by returning false.
     */
    @FunctionalInterface
    public interface OxygenTickEvent {

        /**
         * Called when an entity is in a no-oxygen environment.
         *
         * @param world The world the entity is in
         * @param entity The entity that might suffocate
         * @return false to prevent oxygen deprivation, true to allow it
         */
        boolean tick(WorldServer world, EntityLivingBase entity);

        /**
         * Registers a listener for this event.
         *
         * @param listener The listener to register
         */
        static void register(OxygenTickEvent listener) {
            OXYGEN_TICK_LISTENERS.add(listener);
        }

        /**
         * Internal method to fire the event to all registered listeners.
         *
         * @param world The world
         * @param entity The entity
         * @return false if any listener cancelled the event, true otherwise
         */
        static boolean fire(WorldServer world, EntityLivingBase entity) {
            for (OxygenTickEvent listener : OXYGEN_TICK_LISTENERS) {
                if (!listener.tick(world, entity)) {
                    return false;
                }
            }
            return true;
        }
    }

    /**
     * Event fired to determine if an entity has oxygen at its current position.
     * Listeners can modify the oxygen availability for specific entities.
     */
    @FunctionalInterface
    public interface EntityOxygenEvent {

        /**
         * Called to check if an entity has oxygen.
         *
         * @param entity The entity to check
         * @param hasOxygen The current oxygen state
         * @return The modified oxygen state (true if entity has oxygen, false otherwise)
         */
        boolean hasOxygen(Entity entity, boolean hasOxygen);

        /**
         * Registers a listener for this event.
         *
         * @param listener The listener to register
         */
        static void register(EntityOxygenEvent listener) {
            ENTITY_OXYGEN_LISTENERS.add(listener);
        }

        /**
         * Internal method to fire the event to all registered listeners.
         *
         * @param entity The entity
         * @param hasOxygen The initial oxygen state
         * @return The modified oxygen state
         */
        static boolean fire(Entity entity, boolean hasOxygen) {
            for (EntityOxygenEvent listener : ENTITY_OXYGEN_LISTENERS) {
                boolean newOxygen = listener.hasOxygen(entity, hasOxygen);
                if (newOxygen != hasOxygen) {
                    return newOxygen;
                }
            }
            return hasOxygen;
        }
    }

    /**
     * Event fired when temperature effects are about to be applied to an entity.
     */
    @FunctionalInterface
    public interface TemperatureTickEvent {

        /**
         * Called when temperature effects might be applied to an entity.
         *
         * @param world The world the entity is in
         * @param entity The entity affected by temperature
         * @return false to prevent temperature effects, true to allow them
         */
        boolean tick(WorldServer world, EntityLivingBase entity);

        /**
         * Registers a listener for this event.
         *
         * @param listener The listener to register
         */
        static void register(TemperatureTickEvent listener) {
            TEMPERATURE_TICK_LISTENERS.add(listener);
        }

        /**
         * Internal method to fire the event.
         *
         * @param world The world
         * @param entity The entity
         * @return false if any listener cancelled the event, true otherwise
         */
        static boolean fire(WorldServer world, EntityLivingBase entity) {
            for (TemperatureTickEvent listener : TEMPERATURE_TICK_LISTENERS) {
                if (!listener.tick(world, entity)) {
                    return false;
                }
            }
            return true;
        }
    }

    /**
     * Event fired when hot temperature effects (burning) are about to be applied.
     */
    @FunctionalInterface
    public interface HotTemperatureTickEvent {

        /**
         * Called when an entity is in a hot environment.
         *
         * @param world The world the entity is in
         * @param entity The entity that might burn
         * @return false to prevent burning, true to allow it
         */
        boolean tick(WorldServer world, EntityLivingBase entity);

        /**
         * Registers a listener for this event.
         *
         * @param listener The listener to register
         */
        static void register(HotTemperatureTickEvent listener) {
            HOT_TEMPERATURE_TICK_LISTENERS.add(listener);
        }

        /**
         * Internal method to fire the event.
         *
         * @param world The world
         * @param entity The entity
         * @return false if any listener cancelled the event, true otherwise
         */
        static boolean fire(WorldServer world, EntityLivingBase entity) {
            for (HotTemperatureTickEvent listener : HOT_TEMPERATURE_TICK_LISTENERS) {
                if (!listener.tick(world, entity)) {
                    return false;
                }
            }
            return true;
        }
    }

    /**
     * Event fired when cold temperature effects (freezing) are about to be applied.
     */
    @FunctionalInterface
    public interface ColdTemperatureTickEvent {

        /**
         * Called when an entity is in a cold environment.
         *
         * @param world The world the entity is in
         * @param entity The entity that might freeze
         * @return false to prevent freezing, true to allow it
         */
        boolean tick(WorldServer world, EntityLivingBase entity);

        /**
         * Registers a listener for this event.
         *
         * @param listener The listener to register
         */
        static void register(ColdTemperatureTickEvent listener) {
            COLD_TEMPERATURE_TICK_LISTENERS.add(listener);
        }

        /**
         * Internal method to fire the event.
         *
         * @param world The world
         * @param entity The entity
         * @return false if any listener cancelled the event, true otherwise
         */
        static boolean fire(WorldServer world, EntityLivingBase entity) {
            for (ColdTemperatureTickEvent listener : COLD_TEMPERATURE_TICK_LISTENERS) {
                if (!listener.tick(world, entity)) {
                    return false;
                }
            }
            return true;
        }
    }

    /**
     * Event fired when gravity effects are about to be applied to an entity.
     */
    @FunctionalInterface
    public interface GravityTickEvent {

        /**
         * Called when gravity affects an entity's movement.
         *
         * @param world The world the entity is in
         * @param entity The entity affected by gravity
         * @param travelVector The entity's travel vector
         * @param movementAffectingPos The position affecting movement
         * @return false to prevent gravity effects, true to allow them
         */
        boolean tick(World world, EntityLivingBase entity, Vec3d travelVector, BlockPos movementAffectingPos);

        /**
         * Registers a listener for this event.
         *
         * @param listener The listener to register
         */
        static void register(GravityTickEvent listener) {
            GRAVITY_TICK_LISTENERS.add(listener);
        }

        /**
         * Internal method to fire the event.
         *
         * @param world The world
         * @param entity The entity
         * @param travelVector The travel vector
         * @param movementAffectingPos The movement position
         * @return false if any listener cancelled the event, true otherwise
         */
        static boolean fire(World world, EntityLivingBase entity, Vec3d travelVector, BlockPos movementAffectingPos) {
            for (GravityTickEvent listener : GRAVITY_TICK_LISTENERS) {
                if (!listener.tick(world, entity, travelVector, movementAffectingPos)) {
                    return false;
                }
            }
            return true;
        }
    }

    /**
     * Event fired to determine the gravity value for an entity.
     * Listeners can modify the gravity affecting specific entities.
     */
    @FunctionalInterface
    public interface EntityGravityEvent {

        /**
         * Called to get the gravity for an entity.
         *
         * @param entity The entity
         * @param gravity The current gravity value
         * @return The modified gravity value
         */
        float getGravity(Entity entity, float gravity);

        /**
         * Registers a listener for this event.
         *
         * @param listener The listener to register
         */
        static void register(EntityGravityEvent listener) {
            ENTITY_GRAVITY_LISTENERS.add(listener);
        }

        /**
         * Internal method to fire the event.
         *
         * @param entity The entity
         * @param gravity The initial gravity
         * @return The modified gravity value
         */
        static float fire(Entity entity, float gravity) {
            for (EntityGravityEvent listener : ENTITY_GRAVITY_LISTENERS) {
                float newGravity = listener.getGravity(entity, gravity);
                if (newGravity != gravity) {
                    return newGravity;
                }
            }
            return gravity;
        }
    }

    /**
     * Event fired when zero gravity effects are applied to an entity.
     */
    @FunctionalInterface
    public interface ZeroGravityTickEvent {

        /**
         * Called when zero gravity affects an entity.
         *
         * @param world The world the entity is in
         * @param entity The entity in zero gravity
         * @param travelVector The entity's travel vector
         * @param movementAffectingPos The position affecting movement
         * @return false to prevent zero gravity effects, true to allow them
         */
        boolean tick(World world, EntityLivingBase entity, Vec3d travelVector, BlockPos movementAffectingPos);

        /**
         * Registers a listener for this event.
         *
         * @param listener The listener to register
         */
        static void register(ZeroGravityTickEvent listener) {
            ZERO_GRAVITY_TICK_LISTENERS.add(listener);
        }

        /**
         * Internal method to fire the event.
         *
         * @param world The world
         * @param entity The entity
         * @param travelVector The travel vector
         * @param movementAffectingPos The movement position
         * @return false if any listener cancelled the event, true otherwise
         */
        static boolean fire(World world, EntityLivingBase entity, Vec3d travelVector, BlockPos movementAffectingPos) {
            for (ZeroGravityTickEvent listener : ZERO_GRAVITY_TICK_LISTENERS) {
                if (!listener.tick(world, entity, travelVector, movementAffectingPos)) {
                    return false;
                }
            }
            return true;
        }
    }

    /**
     * Event fired when acid rain effects are about to be applied to an entity.
     * Acid rain occurs on Venus and can damage unprotected entities.
     */
    @FunctionalInterface
    public interface AcidRainTickEvent {

        /**
         * Called when an entity is exposed to acid rain.
         *
         * @param world The world the entity is in
         * @param entity The entity exposed to acid rain
         * @return false to prevent acid rain damage, true to allow it
         */
        boolean tick(WorldServer world, EntityLivingBase entity);

        /**
         * Registers a listener for this event.
         *
         * @param listener The listener to register
         */
        static void register(AcidRainTickEvent listener) {
            ACID_RAIN_TICK_LISTENERS.add(listener);
        }

        /**
         * Internal method to fire the event.
         *
         * @param world The world
         * @param entity The entity
         * @return false if any listener cancelled the event, true otherwise
         */
        static boolean fire(WorldServer world, EntityLivingBase entity) {
            for (AcidRainTickEvent listener : ACID_RAIN_TICK_LISTENERS) {
                if (!listener.tick(world, entity)) {
                    return false;
                }
            }
            return true;
        }
    }

    /**
     * Event fired for random environmental ticks on planets.
     * This is used for effects like freezing water, breaking plants in extreme temperatures, etc.
     */
    @FunctionalInterface
    public interface EnvironmentTickEvent {

        /**
         * Called for random environmental effects on blocks.
         *
         * @param world The world
         * @param pos The block position
         * @param state The block state
         * @param temperature The temperature at this position
         * @return false to prevent environmental effects, true to allow them
         */
        boolean tick(WorldServer world, BlockPos pos, IBlockState state, short temperature);

        /**
         * Registers a listener for this event.
         *
         * @param listener The listener to register
         */
        static void register(EnvironmentTickEvent listener) {
            ENVIRONMENT_TICK_LISTENERS.add(listener);
        }

        /**
         * Internal method to fire the event.
         *
         * @param world The world
         * @param pos The position
         * @param state The block state
         * @param temperature The temperature
         * @return false if any listener cancelled the event, true otherwise
         */
        static boolean fire(WorldServer world, BlockPos pos, IBlockState state, short temperature) {
            for (EnvironmentTickEvent listener : ENVIRONMENT_TICK_LISTENERS) {
                if (!listener.tick(world, pos, state, temperature)) {
                    return false;
                }
            }
            return true;
        }
    }
}
