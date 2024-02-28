package com.github.oobila.bukkit.persistence.adapters;

import com.github.oobila.bukkit.persistence.adapters.utils.FileAdapterUtils;
import com.github.oobila.bukkit.persistence.caches.BaseCache;
import com.github.oobila.bukkit.persistence.model.PersistedObject;
import com.github.oobila.bukkit.persistence.serializers.Serialization;
import org.apache.commons.io.FilenameUtils;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.*;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.function.BiConsumer;

public class DataFileAdapter<K, V extends PersistedObject> implements DataCacheAdapter<K, V> {

    private final Map<K, V> localCache = new HashMap<>();
    private final boolean isCluster;

    public DataFileAdapter() {
        this.isCluster = false;
    }

    public DataFileAdapter(boolean isCluster) {
        this.isCluster = isCluster;
    }

    @Override
    public void open(BaseCache<K, V> cache) {
        File saveFile = FileAdapterUtils.getSaveFile(cache, null);
        if (!saveFile.exists()) {
            Bukkit.getLogger().warning("savefile does not exist");
            FileAdapterUtils.copyDefaults(cache, saveFile);
        }
        if (isCluster) {
            Arrays.stream(saveFile.listFiles()).forEach(file -> {
                String fileName = FilenameUtils.removeExtension(file.getName());
                K key = Serialization.deserialize(cache.getKeyType(), fileName);
                localCache.put(key, onLoadCluster(file));
            });
        } else {
            localCache.putAll(onLoad(saveFile, cache.getKeyType()));
        }
    }

    @Override
    public void close(BaseCache<K, V> cache) {
        File saveFile = FileAdapterUtils.getSaveFile(cache, null);
        if (saveFile.exists()) {
            saveFile.delete();
        }
        if (isCluster) {
            localCache.forEach((k, v) -> {
                File clusterFile = new File(saveFile, Serialization.serialize(k) + ".yml");
                onSaveCluster(clusterFile, v);
            });
        } else {
            onSave(saveFile, localCache.entrySet());
        }
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
    public void remove(K key, BaseCache<K, V> dataCache) {
        localCache.remove(key);
    }

    @Override
    public int removeBefore(ZonedDateTime zonedDateTime, BaseCache<K, V> dataCache) {
        Set<K> keysToRemove = new HashSet<>();
        localCache.forEach((k, v) -> {
            if (v.getCreatedDate().isBefore(zonedDateTime)) {
                keysToRemove.add(k);
            }
        });
        keysToRemove.forEach(k -> this.remove(k, dataCache));
        return 0;
    }

    public void forEach(BiConsumer<K,V> action) {
        localCache.forEach(action);
    }

    public void putIfAbsent(K key, V value) {
        localCache.putIfAbsent(key, value);
    }

    protected Map<K, V> onLoad(File saveFile, Class<K> keyType) {
        Map<K, V> map = new HashMap<>();
        FileConfiguration fileConfiguration = YamlConfiguration.loadConfiguration(saveFile);
        fileConfiguration.getValues(false).entrySet().forEach(entry ->{
            K key = Serialization.deserialize(keyType, entry.getKey());
            V value = (V) entry.getValue();
            map.put(key, value);
        });
        return map;
    }

    protected void onSave(File saveFile, Set<Map.Entry<K, V>> entries) {
        FileConfiguration fileConfiguration = YamlConfiguration.loadConfiguration(saveFile);
        entries.forEach(entry -> {
            String key = Serialization.serialize(entry.getKey());
            fileConfiguration.set(key, entry.getValue());
        });
        try {
            fileConfiguration.save(saveFile);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    protected V onLoadCluster(File saveFile) {
        return FileAdapterUtils.loadConfiguration(saveFile);
    }

    protected void onSaveCluster(File saveFile, V value) {
        FileAdapterUtils.saveConfiguration(saveFile, value);
    }
}
