package com.github.oobila.bukkit.persistence.caches.multi;

import com.github.oobila.bukkit.persistence.caches.BaseCache;
import com.github.oobila.bukkit.persistence.caches.ICache;
import com.github.oobila.bukkit.persistence.model.PersistedObject;
import lombok.Getter;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.List;

import static com.github.oobila.bukkit.persistence.Constants.DATA;

public abstract class MultiCacheBase<K, V extends PersistedObject, C extends BaseCache<K, V>> extends BaseCache<K, V> implements ICache {

    @Getter
    private final String subFolderName;
    protected final List<C> cacheReadInstances = new ArrayList<>();
    protected final C cacheWriteInstance;
    protected final boolean canReadFromWriter;

    protected MultiCacheBase(String name, Class<K> keyType, Class<V> valueType, C writer) {
        this(name, keyType, valueType, writer, DATA);
    }

    protected MultiCacheBase(String name, Class<K> keyType, Class<V> valueType, C writer, String subFolderName) {
        super(name, keyType, valueType);
        this.subFolderName = subFolderName;
        this.cacheWriteInstance = writer;
        this.canReadFromWriter = true;
    }

    protected MultiCacheBase(
            String name,
            Class<K> keyType, Class<V> valueType,
            C writer,
            String subFolderName,
            boolean canReadFromWriter
    ) {
        super(name, keyType, valueType);
        this.subFolderName = subFolderName;
        this.cacheWriteInstance = writer;
        this.canReadFromWriter = canReadFromWriter;
    }

    public void addReadInstance(C readInstance) {
        cacheReadInstances.add(readInstance);
    }

    @Override
    public void onOpen(Plugin plugin) {
        cacheWriteInstance.open(plugin);
        cacheReadInstances.forEach(cache -> cache.open(plugin));
    }

    @Override
    public void onClose() {
        cacheReadInstances.forEach(ICache::close);
        cacheWriteInstance.close();
    }

}
