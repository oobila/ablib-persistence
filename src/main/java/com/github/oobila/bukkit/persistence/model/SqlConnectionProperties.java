package com.github.oobila.bukkit.persistence.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

/** Connection details for the shared MySQL connection used by {@link com.github.oobila.bukkit.persistence.adapters.storage.SqlStorageAdapter}. */
@AllArgsConstructor
@Getter
public class SqlConnectionProperties {

    private String hostname;
    private String port;
    private String database;
    private String username;
    private String password;

}
