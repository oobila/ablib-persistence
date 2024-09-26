package com.github.oobila.bukkit.persistence.observers;

import com.github.oobila.bukkit.persistence.model.CacheItem;

import java.util.Map;
import java.util.UUID;

public interface PlayerUnloadObserver<K, V, C extends CacheItem<K, V>> extends PlayerObserver<K, V, C> {

    void onUnload(UUID playerId, Map<K, C> unloadedData);

}
