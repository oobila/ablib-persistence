package com.github.oobila.bukkit.persistence.adapters.schematic;

import com.github.oobila.bukkit.persistence.adapters.PlayerSqlAdapter;
import com.github.oobila.bukkit.persistence.adapters.sql.SchematicSqlAdapter;
import com.github.oobila.bukkit.persistence.model.SchematicObject;

public class SchematicPlayerSqlAdapter<K> extends PlayerSqlAdapter<K, SchematicObject> {
    public SchematicPlayerSqlAdapter() {
        super(new SchematicSqlAdapter());
    }
}
