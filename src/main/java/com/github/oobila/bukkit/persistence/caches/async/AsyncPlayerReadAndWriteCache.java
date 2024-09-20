package com.github.oobila.bukkit.persistence.caches.async;

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
import java.util.function.Consumer;

import static com.github.oobila.bukkit.common.ABCommon.runTaskAsync;

@SuppressWarnings("unused")
@Getter
public class AsyncPlayerReadAndWriteCache<K, V> extends AsyncPlayerReadOnlyCache<K, V> implements AsyncPlayerWriteCache<K, V> {

    private final PlayerPersistenceVehicle<K, V> writeVehicle;

    public AsyncPlayerReadAndWriteCache(Plugin plugin, String name, PlayerPersistenceVehicle<K, V> vehicle) {
        super(plugin, name, vehicle);
        this.writeVehicle = vehicle;
    }

    public AsyncPlayerReadAndWriteCache(Plugin plugin, String name, List<PlayerPersistenceVehicle<K, V>> readVehicles,
                                        PlayerPersistenceVehicle<K, V> writeVehicle) {
        super(plugin, name, readVehicles);
        this.writeVehicle = writeVehicle;
    }

    @Override
    public void save() {
        writeVehicle.save(getPlugin(), getName(), localCache);
    }

    @Override
    public void savePlayer(UUID id, Consumer<Map<K, CacheItem<K, V>>> consumer) {
        runTaskAsync(() -> {
            writeVehicle.savePlayer(getPlugin(), getName(), id, localCache.get(id));
            if (consumer != null) {
                getWithMetaData(id, consumer);
            }
            runTaskAsync(() ->
                    playerObservers.forEach(observer -> {
                        if (observer instanceof PlayerSaveObserver<K,V> playerSaveObserver) {
                            playerSaveObserver.onSave(id, localCache.get(id));
                        }
                    }
            ));
        });
    }

    @Override
    public void put(UUID id, K key, V value, Consumer<CacheItem<K, V>> consumer) {
        runTaskAsync(() -> {
            localCache.putIfAbsent(id, new HashMap<>());
            CacheItem<K, V> cacheItem = localCache.get(id).put(key, new CacheItem<>(key, value, 0, ZonedDateTime.now()));
            consumer.accept(cacheItem);
        });
    }

    @Override
    public void remove(UUID id, K key, Consumer<CacheItem<K, V>> consumer) {
        runTaskAsync(() -> {
            Map<K, CacheItem<K,V>> innerMap = localCache.get(id);
            if (innerMap != null) {
                CacheItem<K, V> cacheItem = innerMap.remove(key);
                consumer.accept(cacheItem);
            } else {
                consumer.accept(null);
            }
        });
    }

    @Override
    public void removeBefore(ZonedDateTime zonedDateTime, Consumer<List<CacheItem<K, V>>> consumer) {
        runTaskAsync(() -> {
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
            consumer.accept(itemsRemoved);
        });
    }
}
