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

    private static final Map<Class<?>, Serializer<?>> keySerializers = new HashMap<>();
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

    public static <T> void register(Class<T> type, Serializer<T> serializer) {
        keySerializers.put(type, serializer);
    }

    @SuppressWarnings("unchecked")
    public static <T> String serialize(T t) {
        Serializer<T> serializer = (Serializer<T>) getKeySerializer(t.getClass());
        return serializer.serialize(t);
    }

    public static <T> T deserialize(Class<T> type, String s) {
        Serializer<T> serializer = getKeySerializer(type);
        return serializer.deserialize(s);
    }

    @SuppressWarnings("unchecked")
    private static <T> Serializer<T> getKeySerializer(Class<T> type) {
        for (Map.Entry<Class<?>, Serializer<?>> entry : Serialization.keySerializers.entrySet()) {
            if (entry.getKey().isAssignableFrom(type)) {
                return (Serializer<T>) entry.getValue();
            }
        }
        throw new NullPointerException("There is no key serializer for type: " + type.getName());
    }

}
