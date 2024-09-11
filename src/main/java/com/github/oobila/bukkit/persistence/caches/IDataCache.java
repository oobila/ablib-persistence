package com.github.oobila.bukkit.persistence.caches;

import com.github.oobila.bukkit.persistence.model.PersistedObject;

import java.time.ZonedDateTime;
import java.util.List;

public interface IDataCache<K, V extends PersistedObject> extends ICache {

    boolean contains(K key);
    void put(K key, V value);
    V get(K key);
    List<V> get();
    V remove(K key);
    int removeBefore(ZonedDateTime zonedDateTime);

}
