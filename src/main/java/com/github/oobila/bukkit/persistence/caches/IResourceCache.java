package com.github.oobila.bukkit.persistence.caches;

import com.github.oobila.bukkit.persistence.model.PersistedObject;
import com.github.oobila.bukkit.persistence.model.ResourcePack;

import java.util.List;

public interface IResourceCache<K> extends ICache {

    void put(K key, ResourcePack resourcePack);
    ResourcePack get(K key);
    List<ResourcePack> get();

}
