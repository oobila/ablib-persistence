package com.github.oobila.bukkit.persistence.model;

import com.github.oobila.bukkit.persistence.adapters.storage.StoredData;
import com.github.oobila.bukkit.persistence.caches.standard.NoMemoryClusterCache;
import com.github.oobila.bukkit.persistence.serializers.Serialization;
import lombok.Getter;
import org.apache.commons.io.FilenameUtils;

import java.time.ZonedDateTime;

@Getter
public class NoMemoryCacheItem<K, D> extends CacheItem<K, D> {

    private final NoMemoryClusterCache<K, D> cache;

    public NoMemoryCacheItem(K key, D data, StoredData storedData, NoMemoryClusterCache<K, D> cache) {
        super(key, data, storedData);
        this.cache = cache;
    }

    public NoMemoryCacheItem(K key, D data, long size, ZonedDateTime updatedDate, NoMemoryClusterCache<K, D> cache) {
        super(key, data, size, updatedDate);
        this.cache = cache;
    }

    @Override
    public D getData() {
        if (data == null) {
            CacheItem<K, D> cacheItem = cache.getVehicle().loadSingle(
                    cache.getPlugin(),
                    cache.getName(),
                    FilenameUtils.getBaseName(Serialization.serialize(getKey()))
            );
            this.data = cacheItem.data;
        }
        return data;
    }

    public void unload() {
        this.data = null;
    }
}

