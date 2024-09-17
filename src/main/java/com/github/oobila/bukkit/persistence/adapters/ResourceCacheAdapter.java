package com.github.oobila.bukkit.persistence.adapters;

import com.github.oobila.bukkit.persistence.adapters.zip.ZipEntryAdapter;
import com.github.oobila.bukkit.persistence.caches.BaseCache;
import com.github.oobila.bukkit.persistence.model.PersistedObject;
import com.github.oobila.bukkit.persistence.model.Resource;
import com.github.oobila.bukkit.persistence.model.ResourcePack;

import java.util.List;
import java.util.Map;

public interface ResourceCacheAdapter<K> extends CacheReader {

    void open(
            BaseCache<K, ResourcePack> cache,
            Map<Class<? extends PersistedObject>, ZipEntryAdapter<? extends PersistedObject>> zipEntryAdapterMap
    );

    void loadData(CacheReader cacheReader, Resource resource);

    int size(BaseCache<K, ResourcePack> cache);

    void put(K key, ResourcePack value);

    ResourcePack get(K key);

    List<ResourcePack> get();

}
