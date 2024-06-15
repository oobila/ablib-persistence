package com.github.oobila.bukkit.persistence.adapters;

import com.github.oobila.bukkit.persistence.SqlRuntimeException;
import com.github.oobila.bukkit.persistence.adapters.sql.SqlValueAdapter;
import com.github.oobila.bukkit.persistence.adapters.utils.SqlAdapterUtils;
import com.github.oobila.bukkit.persistence.caches.BaseCache;
import com.github.oobila.bukkit.persistence.model.PersistedObject;
import com.github.oobila.bukkit.persistence.serializers.Serialization;
import lombok.RequiredArgsConstructor;
import org.bukkit.OfflinePlayer;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

import static com.github.oobila.bukkit.persistence.Constants.DATA;

@RequiredArgsConstructor
public class PlayerSqlAdapter<K, V extends PersistedObject> implements PlayerCacheAdapter<K, V> {

    private static final ZoneId UTC = ZoneId.of("UTC");
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final SqlValueAdapter<V> adapter;

    @Override
    public void open(BaseCache<K, V> playerCache) {
        SqlAdapterUtils.createConnection(playerCache);
        createTableIfNotExists(playerCache);
    }

    @Override
    public void open(OfflinePlayer player, BaseCache<K, V> playerCache) {
        //do nothing
    }

    @Override
    public void close(BaseCache<K, V> playerCache) {
        SqlAdapterUtils.closeConnection(playerCache);
    }

    @Override
    public void close(OfflinePlayer player, BaseCache<K, V> playerCache) {
        //do nothing
    }

    @Override
    public void put(OfflinePlayer player, K key, V value, BaseCache<K, V> playerCache) {
        String dateTime = value.getCreatedDate().withZoneSameInstant(UTC).toLocalDateTime().format(FORMATTER);
        String query = String.format(
                "INSERT INTO %s (id, player, created, data) VALUES('%s','%s','%s',?) ON DUPLICATE KEY UPDATE created = '%s', data = ?",
                SqlAdapterUtils.getTableName(playerCache),
                Serialization.serialize(key),
                Serialization.serialize(player),
                dateTime,
                dateTime
        );
        try (PreparedStatement statement = SqlAdapterUtils.getConnection().prepareStatement(query)) {
            adapter.addValue(1, value, statement);
            adapter.addValue(2, value, statement);
            statement.executeUpdate(query);
        } catch (SQLException e) {
            throw new SqlRuntimeException(query, e);
        }
    }

    @Override
    public V get(OfflinePlayer player, K key, BaseCache<K, V> playerCache) {
        String query = String.format(
                "SELECT data FROM %s WHERE id='%s' AND player='%s'",
                SqlAdapterUtils.getTableName(playerCache),
                Serialization.serialize(key),
                Serialization.serialize(player)
        );
        try (Statement statement = SqlAdapterUtils.getConnection().createStatement()) {
            ResultSet resultSet = statement.executeQuery(query);
            if(resultSet.next()) {
                return adapter.getValue(resultSet);
            } else {
                return null;
            }
        } catch (SQLException e) {
            throw new SqlRuntimeException(query, e);
        }
    }

    @Override
    public void remove(OfflinePlayer player, K key, BaseCache<K, V> playerCache) {
        String query = String.format(
                "DELETE FROM %s WHERE id='%s' AND player='%s'",
                SqlAdapterUtils.getTableName(playerCache),
                Serialization.serialize(key),
                Serialization.serialize(player)
        );
        try (Statement statement = SqlAdapterUtils.getConnection().createStatement()) {
            statement.executeUpdate(query);
        } catch (SQLException e) {
            throw new SqlRuntimeException(query, e);
        }
    }

    @Override
    public void remove(OfflinePlayer player, BaseCache<K, V> playerCache) {
        String query = String.format(
                "DELETE FROM %s WHERE player='%s'",
                SqlAdapterUtils.getTableName(playerCache),
                Serialization.serialize(player)
        );
        try (Statement statement = SqlAdapterUtils.getConnection().createStatement()) {
            statement.executeUpdate(query);
        } catch (SQLException e) {
            throw new SqlRuntimeException(query, e);
        }
    }

    private void createTableIfNotExists(BaseCache<K,V> dataCache) {
        String query = String.format(
                "CREATE TABLE IF NOT EXISTS %s (id VARCHAR(40) PRIMARY KEY, player VARCHAR(40) NOT NULL UNIQUE, created DATETIME NOT NULL, data %s NOT NULL)",
                SqlAdapterUtils.getTableName(dataCache),
                adapter.type()
        );
        try (Statement statement = SqlAdapterUtils.getConnection().createStatement()) {
            statement.executeUpdate(query);
        } catch (SQLException e) {
            throw new SqlRuntimeException(query, e);
        }
    }

}