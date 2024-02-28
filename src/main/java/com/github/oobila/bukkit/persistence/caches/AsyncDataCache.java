package com.github.oobila.bukkit.persistence.caches;

import com.github.oobila.bukkit.persistence.adapters.DataCacheAdapter;
import com.github.oobila.bukkit.persistence.adapters.DataFileAdapter;
import com.github.oobila.bukkit.persistence.model.PersistedObject;
import com.github.oobila.bukkit.persistence.model.SqlConnectionProperties;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import java.time.ZonedDateTime;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.IntConsumer;
import java.util.logging.Level;

public class AsyncDataCache<K, V extends PersistedObject> extends BaseCache<K, V>{

    private static final String SUB_FOLDER_NAME = "data";

    @Getter
    @Setter
    private SqlConnectionProperties sqlConnectionProperties;

    @Setter
    private DataCacheAdapter<K,V> adapter;

    public AsyncDataCache(String name, Class<K> keyType, Class<V> type) {
        this(name, keyType, type, new DataFileAdapter<>());
    }

    public AsyncDataCache(String name, Class<K> keyType, Class<V> type, DataCacheAdapter<K,V> adapter) {
        super(name, keyType, type);
        this.adapter = adapter;
    }

    @Override
    public String getSubFolderName() {
        return SUB_FOLDER_NAME;
    }

    public void open(Plugin plugin) {
        this.plugin = plugin;
        adapter.open(this);
    }

    public void close(){
        adapter.close(this);
    }

    public void put(K key, V value, Runnable runnable) {
        Bukkit.getScheduler().runTaskAsynchronously(getPlugin(), () -> {
            adapter.put(key, value, this);
            if (runnable != null) {
                runnable.run();
            }
        });
    }

    public void get(K key, Consumer<V> consumer) {
        Bukkit.getScheduler().runTaskAsynchronously(getPlugin(), () -> {
            V v = adapter.get(key, this);
            if (consumer != null) {
                consumer.accept(v);
            }
        });
    }

    public void remove(K key, Runnable runnable) {
        Bukkit.getScheduler().runTaskAsynchronously(getPlugin(), () -> {
            adapter.remove(key, this);
            if (runnable != null) {
                runnable.run();
            }
        });
    }

    public void removeBefore(ZonedDateTime zonedDateTime, IntConsumer consumer) {
        Bukkit.getScheduler().runTaskAsynchronously(getPlugin(), () -> {
            int i = adapter.removeBefore(zonedDateTime, this);
            if (consumer != null) {
                consumer.accept(i);
            }
        });
    }

    public void forEach(BiConsumer<K, V> action) {
        if (adapter instanceof DataFileAdapter<K,V> dataFileAdapter) {
            dataFileAdapter.forEach(action);
        } else {
            Bukkit.getLogger().log(Level.WARNING, "Method only allowed when reading from file");
        }
    }

    public void putIfAbsent(K key, V value) {
        if (adapter instanceof DataFileAdapter<K,V> dataFileAdapter) {
            dataFileAdapter.putIfAbsent(key, value);
        } else {
            Bukkit.getLogger().log(Level.WARNING, "Method only allowed when reading from file");
        }
    }
}