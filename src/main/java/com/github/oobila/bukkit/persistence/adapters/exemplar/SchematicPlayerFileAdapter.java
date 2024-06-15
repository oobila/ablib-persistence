package com.github.oobila.bukkit.persistence.adapters.exemplar;

import com.github.oobila.bukkit.persistence.adapters.PlayerFileAdapter;
import com.github.oobila.bukkit.persistence.model.SchematicObject;

public class SchematicPlayerFileAdapter<K> extends PlayerFileAdapter<K, SchematicObject> {

    public SchematicPlayerFileAdapter() {
        super(new SchematicFileAdapter<>());
    }
}
