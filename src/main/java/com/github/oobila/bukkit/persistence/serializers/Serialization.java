package com.github.oobila.bukkit.persistence.serializers;

import com.github.alastairbooth.abid.ABID;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Central registry and lookup point for {@link KeySerializer}s, used by
 * {@link com.github.oobila.bukkit.persistence.adapters.vehicle.DynamicVehicle} to turn cache keys
 * into path/table segments and back. Ships with serializers for the key types most Bukkit plugins
 * need out of the box (see the static initializer below); call {@link #register} to add support
 * for a custom key type.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Serialization {

    private static final Map<Class<?>, KeySerializer<?>> keySerializers = new HashMap<>();
    static {
        register(String.class, new StringSerializer());
        register(Integer.class, new IntSerializer());
        register(int.class, new IntSerializer());
        register(ABID.class, new ABIDSerializer());
        register(UUID.class, new UUIDSerializer());
        register(OfflinePlayer.class, new OfflinePlayerSerializer());
        register(World.class, new WorldSerializer());
        register(Location.class, new LocationSerializer());
        register(ZonedDateTime.class, new ZonedDateTimeSerializer());
        register(LocalDate.class, new LocalDateSerializer());
        register(LocalTime.class, new LocalTimeSerializer());
    }

    /** Registers (or replaces) the {@link KeySerializer} used for keys of type {@code type}. */
    public static <T> void register(Class<T> type, KeySerializer<T> keySerializer) {
        keySerializers.put(type, keySerializer);
    }

    /** Serializes a key using the registered serializer for its runtime type (or nearest registered supertype). */
    @SuppressWarnings("unchecked")
    public static <T> String serialize(T t) {
        KeySerializer<T> keySerializer = (KeySerializer<T>) getKeySerializer(t.getClass());
        return keySerializer.serialize(t);
    }

    /** Deserializes a key string using the registered serializer for {@code type} (or nearest registered supertype). */
    public static <T> T deserialize(Class<T> type, String s) {
        KeySerializer<T> keySerializer = getKeySerializer(type);
        return keySerializer.deserialize(s);
    }

    /** Finds the serializer registered for {@code type}, or the first one registered for an assignable supertype (e.g. any {@code OfflinePlayer} implementation). */
    @SuppressWarnings("unchecked")
    private static <T> KeySerializer<T> getKeySerializer(Class<T> type) {
        for (Map.Entry<Class<?>, KeySerializer<?>> entry : Serialization.keySerializers.entrySet()) {
            if (entry.getKey().isAssignableFrom(type)) {
                return (KeySerializer<T>) entry.getValue();
            }
        }
        throw new NullPointerException("There is no key serializer for type: " + type.getName());
    }

}
