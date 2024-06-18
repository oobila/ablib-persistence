package com.github.oobila.bukkit.persistence.adapters.schematic;

import com.github.oobila.bukkit.persistence.adapters.DataFileAdapter;
import com.github.oobila.bukkit.persistence.adapters.utils.FileAdapterUtils;
import com.github.oobila.bukkit.persistence.adapters.utils.WorldEditFileAdapterUtils;
import com.github.oobila.bukkit.persistence.caches.BaseCache;
import com.github.oobila.bukkit.persistence.model.SchematicObject;
import com.github.oobila.bukkit.persistence.serializers.Serialization;

import java.io.File;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;

import static com.github.oobila.bukkit.common.ABCommon.log;

public class SchematicFileAdapter<K> extends DataFileAdapter<K, SchematicObject> {

    public SchematicFileAdapter() {
        super(true);
    }

    @Override
    protected SchematicObject onLoadCluster(File saveFile) {
        return WorldEditFileAdapterUtils.loadSchematic(saveFile);
    }

    @Override
    protected void onSaveCluster(File saveFile, SchematicObject value) {
        WorldEditFileAdapterUtils.saveSchematic(saveFile, value);
    }

    @Override
    public void open(BaseCache<K, SchematicObject> cache) {
        //do nothing
    }

    @Override
    public void close(BaseCache<K, SchematicObject> cache) {
        //do nothing
    }

    @Override
    public void put(K key, SchematicObject value, BaseCache<K, SchematicObject> cache) {
        File file = getSchematicFile(key, cache);
        WorldEditFileAdapterUtils.saveSchematic(file, value);
    }

    @Override
    public SchematicObject get(K key, BaseCache<K, SchematicObject> cache) {
        File file = getSchematicFile(key, cache);
        return WorldEditFileAdapterUtils.loadSchematic(file);
    }

    @Override
    public List<SchematicObject> get(BaseCache<K, SchematicObject> cache) {
        //do nothing
        log(Level.WARNING, "not allowed to get full set of schematics");
        return Collections.emptyList();
    }

    private File getSchematicFile(K key, BaseCache<K, SchematicObject> cache) {
        File saveFolder = FileAdapterUtils.getSaveFile(cache, null);
        return new File(saveFolder, Serialization.serialize(key) + ".schem");
    }
}
