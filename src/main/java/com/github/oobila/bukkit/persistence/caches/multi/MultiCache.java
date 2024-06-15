package com.github.oobila.bukkit.persistence.caches.multi;

import com.github.oobila.bukkit.persistence.caches.DataCache;
import com.github.oobila.bukkit.persistence.caches.IDataCache;
import com.github.oobila.bukkit.persistence.model.PersistedObject;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

import static com.github.oobila.bukkit.persistence.Constants.DATA;

public class MultiCache<K, V extends PersistedObject> extends MultiCacheBase<K, V, DataCache<K, V>> implements IDataCache<K, V> {

    public MultiCache(String name, Class<K> keyType, Class<V> valueType, DataCache<K, V> cacheWriteInstance) {
        this(name, keyType, valueType, cacheWriteInstance, DATA);
    }

    public MultiCache(String name, Class<K> keyType, Class<V> valueType, DataCache<K, V> cacheWriteInstance, String subFolderName) {
        super(name, keyType, valueType, cacheWriteInstance, subFolderName);
    }

    @Override
    public void put(K key, V value) {
        cacheWriteInstance.put(key, value);
    }

    @Override
    public V get(K key) {
        V v = null;
        if (canReadFromWriter) {
            v = cacheWriteInstance.get(key);
            if (v != null) {
                return v;
            }
        }
        for (IDataCache<K, V> cacheReader : cacheReadInstances) {
            v = cacheReader.get(key);
            if (v != null) {
                return v;
            }
        }
        return v;
    }

    @Override
    public List<V> get() {
        List<V> list = new ArrayList<>();
        if (canReadFromWriter) {
            list.addAll(cacheWriteInstance.get());
        }
        for (IDataCache<K, V> cacheReader : cacheReadInstances) {
            list.addAll(cacheReader.get());
        }
        return list;
    }

    @Override
    public V remove(K key) {
        return cacheWriteInstance.remove(key);
    }

    @Override
    public int removeBefore(ZonedDateTime zonedDateTime) {
        return cacheWriteInstance.removeBefore(zonedDateTime);
    }
}
