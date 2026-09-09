package com.github.oobila.bukkit.persistence.caches.real;

import com.github.oobila.bukkit.persistence.adapters.code.MapOfConfigurationSerializableCodeAdapter;
import com.github.oobila.bukkit.persistence.adapters.storage.FileStorageAdapter;
import com.github.oobila.bukkit.persistence.adapters.vehicle.DynamicVehicle;
import com.github.oobila.bukkit.persistence.caches.standard.ReadAndWriteCache;

/**
 * A synchronous, fully in-memory file-backed cache for {@code ConfigurationSerializable} values
 * (including {@code ItemStack}s) stored as YAML — the most common building block for plugin data
 * such as per-player homes, kits, or saved items. See the module {@code README.md} for a usage
 * example.
 *
 * @param <K> the key type identifying individual records (e.g. {@code String} for a home name)
 * @param <V> the value type being stored (must be {@code ConfigurationSerializable})
 */
@SuppressWarnings("unused")
public class SimpleFileCache<K, V> extends ReadAndWriteCache<K, V> {

    /**
     * @param pathString e.g. {@code "homes/{uuid}/{key}.yml"} for one file per home per player,
     *                    or {@code "homes/{uuid}.yml"} to store all of a player's homes in one file
     */
    public SimpleFileCache(String pathString, Class<K> keyType, Class<V> valueType) {
        super(
                new DynamicVehicle<>(
                        pathString,
                        false,
                        keyType,
                        new FileStorageAdapter(),
                        new MapOfConfigurationSerializableCodeAdapter<>(valueType)
                )
        );
    }

}
