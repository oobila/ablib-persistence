package com.github.oobila.bukkit.persistence.adapters;

import com.github.oobila.bukkit.persistence.adapters.utils.FileAdapterUtils;
import com.github.oobila.bukkit.persistence.caches.ConfigCache;
import com.github.oobila.bukkit.persistence.serializers.Serialization;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ConfigFileAdapter<K, V> implements ConfigCacheAdapter<K, V> {

    private final Map<K, V> localCache = new HashMap<>();
    private final List<K> keys = new ArrayList<>();
    private final List<V> values = new ArrayList<>();
    private LocalDateTime lastUpdated;

    @Override
    public void open(ConfigCache<K, V> cache) {
        File saveFile = FileAdapterUtils.getSaveFile(cache, null);
        if (!saveFile.exists()) {
            Bukkit.getLogger().warning("savefile does not exist");
            FileAdapterUtils.copyDefaults(cache, saveFile);
        }
        FileConfiguration fileConfiguration = YamlConfiguration.loadConfiguration(saveFile);
        updateDefaults(cache, saveFile, fileConfiguration);

        fileConfiguration.getValues(false).entrySet().forEach(entry ->{
            K key = Serialization.deserialize(cache.getKeyType(), entry.getKey());
            V value = (V) entry.getValue();
            localCache.put(key, value);
            keys.add(key);
            values.add(value);
        });
        lastUpdated = LocalDateTime.now();
    }

    private void updateDefaults(ConfigCache<K, V> cache, File saveFile, FileConfiguration fileConfiguration) {
        FileConfiguration defaults = YamlConfiguration.loadConfiguration(
                new InputStreamReader(cache.getPlugin().getResource(FileAdapterUtils.getSimpleFileName(cache)))
        );
        boolean addDefaults = false;
        for (Map.Entry<String, Object> entry : defaults.getValues(false).entrySet()) {
            if (!fileConfiguration.contains(entry.getKey())) {
                fileConfiguration.set(entry.getKey(), entry.getValue());
                addDefaults = true;
            }
        }
        try {
            if (addDefaults) {
                Bukkit.getLogger().info("adding new configuration items for " + cache.getPlugin().getName() + ": " + cache.getName());
                fileConfiguration.save(saveFile);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public V get(K key) {
        return localCache.get(key);
    }

    @Override
    public List<K> keys() {
        return keys;
    }

    @Override
    public List<V> values() {
        return values;
    }

    @Override
    public LocalDateTime getLastUpdated() {
        return lastUpdated;
    }
}
