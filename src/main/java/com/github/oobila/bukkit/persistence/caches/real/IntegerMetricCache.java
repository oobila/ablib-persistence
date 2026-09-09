package com.github.oobila.bukkit.persistence.caches.real;

import com.github.oobila.bukkit.persistence.adapters.code.MapOfConfigurationSerializableCodeAdapter;
import com.github.oobila.bukkit.persistence.adapters.storage.FileStorageAdapter;
import com.github.oobila.bukkit.persistence.adapters.vehicle.DynamicVehicle;
import com.github.oobila.bukkit.persistence.caches.standard.ReadAndWriteCache;

import java.util.HashMap;
import java.util.Map;

/**
 * A file-backed cache of named integer counters/statistics, with {@link #incrementAndGet}/
 * {@link #resolve} providing thread-safe (per-key-locked) increment and read-and-reset
 * operations — handy for things like kill counts, vote tallies, or other simple metrics that
 * don't warrant a full custom value type.
 */
@SuppressWarnings("unused")
public class IntegerMetricCache extends ReadAndWriteCache<String, Integer> {

    /** Per-key monitor objects so concurrent increments/resolves on different keys don't contend with each other. */
    private final Map<String, Object> locks = new HashMap<>();

    public IntegerMetricCache(String pathString) {
        super(
                new DynamicVehicle<>(
                        pathString,
                        false,
                        String.class,
                        new FileStorageAdapter(),
                        new MapOfConfigurationSerializableCodeAdapter<>(Integer.class)
                )
        );
    }

    /** Increments {@code key}'s counter by 1 and returns the new value. */
    public int incrementAndGet(String key) {
        return incrementAndGet(key, 1);
    }

    /** Increments {@code key}'s counter by {@code amount} (creating it at 0 if absent) and returns the new value. */
    public int incrementAndGet(String key, int amount) {
        locks.putIfAbsent(key, new Object());
        synchronized (locks.get(key)) {
            int i = 0;
            if (containsKey(key)) {
                i += getValue(key);
            }
            i += amount;
            putValue(key, i);
            return i;
        }
    }

    /** Returns {@code key}'s current counter value (0 if absent) and removes it from storage. */
    public int resolve(String key) {
        locks.putIfAbsent(key, new Object());
        synchronized (locks.get(key)) {
            int i = 0;
            if (containsKey(key)) {
                i += getValue(key);
            }
            remove(key);
            return i;
        }
    }

}