package com.github.oobila.bukkit.persistence.old.vehicles.temp;

import com.github.oobila.bukkit.persistence.old.adapters.code.CodeAdapter;
import com.github.oobila.bukkit.persistence.old.adapters.storage.StorageAdapter;
import com.github.oobila.bukkit.persistence.old.caches.Cache;
import com.github.oobila.bukkit.persistence.old.model.BackwardsCompatibility;
import com.github.oobila.bukkit.persistence.old.model.CacheItem;

import java.util.List;

public interface PersistenceVehicle<K, V, C extends CacheItem<K, V>> {

    PersistenceVehicle<K, V, C> register(Cache<K, V, C> cache);

    Cache<K, V, C> getCache();

    Class<K> getKeyType();

    StorageAdapter getStorageAdapter();

    CodeAdapter<V> getCodeAdapter();

    void addBackwardsCompatibility(BackwardsCompatibility backwardsCompatibility);

    List<BackwardsCompatibility> getBackwardsCompatibilityList();

}
