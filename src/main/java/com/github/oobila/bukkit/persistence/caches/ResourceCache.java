package com.github.oobila.bukkit.persistence.caches;

import com.github.oobila.bukkit.persistence.adapters.ResourceCacheAdapter;
import com.github.oobila.bukkit.persistence.adapters.ResourceFileAdapter;
import com.github.oobila.bukkit.persistence.adapters.zip.SchematicZipAdapter;
import com.github.oobila.bukkit.persistence.adapters.zip.ZipEntryAdapter;
import com.github.oobila.bukkit.persistence.model.PersistedObject;
import com.github.oobila.bukkit.persistence.model.ResourcePack;
import com.github.oobila.bukkit.persistence.model.SchematicObject;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.plugin.Plugin;

import java.util.HashMap;
import java.util.Map;

import static com.github.oobila.bukkit.persistence.Constants.DATA;

public class ResourceCache<K> extends BaseCache<K, ResourcePack> {

    @Setter
    private ResourceCacheAdapter<K> adapter;
    private Map<Class<? extends PersistedObject>, ZipEntryAdapter<? extends PersistedObject>> zipEntryAdapters = new HashMap<>();
    @Getter
    private final String subFolderName;

    public ResourceCache(String name, Class<K> keyType, ResourceCacheAdapter<K> adapter) {
        this(name, keyType, adapter, DATA);
    }

    public ResourceCache(String name, Class<K> keyType, ResourceCacheAdapter<K> adapter, String subFolderName) {
        super(name, keyType, ResourcePack.class);
        this.adapter = adapter;
        this.subFolderName = subFolderName;
        zipEntryAdapters.put(SchematicObject.class, new SchematicZipAdapter());
    }

    public <V extends PersistedObject> void addZipEntryAdapter(Class<V> vClass, ZipEntryAdapter<V> zipEntryAdapter) {
        zipEntryAdapters.put(vClass, zipEntryAdapter);
    }

    @Override
    public void onOpen(Plugin plugin) {
        this.plugin = plugin;
        adapter.open(this, zipEntryAdapters);
    }

    @Override
    public void onClose() {
        //no close action necessary
    }
}
