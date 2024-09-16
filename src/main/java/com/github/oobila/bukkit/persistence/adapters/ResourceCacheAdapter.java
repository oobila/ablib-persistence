package com.github.oobila.bukkit.persistence.adapters;

import com.github.oobila.bukkit.persistence.caches.BaseCache;
import com.github.oobila.bukkit.persistence.model.Resource;
import com.github.oobila.bukkit.persistence.model.ResourcePack;

import java.util.List;

public interface ResourceCacheAdapter<K> extends CacheReader {

    void open(BaseCache<K, ResourcePack> cache);

    int size(BaseCache<K, ResourcePack> cache);

}
