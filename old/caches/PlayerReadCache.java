package com.github.oobila.bukkit.persistence.old.caches;

import com.github.oobila.bukkit.persistence.old.model.CacheItem;
import com.github.oobila.bukkit.persistence.old.observers.PlayerObserver;

import java.util.UUID;

@SuppressWarnings("unused")
public interface PlayerReadCache<K, V, C extends CacheItem<K, V>> extends Cache<K, V, C> {

    void unloadPlayer(UUID id);

    void addPlayerObserver(PlayerObserver<K, V, C> playerObserver);

}
