package com.github.oobila.bukkit.persistence.caches.real;

import com.github.oobila.bukkit.persistence.adapters.vehicle.PersistenceVehicle;
import com.github.oobila.bukkit.persistence.caches.async.AsyncWriteCache;
import com.github.oobila.bukkit.persistence.model.CacheItem;
import com.github.oobila.bukkit.persistence.model.OnDemandCacheItem;
import com.github.oobila.bukkit.persistence.model.SqlConnectionProperties;
import lombok.Getter;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

public class CombiCache<K, V> implements AsyncWriteCache<K, V, CacheItem<K, V>> {

    @Getter
    private final Plugin plugin;
    private final SimpleFileCache<K, V> fileCache;
    private final SimpleSqlCache<K, V> sqlCache;
    private final boolean primaryIsSql;

    public CombiCache(Plugin plugin, Class<K> keyType, Class<V> valueType, String pathString, String tableName,
                      SqlConnectionProperties sqlConnectionProperties, boolean primaryIsSql) {
        this.plugin = plugin;
        this.primaryIsSql = primaryIsSql;
        fileCache = new SimpleFileCache<>(pathString, keyType, valueType);
        if (sqlConnectionProperties == null) {
            sqlCache = null;
        } else {
            sqlCache = new SimpleSqlCache<>(plugin.getName(), tableName, keyType, valueType, sqlConnectionProperties);
        }
    }

    private void transfer() {
        if (primaryIsSql) {
            List<CacheItem<K, V>> values = new ArrayList<>(fileCache.values());
            values.forEach(cacheItem ->
                sqlCache.putValue(cacheItem.getKey(), cacheItem.getData(), onDemandCacheItem ->
                    fileCache.remove(cacheItem.getKey())
                )
            );
            fileCache.save();
        } else if(sqlCache != null) {
            //do nothing for now, sql functions for getting keys or values is not there
//            List<CacheItem<K, V>> values = new ArrayList<>(sqlCache.values());
//            values.forEach(onDemandCacheItem -> {
//                fileCache.putValue(onDemandCacheItem.getKey(), onDemandCacheItem.getData());
//                sqlCache.remove(onDemandCacheItem.getKey(), a -> {});
//            });
        }
    }

    private void transfer(UUID partition) {
        if (primaryIsSql) {
            List<CacheItem<K, V>> values = new ArrayList<>(fileCache.values(partition));
            values.forEach(cacheItem ->
                    sqlCache.putValue(partition, cacheItem.getKey(), cacheItem.getData(), onDemandCacheItem ->
                            fileCache.remove(partition, cacheItem.getKey())
                    )
            );
            fileCache.save(partition);
        } else if(sqlCache != null) {
            //do nothing for now, sql functions for getting keys or values is not there
//            List<CacheItem<K, V>> values = new ArrayList<>(sqlCache.values(partition));
//            values.forEach(onDemandCacheItem -> {
//                fileCache.putValue(partition, onDemandCacheItem.getKey(), onDemandCacheItem.getData());
//                sqlCache.remove(partition, onDemandCacheItem.getKey(), a -> {});
//            });
        }
    }

    @Override
    public PersistenceVehicle<K, V, CacheItem<K, V>> getWriteVehicle() {
        return null;
    }

    @Override
    public List<PersistenceVehicle<K, V, CacheItem<K, V>>> getReadVehicles() {
        return null;
    }

    @Override
    public void load(Plugin plugin) {
        fileCache.load(plugin);
        if (sqlCache != null) {
            sqlCache.load(plugin);
        }
        transfer();
    }

    @Override
    public void load(UUID partition) {
        fileCache.load(partition);
        if (sqlCache != null) {
            sqlCache.load(partition);
        }
        transfer(partition);
    }

    @Override
    public boolean isLoaded(UUID partition) {
        if (primaryIsSql) {
            return sqlCache.isLoaded(partition);
        } else {
            return fileCache.isLoaded(partition);
        }
    }

    @Override
    public void unload() {
        fileCache.unload();
        if (sqlCache != null) {
            sqlCache.unload();
        }
    }

    @Override
    public void unload(UUID partition) {
        fileCache.unload(partition);
        if (sqlCache != null) {
            sqlCache.unload(partition);
        }
    }

    @Override
    public void save() {
        fileCache.save();
        if (sqlCache != null) {
            sqlCache.save();
        }
    }

    @Override
    public void save(UUID partition) {
        fileCache.save(partition);
        if (sqlCache != null) {
            sqlCache.save(partition);
        }
    }

    @Override
    public void getValue(K key, @NotNull Consumer<V> consumer) {
        if (primaryIsSql) {
            sqlCache.getValue(key, consumer);
        } else {
            consumer.accept(fileCache.getValue(key));
        }
    }

    @Override
    public void getValue(UUID partition, K key, @NotNull Consumer<V> consumer) {
        if (primaryIsSql) {
            sqlCache.getValue(partition, key, consumer);
        } else {
            consumer.accept(fileCache.getValue(partition, key));
        }
    }

    @Override
    public CacheItem<K, V> get(UUID partition, K key) {
        if (primaryIsSql) {
            return sqlCache.get(partition, key);
        } else {
            return fileCache.get(partition, key);
        }
    }

    @Override
    public Collection<CacheItem<K, V>> values() {
        if (primaryIsSql) {
            Collection<OnDemandCacheItem<K, V>> onDemandCacheItems = sqlCache.values();
            return onDemandCacheItems.stream().map(o ->
                new CacheItem<>(o.getType(), o.getKey(), o.getData(), o.getSize(), o.getUpdatedDate())
            ).toList();
        } else {
            return fileCache.values();
        }
    }

    @Override
    public Collection<CacheItem<K, V>> values(UUID partition) {
        if (primaryIsSql) {
            Collection<OnDemandCacheItem<K, V>> onDemandCacheItems = sqlCache.values(partition);
            return onDemandCacheItems.stream().map(this::toCacheItem).toList();
        } else {
            return fileCache.values(partition);
        }
    }

    @Override
    public Collection<K> keySet() {
        if (primaryIsSql) {
            return sqlCache.keySet();
        } else {
            return fileCache.keySet();
        }
    }

    @Override
    public Collection<K> keySet(UUID partition) {
        if (primaryIsSql) {
            return sqlCache.keySet(partition);
        } else {
            return fileCache.keySet(partition);
        }
    }

    @Override
    public void putValue(K key, V value, @NotNull Consumer<CacheItem<K, V>> consumer) {
        if (primaryIsSql) {
            sqlCache.putValue(key, value, onDemandCacheItem ->
                consumer.accept(toCacheItem(onDemandCacheItem))
            );
        } else {
            consumer.accept(fileCache.putValue(key, value));
        }
    }

    @Override
    public void putValue(UUID partition, K key, V value, @NotNull Consumer<CacheItem<K, V>> consumer) {
        if (primaryIsSql) {
            sqlCache.putValue(partition, key, value, onDemandCacheItem ->
                    consumer.accept(toCacheItem(onDemandCacheItem))
            );
        } else {
            consumer.accept(fileCache.putValue(partition, key, value));
        }
    }

    @Override
    public void remove(K key, @NotNull Consumer<CacheItem<K, V>> consumer) {
        if (primaryIsSql) {
            sqlCache.remove(key, onDemandCacheItem ->
                    consumer.accept(toCacheItem(onDemandCacheItem))
            );
        } else {
            consumer.accept(fileCache.remove(key));
        }
    }

    @Override
    public void remove(UUID partition, K key, @NotNull Consumer<CacheItem<K, V>> consumer) {
        if (primaryIsSql) {
            sqlCache.remove(partition, key, onDemandCacheItem ->
                    consumer.accept(toCacheItem(onDemandCacheItem))
            );
        } else {
            consumer.accept(fileCache.remove(partition, key));
        }
    }

    @Override
    public void clear(UUID partition, @NotNull Consumer<List<CacheItem<K, V>>> consumer) {
        if (primaryIsSql) {
            sqlCache.clear(partition, list ->
                    consumer.accept(toCacheItem(list))
            );
        } else {
            consumer.accept(fileCache.clear(partition));
        }
    }

    @Override
    public void removeBefore(ZonedDateTime zonedDateTime, @NotNull Consumer<List<CacheItem<K, V>>> consumer) {
        if (primaryIsSql) {
            //unsupported
        } else {
            consumer.accept(fileCache.removeBefore(zonedDateTime));
        }
    }

    private CacheItem<K, V> toCacheItem(OnDemandCacheItem<K, V> o) {
        return new CacheItem<>(o.getType(), o.getKey(), o.getData(), o.getSize(), o.getUpdatedDate());
    }

    private List<CacheItem<K, V>> toCacheItem(List<OnDemandCacheItem<K, V>> l) {
        return l.stream().map(this::toCacheItem).toList();
    }
}
