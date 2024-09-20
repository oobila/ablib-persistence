package com.github.oobila.bukkit.persistence.observers;

import com.github.oobila.bukkit.persistence.model.CacheItem;

import java.util.Map;
import java.util.UUID;

public interface PlayerSaveObserver<K, V> extends PlayerObserver<K, V> {

    void onSave(UUID playerId, Map<K, CacheItem<K, V>> savedData);

}
