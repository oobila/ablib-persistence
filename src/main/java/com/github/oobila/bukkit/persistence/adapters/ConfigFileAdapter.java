package com.github.oobila.bukkit.persistence.adapters;

import com.github.oobila.bukkit.persistence.adapters.utils.AdapterUtils;
import com.github.oobila.bukkit.persistence.adapters.utils.FileAdapterUtils;
import com.github.oobila.bukkit.persistence.caches.ConfigCache;
import com.github.oobila.bukkit.persistence.serializers.Serialization;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

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
        YamlConfiguration yamlConfiguration = FileAdapterUtils.loadYaml(this, saveFile);
        updateDefaults(cache, saveFile, yamlConfiguration);
        yamlConfiguration.getValues(false).forEach((key1, value1) -> {
            K key = Serialization.deserialize(cache.getKeyType(), key1);
            V value = (V) value1;
            localCache.put(key, value);
            keys.add(key);
            values.add(value);
        });
        lastUpdated = LocalDateTime.now();
    }

    private void updateDefaults(ConfigCache<K, V> cache, File saveFile, YamlConfiguration yamlConfiguration) {
        try (InputStreamReader reader = new InputStreamReader(cache.getPlugin().getResource(FileAdapterUtils.getSimpleFileName(cache)))) {
            YamlConfiguration defaults = AdapterUtils.loadYaml(this, reader);
            boolean addDefaults = false;
            for (Map.Entry<String, Object> entry : defaults.getValues(false).entrySet()) {
                if (!yamlConfiguration.contains(entry.getKey())) {
                    yamlConfiguration.set(entry.getKey(), entry.getValue());
                    addDefaults = true;
                }
            }
            if (addDefaults) {
                Bukkit.getLogger().info("adding new configuration items for " + cache.getPlugin().getName() + ": " + cache.getName());
                yamlConfiguration.save(saveFile);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void forEach(BiConsumer<K,V> action) {
        localCache.forEach(action);
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
