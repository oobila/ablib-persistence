package com.github.oobila.bukkit.persistence.adapters.exemplar;

import com.github.oobila.bukkit.persistence.adapters.DataFileAdapter;
import com.github.oobila.bukkit.persistence.adapters.utils.WorldEditFileAdapterUtils;
import com.github.oobila.bukkit.persistence.model.SchematicObject;

import java.io.File;

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
}
