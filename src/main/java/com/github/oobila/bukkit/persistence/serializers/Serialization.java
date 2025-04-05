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

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Serialization {

    private static final Map<Class<?>, KeySerializer<?>> keySerializers = new HashMap<>();
    static {
        register(String.class, new StringSerializer());
        register(ABID.class, new ABIDSerializer());
        register(UUID.class, new UUIDSerializer());
        register(OfflinePlayer.class, new OfflinePlayerSerializer());
        register(World.class, new WorldSerializer());
        register(Location.class, new LocationSerializer());
        register(ZonedDateTime.class, new ZonedDateTimeSerializer());
        register(LocalDate.class, new LocalDateSerializer());
        register(LocalTime.class, new LocalTimeSerializer());
    }

    public static <T> void register(Class<T> type, KeySerializer<T> keySerializer) {
        keySerializers.put(type, keySerializer);
    }

    @SuppressWarnings("unchecked")
    public static <T> String serialize(T t) {
        KeySerializer<T> keySerializer = (KeySerializer<T>) getKeySerializer(t.getClass());
        return keySerializer.serialize(t);
    }

    public static <T> T deserialize(Class<T> type, String s) {
        KeySerializer<T> keySerializer = getKeySerializer(type);
        return keySerializer.deserialize(s);
    }

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
