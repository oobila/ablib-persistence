package com.github.oobila.bukkit.persistence.serializers;

import com.github.alastairbooth.abid.ABID;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;

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
        register(Location.class, new LocationSerializer());
    }

    public static <T> void register(Class<T> type, KeySerializer<T> serializer) {
        keySerializers.put(type, serializer);
    }

    public static <T> String serialize(T t) {
        KeySerializer<T> keySerializer = (KeySerializer<T>) getKeySerializer(t.getClass());
        return keySerializer.serialize(t);
    }

    public static <T> T deserialize(Class<T> type, String s) {
        KeySerializer<T> keySerializer = getKeySerializer(type);
        return keySerializer.deserialize(s);
    }

    private static <T> KeySerializer<T> getKeySerializer(Class<T> type) {
        for (Map.Entry<Class<?>, KeySerializer<?>> entry : Serialization.keySerializers.entrySet()) {
            if (entry.getKey().isAssignableFrom(type)) {
                return (KeySerializer<T>) entry.getValue();
            }
        }
        throw new NullPointerException("There is no key serializer for type: " + type.getName());
    }

}
