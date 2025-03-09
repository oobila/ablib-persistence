package com.github.oobila.bukkit.persistence.old.vehicles.pollmethod;

import com.github.oobila.bukkit.persistence.old.adapters.storage.StorageAdapter;
import com.github.oobila.bukkit.persistence.old.model.CacheItem;

import java.util.List;

public interface PollMethod<T> {

    List<String> getPaths(StorageAdapter storageAdapter, T object);
    String getPath(StorageAdapter storageAdapter, T object, CacheItem<?, ?> cacheItem);

}
