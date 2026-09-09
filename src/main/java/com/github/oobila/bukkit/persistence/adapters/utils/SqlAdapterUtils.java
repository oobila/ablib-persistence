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

/**
 * Manages a single shared JDBC {@link Connection} reused by every
 * {@link com.github.oobila.bukkit.persistence.adapters.storage.SqlStorageAdapter} table on the
 * server, reference-counted by table name so the connection is only closed once every table using
 * it has released it.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class SqlAdapterUtils {

    /** Table names currently holding a reference to {@link #connection}; the connection closes once this is empty. */
    private static final Set<String> connectionHolders = new HashSet<>();
    @Getter
    private static Connection connection;

    /** Opens the shared connection if not already open, and registers {@code tableName} as a holder of it. */
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

    /** Releases {@code tableName}'s hold on the shared connection, closing it once no table needs it any more. */
    public static void closeConnection(String tableName) {
        connectionHolders.remove(tableName);
        if (connectionHolders.isEmpty()) {
            closeAllConnections();
        }
    }

    /** Force-closes the shared connection regardless of remaining holders. */
    public static void closeAllConnections() {
        try {
            connection.close();
            connection = null;
        } catch (SQLException e) {
            throw new SqlRuntimeException(e);
        }
    }
}