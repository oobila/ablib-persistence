package com.github.oobila.bukkit.persistence.caches;

import com.github.oobila.bukkit.persistence.model.SqlConnectionProperties;

public interface SqlCache extends Cache {

    SqlConnectionProperties getSqlConnectionProperties();

}
