package com.github.oobila.bukkit.persistence.adapters.schematic;

import com.github.oobila.bukkit.persistence.adapters.DataSqlAdapter;
import com.github.oobila.bukkit.persistence.model.SchematicObject;

public class SchematicSqlAdapter<K> extends DataSqlAdapter<K, SchematicObject> {

    public SchematicSqlAdapter() {
        super(new com.github.oobila.bukkit.persistence.adapters.sql.SchematicSqlAdapter());
    }
}
