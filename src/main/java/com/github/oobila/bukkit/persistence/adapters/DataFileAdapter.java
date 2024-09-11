package com.github.oobila.bukkit.persistence.adapters;

import com.github.oobila.bukkit.persistence.adapters.utils.FileAdapterUtils;
import com.github.oobila.bukkit.persistence.caches.BaseCache;
import com.github.oobila.bukkit.persistence.model.PersistedObject;
import com.github.oobila.bukkit.persistence.serializers.Serialization;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.logging.Level;

import static com.github.oobila.bukkit.common.ABCommon.log;

public class DataFileAdapter<K, V extends PersistedObject> implements DataCacheAdapter<K, V> {

    private final Map<K, V> localCache = new HashMap<>();

    @Override
    public void open(BaseCache<K, V> cache) {
        File saveFile = FileAdapterUtils.getSaveFile(cache, null);
        if (!saveFile.exists()) {
            Bukkit.getLogger().warning("savefile does not exist");
            FileAdapterUtils.copyDefaults(cache, saveFile);
        }
        localCache.putAll(onLoad(saveFile, cache.getKeyType()));
    }

    @Override
    public void close(BaseCache<K, V> cache) {
        File saveFile = FileAdapterUtils.getSaveFile(cache, null);
        if (saveFile.exists()) {
            try {
                Files.delete(saveFile.toPath());
            } catch (IOException e) {
                log(Level.SEVERE, e);
            }
        }
        onSave(saveFile, localCache.entrySet());
    }

    @Override
    public boolean contains(K key, BaseCache<K, V> dataCache) {
        return localCache.containsKey(key);
    }

    @Override
    public int size(BaseCache<K, V> dataCache) {
        return localCache.size();
    }

    @Override
    public void put(K key, V value, BaseCache<K, V> dataCache) {
        localCache.put(key, value);
    }

    @Override
    public V get(K key, BaseCache<K, V> dataCache) {
        return localCache.get(key);
    }

    @Override
    public List<V> get(BaseCache<K, V> dataCache) {
        return new ArrayList<>(localCache.values());
    }

    @Override
    public V remove(K key, BaseCache<K, V> dataCache) {
        return localCache.remove(key);
    }

    @Override
    public Collection<V> removeBefore(ZonedDateTime zonedDateTime, BaseCache<K, V> dataCache) {
        Set<K> keysToRemove = new HashSet<>();
        List<V> valuesRemoved = new ArrayList<>();
        localCache.forEach((k, v) -> {
            if (v.getCreatedDate().isBefore(zonedDateTime)) {
                keysToRemove.add(k);
            }
        });
        keysToRemove.forEach(k -> valuesRemoved.add(this.remove(k, dataCache)));
        return valuesRemoved;
    }

    public void forEach(BiConsumer<K,V> action) {
        localCache.forEach(action);
    }

    public void putIfAbsent(K key, V value) {
        localCache.putIfAbsent(key, value);
    }

    protected Map<K, V> onLoad(File saveFile, Class<K> keyType) {
        Map<K, V> map = new HashMap<>();
        YamlConfiguration yamlConfiguration = FileAdapterUtils.loadYaml(this, saveFile);
        yamlConfiguration.getValues(false).forEach((key1, value1) -> {
            K key = Serialization.deserialize(keyType, key1);
            V value = (V) value1;
            map.put(key, value);
        });
        return map;
    }

    protected void onSave(File saveFile, Set<Map.Entry<K, V>> entries) {
        YamlConfiguration yamlConfiguration = new YamlConfiguration();
        entries.forEach(entry -> {
            String key = Serialization.serialize(entry.getKey());
            yamlConfiguration.set(key, entry.getValue());
        });
        try {
            yamlConfiguration.save(saveFile);
        } catch (IOException e) {
            log(Level.SEVERE, "Failed to save: {0}", saveFile.getName());
        }
    }
}
