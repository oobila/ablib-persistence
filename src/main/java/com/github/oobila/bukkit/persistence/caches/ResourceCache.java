package com.github.oobila.bukkit.persistence.caches;

import com.github.oobila.bukkit.persistence.adapters.ResourceCacheAdapter;
import com.github.oobila.bukkit.persistence.adapters.ResourceFileAdapter;
import com.github.oobila.bukkit.persistence.model.ResourcePack;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.plugin.Plugin;

import static com.github.oobila.bukkit.common.ABCommon.log;
import static com.github.oobila.bukkit.persistence.Constants.DATA;

public class ResourceCache<K> extends BaseCache<K, ResourcePack> {

    @Setter
    private ResourceCacheAdapter<K> adapter;
    @Getter
    private final String subFolderName;

    public ResourceCache(String name, Class<K> keyType) {
        this(name, keyType, new ResourceFileAdapter<>(keyType));
    }

    public ResourceCache(String name, Class<K> keyType, String subFolderName) {
        this(name, keyType, new ResourceFileAdapter<>(keyType), subFolderName);
    }

    public ResourceCache(String name, Class<K> keyType, ResourceCacheAdapter<K> adapter) {
        this(name, keyType, adapter, DATA);
    }

    public ResourceCache(String name, Class<K> keyType, ResourceCacheAdapter<K> adapter, String subFolderName) {
        super(name, keyType, ResourcePack.class);
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
        //no close action necessary
    }
}
