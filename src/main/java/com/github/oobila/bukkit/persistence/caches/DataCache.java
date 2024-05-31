package com.github.oobila.bukkit.persistence.caches;

import com.github.oobila.bukkit.persistence.adapters.DataCacheAdapter;
import com.github.oobila.bukkit.persistence.adapters.DataFileAdapter;
import com.github.oobila.bukkit.persistence.model.PersistedObject;
import com.github.oobila.bukkit.persistence.observers.DataCacheObserver;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.logging.Level;

public class DataCache<K, V extends PersistedObject> extends BaseCache<K, V>{

    private static final String SUB_FOLDER_NAME = "data";

    @Setter
    private DataCacheAdapter<K,V> adapter;
    private final List<DataCacheObserver<K, V>> observers = new ArrayList<>();

    private final String subFolderName;

    public DataCache(String name, Class<K> keyType, Class<V> type) {
        this(name, keyType, type, new DataFileAdapter<>());
    }

    public DataCache(String name, Class<K> keyType, Class<V> type, String subFolderName) {
        this(name, keyType, type, new DataFileAdapter<>(), subFolderName);
    }

    public DataCache(String name, Class<K> keyType, Class<V> type, DataCacheAdapter<K,V> adapter) {
        this(name, keyType, type, adapter, SUB_FOLDER_NAME);
    }

    public DataCache(String name, Class<K> keyType, Class<V> type, DataCacheAdapter<K,V> adapter, String subFolderName) {
        super(name, keyType, type);
        this.adapter = adapter;
        this.subFolderName = subFolderName;
    }

    public void open(Plugin plugin) {
        this.plugin = plugin;
        adapter.open(this);
        observers.forEach(observer -> observer.onOpen(this));
    }

    public void close(){
        observers.forEach(observer -> observer.onClose(this));
        adapter.close(this);
    }

    public void put(K key, V value) {
        adapter.put(key, value, this);
        observers.forEach(observer -> observer.onPut(key, value, this));
    }

    public V get(K key) {
        return adapter.get(key, this);
    }

    public V remove(K key) {
        V value = adapter.remove(key, this);
        observers.forEach(observer -> observer.onRemove(key, value, this));
        return value;
    }

    public int removeBefore(ZonedDateTime zonedDateTime) {
        return adapter.removeBefore(zonedDateTime, this);
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

    @Override
    public String getSubFolderName() {
        return subFolderName == null ? SUB_FOLDER_NAME : subFolderName;
    }

    public int size(){
        return adapter.size(this);
    }

    public void addObserver(DataCacheObserver<K, V> observer) {
        observers.add(observer);
    }

}