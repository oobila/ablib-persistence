package com.github.oobila.bukkit.persistence.adapters;

import com.github.oobila.bukkit.persistence.adapters.utils.FileAdapterUtils;
import com.github.oobila.bukkit.persistence.caches.BaseCache;
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

public class ResourceFileAdapter<K> implements ResourceCacheAdapter<K, ResourcePack> {

    private final Map<K, ResourcePack> localCache = new HashMap<>();
    private Class<K> keyType;

    public ResourceFileAdapter(Class<K> keyType) {
        this.keyType = keyType;
    }

    @Override
    public void open(BaseCache<K, ResourcePack> cache) {
        File directory = FileAdapterUtils.getClusterLocation(cache, null);
        if (directory.exists()) {
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
                        ResourcePack resourcePack = new ResourcePack(file);
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
                            Resource resource = new Resource(name, size);
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

    private void loadResourceData(ResourcePack resourcePack) {
        Map<String, Resource> resources = resourcePack.getResources();
        File file = resourcePack.getFile();
        try (ZipFile zip = new ZipFile(file)) {
            for (Enumeration<? extends ZipEntry> zipEntries = zip.entries(); zipEntries.hasMoreElements(); ) {
                ZipEntry entry = zipEntries.nextElement();
                try {
                    if (!entry.isDirectory()) {
                        //found file inside zip
                        String name = entry.getName();
                        Resource resource = resources.get(name);
                        if (resource != null) {

                        }
//                        InputStream inputStream = zip.getInputStream(entry);
//                        ResourceType resourceType = ResourceType.of(FilenameUtils.getExtension(entry.getName()));
//                        Object o;
//                        switch (resourceType) {
//                            case YAML ->
//                                    log(Level.INFO, "todo");
////                                    o = FileAdapterUtils.loadConfiguration(inputStream); TODO
//                            case SCHEMATIC ->
//                                    o = WorldEditFileAdapterUtils.loadSchematic(
//                                            inputStream,
//                                            ZonedDateTime.ofInstant(
//                                                    entry.getLastModifiedTime().toInstant(),
//                                                    ZoneId.systemDefault()
//                                            )
//                                    );
//                            default ->
//                                    log(Level.SEVERE, "Failed to load resource: {0}", entry.getName());
//                        }
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

    private void unloadResourceData(ResourcePack resourcePack) {
        resourcePack.getResources().values().forEach(Resource::clearData);
    }
}
