package com.github.oobila.bukkit.persistence.caches.standard;

import com.github.oobila.bukkit.persistence.adapters.vehicle.PlayerPersistenceVehicle;
import com.github.oobila.bukkit.persistence.model.CacheItem;
import com.github.oobila.bukkit.persistence.observers.PlayerSaveObserver;
import lombok.Getter;
import org.bukkit.plugin.Plugin;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@SuppressWarnings("unused")
@Getter
public class PlayerReadAndWriteCache<K, V> extends PlayerReadOnlyCache<K, V> implements StandardPlayerWriteCache<K, V> {

    private final PlayerPersistenceVehicle<K, V> writeVehicle;

    public PlayerReadAndWriteCache(Plugin plugin, String name, PlayerPersistenceVehicle<K, V> vehicle) {
        super(plugin, name, vehicle);
        this.writeVehicle = vehicle;
    }

    public PlayerReadAndWriteCache(Plugin plugin, String name, List<PlayerPersistenceVehicle<K, V>> readVehicles,
                                   PlayerPersistenceVehicle<K, V> writeVehicle) {
        super(plugin, name, readVehicles);
        this.writeVehicle = writeVehicle;
    }

    @Override
    public void save() {
        writeVehicle.save(getPlugin(), getName(), localCache);
    }

    @Override
    public void savePlayer(UUID id) {
        writeVehicle.savePlayer(getPlugin(), getName(), id, localCache.get(id));
        playerObservers.forEach(observer -> {
            if (observer instanceof PlayerSaveObserver<K,V> playerSaveObserver) {
                playerSaveObserver.onSave(id, localCache.get(id));
            }
        });
    }

    @Override
    public CacheItem<K, V> put(UUID id, K key, V value) {
        localCache.putIfAbsent(id, new HashMap<>());
        return localCache.get(id).put(key, new CacheItem<>(key, value, 0, ZonedDateTime.now()));
    }

    @Override
    public CacheItem<K, V> remove(UUID id, K key) {
        Map<K, CacheItem<K,V>> innerMap = localCache.get(id);
        if (innerMap != null) {
            return innerMap.remove(key);
        }
        return null;
    }

    @Override
    public List<CacheItem<K,V>> removeBefore(ZonedDateTime zonedDateTime) {
        List<K> itemsToRemove = new ArrayList<>();
        List<CacheItem<K,V>> itemsRemoved = new ArrayList<>();
        localCache.forEach((uuid, innerMap) -> {
            innerMap.forEach((key, cacheItem) -> {
                if (cacheItem.getUpdatedDate().isBefore(zonedDateTime)) {
                    itemsToRemove.add(key);
                }
            });
            itemsToRemove.forEach(key -> itemsRemoved.add(innerMap.remove(key)));
        });
        return itemsRemoved;
    }
}
