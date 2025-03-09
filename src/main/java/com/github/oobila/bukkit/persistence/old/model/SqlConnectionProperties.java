package com.github.oobila.bukkit.persistence.old.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class SqlConnectionProperties {

    private String hostname;
    private String port;
    private String database;
    private String username;
    private String password;

}
