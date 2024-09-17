package com.github.oobila.bukkit.persistence.adapters;

import com.github.oobila.bukkit.persistence.adapters.utils.FileAdapterUtils;
import com.github.oobila.bukkit.persistence.adapters.zip.ZipEntryAdapter;
import com.github.oobila.bukkit.persistence.caches.BaseCache;
import com.github.oobila.bukkit.persistence.model.PersistedObject;
import com.github.oobila.bukkit.persistence.model.Resource;
import com.github.oobila.bukkit.persistence.model.ResourcePack;
import com.github.oobila.bukkit.persistence.serializers.Serialization;
import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Level;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import static com.github.oobila.bukkit.common.ABCommon.log;

public class ResourceFileAdapter<K> implements ResourceCacheAdapter<K> {

    private final Map<K, ResourcePack> localCache = new HashMap<>();
    private Map<Class<? extends PersistedObject>, ZipEntryAdapter<? extends PersistedObject>> zipEntryAdapters;

    public ResourceFileAdapter(Map<Class<? extends PersistedObject>, ZipEntryAdapter<? extends PersistedObject>> zipEntryAdapters) {
        this.zipEntryAdapters = zipEntryAdapters;
    }

    @Override
    public void open(
            BaseCache<K, ResourcePack> cache,
            Map<Class<? extends PersistedObject>, ZipEntryAdapter<? extends PersistedObject>> zipEntryAdapters
    ) {
        File directory = FileAdapterUtils.getClusterLocation(cache, null);
        if (directory.exists()) {
            localCache.clear();
            localCache.putAll(onLoad(directory, cache.getKeyType()));
        }
    }

    @Override
    public int size(BaseCache<K, ResourcePack> cache) {
        return 0;
    }

    protected Map<K, ResourcePack> onLoad(File directory, Class<K> keyType) {
        Map<K, ResourcePack> map = new HashMap<>();
        if (directory != null) {
            for (File file : Objects.requireNonNull(directory.listFiles())) {
                String extension = FilenameUtils.getExtension(file.getName()).toLowerCase();
                if (extension.equals("zip")) {
                    String fileName = FilenameUtils.getBaseName(file.getName());
                    try {
                        K key = Serialization.deserialize(keyType, fileName);
                        ResourcePack resourcePack = new ResourcePack(file, this);
                        map.put(key, resourcePack);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                } else {
                    log(Level.WARNING, "non-zip file found in resources directory: {}", file.getAbsolutePath());
                }
            }
        }
        loadResourceMetaData(map);
        return map;
    }

    private void loadResourceMetaData(Map<K, ResourcePack> map) {
        map.forEach((k, resourcePack) -> {
            Map<String, Resource> resources = resourcePack.getResources();
            File file = resourcePack.getFile();
            try (ZipFile zip = new ZipFile(file)) {
                for (Enumeration<? extends ZipEntry> zipEntries = zip.entries(); zipEntries.hasMoreElements(); ) {
                    ZipEntry entry = zipEntries.nextElement();
                    try {
                        if (!entry.isDirectory()) {
                            //found file inside zip
                            String name = entry.getName();
                            long size = entry.getSize();
                            Resource resource = new Resource(resourcePack, name, size);
                            resources.put(name, resource);
                        }
                    } catch (Exception e) {
                        log(Level.SEVERE, "Failed to load resource: {0}", entry.getName());
                        log(Level.SEVERE, e);
                    }
                }
            } catch (IOException e) {
                log(Level.SEVERE, "Failed loading resource pack: {0}", resourcePack.getName());
                log(Level.SEVERE, e);
            }
        });
    }

    @Override
    public void loadData(Resource resource) {
        ResourcePack resourcePack = resource.getResourcePack();
        File file = resourcePack.getFile();
        try (ZipFile zip = new ZipFile(file)) {
            for (Enumeration<? extends ZipEntry> zipEntries = zip.entries(); zipEntries.hasMoreElements(); ) {
                ZipEntry entry = zipEntries.nextElement();
                try {
                    if (!entry.isDirectory() && resource.getName().equalsIgnoreCase(entry.getName())) {
                        //found file inside zip
                        ZipEntryAdapter<?> zipEntryAdapter = getZipEntryAdapter(resource);
                        resource.setData(zipEntryAdapter.getValue(entry, zip, this));
                    }
                } catch (Exception e) {
                    log(Level.SEVERE, "Failed to load resource: {0}", entry.getName());
                    log(Level.SEVERE, e);
                }
            }
        } catch (IOException e) {
            log(Level.SEVERE, "Failed loading resource pack: {0}", resourcePack.getName());
            log(Level.SEVERE, e);
        }
    }

    private ZipEntryAdapter<?> getZipEntryAdapter(Resource resource) {
        for (ZipEntryAdapter<?> zipEntryAdapter : zipEntryAdapters.values()) {
            if (zipEntryAdapter.qualifierMatches(resource.getLocation())) {
                return zipEntryAdapter;
            }
        }
        throw new RuntimeException("Could not find a zip entry adapter for: " + resource.getLocation());
    }
}
