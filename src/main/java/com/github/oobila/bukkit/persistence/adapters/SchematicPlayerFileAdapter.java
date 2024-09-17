package com.github.oobila.bukkit.persistence.adapters;

import com.github.oobila.bukkit.persistence.adapters.utils.FileAdapterUtils;
import com.github.oobila.bukkit.persistence.adapters.utils.WorldEditAdapterUtils;
import com.github.oobila.bukkit.persistence.caches.BaseCache;
import com.github.oobila.bukkit.persistence.model.SchematicObject;
import com.github.oobila.bukkit.persistence.serializers.Serialization;
import org.bukkit.OfflinePlayer;

import java.io.File;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;

import static com.github.oobila.bukkit.common.ABCommon.log;

public class SchematicPlayerFileAdapter<K> extends DataPlayerFileAdapter<K, SchematicObject> {

    public SchematicPlayerFileAdapter() {
        super(SchematicFileAdapter::new);
    }

    @Override
    public void open(BaseCache<K, SchematicObject> cache) {
        //do nothing
    }

    @Override
    public void open(OfflinePlayer offlinePlayer, BaseCache<K, SchematicObject> cache) {
        //do nothing
    }

    @Override
    public void close(BaseCache<K, SchematicObject> playerCache) {
        //do nothing
    }

    @Override
    public void close(OfflinePlayer player, BaseCache<K, SchematicObject> cache) {
        //do nothing
    }

    @Override
    public void put(OfflinePlayer player, K key, SchematicObject value, BaseCache<K, SchematicObject> playerCache) {
        File file = getSchematicFile(player, key, playerCache);
        WorldEditAdapterUtils.saveSchematic(file, value);
    }

    @Override
    public SchematicObject get(OfflinePlayer player, K key, BaseCache<K, SchematicObject> playerCache) {
        File file = getSchematicFile(player, key, playerCache);
        return WorldEditAdapterUtils.loadSchematic(file);
    }

    @Override
    public List<SchematicObject> get(OfflinePlayer player, BaseCache<K, SchematicObject> playerCache) {
        //do nothing
        log(Level.WARNING, "not allowed to get full set of schematics");
        return Collections.emptyList();
    }

    private File getSchematicFile(OfflinePlayer player, K key, BaseCache<K, SchematicObject> cache) {
        File saveFolder = FileAdapterUtils.getSaveFile(cache, player);
        return new File(saveFolder, Serialization.serialize(key) + ".schem");
    }
}
