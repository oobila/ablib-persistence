package com.github.oobila.bukkit.persistence.caches;

import com.github.oobila.bukkit.persistence.adapters.utils.FileAdapterUtils;
import com.github.oobila.bukkit.persistence.adapters.utils.WorldEditFileAdapterUtils;
import com.github.oobila.bukkit.persistence.model.PersistedObject;
import com.github.oobila.bukkit.persistence.model.ResourceType;
import com.github.oobila.bukkit.persistence.serializers.Serialization;
import org.apache.commons.io.FilenameUtils;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Enumeration;
import java.util.logging.Level;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import static com.github.oobila.bukkit.common.ABCommon.log;

public class ResourceCache<K, V extends PersistedObject> extends BaseCache<K, V> {

    private static final String SUB_FOLDER_NAME = "resources";

    public ResourceCache(String name, Class<K> keyType, Class<V> type) {
        super(name, keyType, type);
    }

    public V load(K key) {
        String resourcePackName = Serialization.serialize(key);
        File resourceFolder = FileAdapterUtils.getSaveFile(this, null);
        File resourceFile = new File(resourceFolder, resourcePackName);
        try (ZipFile zip = new ZipFile(resourceFile)) {
            for (Enumeration<? extends ZipEntry> zipEntries = zip.entries(); zipEntries.hasMoreElements(); ) {
                ZipEntry entry = zipEntries.nextElement();
                try {
                    if (!entry.isDirectory()) {
                        //found file inside zip
                        InputStream inputStream = zip.getInputStream(entry);
                        ResourceType resourceType = ResourceType.of(FilenameUtils.getExtension(entry.getName()));
                        Object o;
                        switch (resourceType) {
                            case YAML ->
                                    log(Level.INFO, "todo");
//                                    o = FileAdapterUtils.loadConfiguration(inputStream); TODO
                            case SCHEMATIC ->
                                    o = WorldEditFileAdapterUtils.loadSchematic(
                                            inputStream,
                                            ZonedDateTime.ofInstant(
                                                    entry.getLastModifiedTime().toInstant(),
                                                    ZoneId.systemDefault()
                                            )
                                    );
                            default ->
                                log(Level.SEVERE, "Failed to load resource: {0}", entry.getName());
                        }
                    }
                } catch (Exception e) {
                    log(Level.SEVERE, "Failed to load resource: {0}", entry.getName());
                    log(Level.SEVERE, e);
                }
            }
        } catch (IOException e) {
            log(Level.SEVERE, "Failed loading resource pack: {0}", resourcePackName);
            log(Level.SEVERE, e);
        }
        return null;
    }

    @Override
    public String getSubFolderName() {
        return SUB_FOLDER_NAME;
    }

    @Override
    public void open(Plugin plugin) {
        //TODO
    }

    @Override
    public void close() {
        //not required
    }
}
