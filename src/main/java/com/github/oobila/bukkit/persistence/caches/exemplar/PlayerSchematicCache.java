package com.github.oobila.bukkit.persistence.caches.exemplar;

import com.github.oobila.bukkit.persistence.adapters.PlayerFileAdapter;
import com.github.oobila.bukkit.persistence.adapters.PlayerSqlAdapter;
import com.github.oobila.bukkit.persistence.adapters.sql.SchematicSqlAdapter;
import com.github.oobila.bukkit.persistence.caches.PlayerCache;
import com.github.oobila.bukkit.persistence.model.SchematicObject;
import com.github.oobila.bukkit.persistence.model.SqlConnectionProperties;

import java.util.UUID;

public class PlayerSchematicCache extends PlayerCache<UUID, SchematicObject> {

    public PlayerSchematicCache(String name, SqlConnectionProperties connectionProperties) {
        super(name, UUID.class, SchematicObject.class, new PlayerSqlAdapter<>(new SchematicSqlAdapter()));
        setSqlConnectionProperties(connectionProperties);
    }

    public PlayerSchematicCache(String name) {
        super(name, UUID.class, SchematicObject.class, new PlayerFileAdapter<>());
    }
}
