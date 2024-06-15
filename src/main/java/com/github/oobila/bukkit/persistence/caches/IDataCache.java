package com.github.oobila.bukkit.persistence.caches;

import com.github.oobila.bukkit.persistence.model.PersistedObject;

import java.time.ZonedDateTime;

public interface IDataCache<K, V extends PersistedObject> extends ICache {

    void put(K key, V value);
    V get(K key);
    V remove(K key);
    int removeBefore(ZonedDateTime zonedDateTime);

}
