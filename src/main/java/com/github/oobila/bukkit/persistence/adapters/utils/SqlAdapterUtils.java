package com.github.oobila.bukkit.persistence.adapters.utils;

import com.github.oobila.bukkit.persistence.model.SqlConnectionProperties;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class SqlAdapterUtils {

    private static final Set<String> connectionHolders = new HashSet<>();
    @Getter
    private static Connection connection;

    @SuppressWarnings("java:S4925") //this is required to load the correct SQL driver
    public static Connection createConnection(String tableName, SqlConnectionProperties connectionProperties) {
        if(connectionHolders.isEmpty()) {
            try {
                Class.forName("com.mysql.jdbc.Driver");
                connection = DriverManager.getConnection(
                        "jdbc:mysql://" + connectionProperties.getHostname() + ":" +
                                connectionProperties.getPort() + "/" +
                                connectionProperties.getDatabase(),
                        connectionProperties.getUsername(),
                        connectionProperties.getPassword()
                );
            } catch (SQLException | ClassNotFoundException e) {
                throw new SqlRuntimeException(e);
            }
        }
        connectionHolders.add(tableName);
        return connection;
    }

    public static void closeConnection(String tableName) {
        connectionHolders.remove(tableName);
        if (connectionHolders.isEmpty()) {
            closeAllConnections();
        }
    }

    public static void closeAllConnections() {
        try {
            connection.close();
            connection = null;
        } catch (SQLException e) {
            throw new SqlRuntimeException(e);
        }
    }
}