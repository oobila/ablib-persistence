package com.github.oobila.bukkit.persistence.adapters;

import com.github.oobila.bukkit.persistence.adapters.utils.FileAdapterUtils;
import com.github.oobila.bukkit.persistence.adapters.utils.WorldEditAdapterUtils;
import com.github.oobila.bukkit.persistence.caches.BaseCache;
import com.github.oobila.bukkit.persistence.model.SchematicObject;
import com.github.oobila.bukkit.persistence.serializers.Serialization;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;

import static com.github.oobila.bukkit.common.ABCommon.log;

public class SchematicFileAdapter<K> extends DataClusterFileAdapter<K, SchematicObject> {

    public SchematicFileAdapter() {
        super(SchematicObject.class);
    }

    @Override
    protected SchematicObject onLoad(File saveFile) {
        return WorldEditAdapterUtils.loadSchematic(saveFile);
    }

    @Override
    protected void onSave(File saveFile, SchematicObject value) {
        WorldEditAdapterUtils.saveSchematic(saveFile, value);
    }

    @Override
    public void put(K key, SchematicObject value, BaseCache<K, SchematicObject> cache) {
        File file = getSchematicFile(key, cache);
        WorldEditAdapterUtils.saveSchematic(file, value);
    }

    @Override
    public SchematicObject get(K key, BaseCache<K, SchematicObject> cache) {
        File file = getSchematicFile(key, cache);
        return WorldEditAdapterUtils.loadSchematic(file);
    }

    @Override
    public List<SchematicObject> get(BaseCache<K, SchematicObject> cache) {
        //do nothing
        log(Level.WARNING, "not allowed to get full set of schematics");
        return Collections.emptyList();
    }

    @Override
    public SchematicObject remove(K key, BaseCache<K, SchematicObject> cache) {
        SchematicObject schematicObject = get(key, cache);
        File file = getSchematicFile(key, cache);
        try {
            FileUtils.delete(file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return schematicObject;
    }

    private File getSchematicFile(K key, BaseCache<K, SchematicObject> cache) {
        File saveFolder = FileAdapterUtils.getSaveFile(cache, null);
        return new File(saveFolder, Serialization.serialize(key) + ".schem");
    }
}
