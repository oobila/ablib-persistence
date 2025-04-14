package com.github.oobila.bukkit.persistence.caches.real;

import com.github.oobila.bukkit.persistence.adapters.code.MapOfConfigurationSerializableCodeAdapter;
import com.github.oobila.bukkit.persistence.adapters.storage.FileStorageAdapter;
import com.github.oobila.bukkit.persistence.adapters.vehicle.DynamicVehicle;
import com.github.oobila.bukkit.persistence.caches.standard.ReadAndWriteCache;

import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("unused")
public class IntegerMetricCache extends ReadAndWriteCache<String, Integer> {

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

    public int incrementAndGet(String key) {
        return incrementAndGet(key, 1);
    }

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