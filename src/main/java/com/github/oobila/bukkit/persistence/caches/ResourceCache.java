package com.github.oobila.bukkit.persistence.caches;

import com.github.oobila.bukkit.persistence.adapters.ResourceCacheAdapter;
import com.github.oobila.bukkit.persistence.adapters.ResourceFileAdapter;
import com.github.oobila.bukkit.persistence.model.ResourcePack;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.plugin.Plugin;

import static com.github.oobila.bukkit.common.ABCommon.log;
import static com.github.oobila.bukkit.persistence.Constants.DATA;

public class ResourceCache<K, V extends ResourcePack> extends BaseCache<K, V> {

    @Setter
    private ResourceCacheAdapter<K,V> adapter;
    @Getter
    private final String subFolderName;

    public ResourceCache(String name, Class<K> keyType, Class<V> type) {
        this(name, keyType, type, new ResourceFileAdapter<>());
    }

    public ResourceCache(String name, Class<K> keyType, Class<V> type, String subFolderName) {
        this(name, keyType, type, new ResourceFileAdapter<>(), subFolderName);
    }

    public ResourceCache(String name, Class<K> keyType, Class<V> type, ResourceCacheAdapter<K,V> adapter) {
        this(name, keyType, type, adapter, DATA);
    }

    public ResourceCache(String name, Class<K> keyType, Class<V> type, ResourceCacheAdapter<K,V> adapter, String subFolderName) {
        super(name, keyType, type);
        this.adapter = adapter;
        this.subFolderName = subFolderName;
    }

    @Override
    public void onOpen(Plugin plugin) {
        this.plugin = plugin;
        adapter.open(this);
    }

    @Override
    public void onClose() {
        adapter.close(this);
    }
}
