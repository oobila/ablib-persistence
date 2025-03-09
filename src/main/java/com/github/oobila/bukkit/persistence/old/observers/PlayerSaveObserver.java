package com.github.oobila.bukkit.persistence.old.observers;

import com.github.oobila.bukkit.persistence.old.model.CacheItem;

import java.util.Map;
import java.util.UUID;

public interface PlayerSaveObserver<K, V, C extends CacheItem<K, V>> extends PlayerObserver<K, V, C> {

    void onSave(UUID playerId, Map<K, C> savedData);

}
