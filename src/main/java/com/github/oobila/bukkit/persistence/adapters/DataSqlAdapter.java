package com.github.oobila.bukkit.persistence.adapters;

import com.github.oobila.bukkit.persistence.adapters.utils.SqlAdapterUtils;
import com.github.oobila.bukkit.persistence.caches.BaseCache;
import com.github.oobila.bukkit.persistence.model.PersistedObject;
import com.github.oobila.bukkit.persistence.serializers.Serialization;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


public class DataSqlAdapter<K, V extends PersistedObject> implements DataCacheAdapter<K, V> {
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    public void open(BaseCache<K, V> dataCache) {
        SqlAdapterUtils.createConnection(dataCache);
        createTableIfNotExists(dataCache);
    }

    @Override
    public void close(BaseCache<K, V> dataCache) {
        SqlAdapterUtils.closeConnection(dataCache);
    }

    @Override
    public void put(K key, V value, BaseCache<K, V> dataCache) {
        String dateTime = value.getCreatedDate().withZoneSameInstant(ZoneOffset.UTC).toLocalDateTime().format(FORMATTER);
        String yaml = SqlAdapterUtils.serializeData(value);
        String query = String.format(
                "INSERT INTO %s (id, created, data) VALUES('%s','%s','%s') ON DUPLICATE KEY UPDATE created = '%s', data = '%s'",
                SqlAdapterUtils.getTableName(dataCache),
                Serialization.serialize(key),
                dateTime,
                yaml,
                dateTime,
                yaml
        );
        try (Statement statement = SqlAdapterUtils.getConnection().createStatement()) {
            statement.executeUpdate(query);
        } catch (SQLException e) {
            throw new RuntimeException("query: " + query, e);
        }
    }

    @Override
    public V get(K key, BaseCache<K, V> dataCache) {
        String query = String.format(
                "SELECT data FROM %s WHERE id='%s'",
                SqlAdapterUtils.getTableName(dataCache),
                Serialization.serialize(key)
        );
        try (Statement statement = SqlAdapterUtils.getConnection().createStatement()) {
            ResultSet resultSet = statement.executeQuery(query);
            if (resultSet.next()) {
                String data = resultSet.getString("data");
                return SqlAdapterUtils.deserializeData(this, data, dataCache.getType());
            } else {
                return null;
            }
        } catch (SQLException e) {
            throw new RuntimeException("query: " + query, e);
        }
    }

    @Override
    public V remove(K key, BaseCache<K, V> dataCache) {
        V value = get(key, dataCache);
        String query = String.format(
                "DELETE FROM %s WHERE id='%s'",
                SqlAdapterUtils.getTableName(dataCache),
                Serialization.serialize(key)
        );
        try (Statement statement = SqlAdapterUtils.getConnection().createStatement()) {
            statement.executeUpdate(query);
            return value;
        } catch (SQLException e) {
            throw new RuntimeException("query: " + query, e);
        }
    }

    @Override
    public Collection<V> removeBefore(ZonedDateTime zonedDateTime, BaseCache<K, V> dataCache) {
        String query1 = String.format(
                "SELECT data FROM %s WHERE created<'%s'",
                SqlAdapterUtils.getTableName(dataCache),
                zonedDateTime.withZoneSameInstant(ZoneOffset.UTC).toLocalDateTime().format(FORMATTER)
        );
        List<V> valuesRemoved = new ArrayList<>();
        try (Statement statement = SqlAdapterUtils.getConnection().createStatement()) {
            ResultSet resultSet = statement.executeQuery(query1);
            while (resultSet.next()) {
                String data = resultSet.getString("data");
                valuesRemoved.add(SqlAdapterUtils.deserializeData(this, data, dataCache.getType()));
            }
        } catch (SQLException e) {
            throw new RuntimeException("query: " + query1, e);
        }
        String query2 = String.format(
                "DELETE FROM %s WHERE created<'%s'",
                SqlAdapterUtils.getTableName(dataCache),
                zonedDateTime.withZoneSameInstant(ZoneOffset.UTC).toLocalDateTime().format(FORMATTER)
        );
        try (Statement statement = SqlAdapterUtils.getConnection().createStatement()) {
            statement.executeUpdate(query2);
        } catch (SQLException e) {
            throw new RuntimeException("query: " + query2, e);
        }
        return valuesRemoved;
    }

    private void createTableIfNotExists(BaseCache<K,V> dataCache) {
        String query = String.format(
                "CREATE TABLE IF NOT EXISTS %s (id VARCHAR(40) PRIMARY KEY, created DATETIME NOT NULL, data TEXT NOT NULL)",
                SqlAdapterUtils.getTableName(dataCache)
        );
        try (Statement statement = SqlAdapterUtils.getConnection().createStatement()) {
            statement.executeUpdate(query);
        } catch (SQLException e) {
            throw new RuntimeException("query: " + query, e);
        }
    }

    @Override
    public int size(BaseCache<K, V> dataCache) {
        String query = String.format(
                "SELECT count(*) FROM %s",
                SqlAdapterUtils.getTableName(dataCache)
        );
        try (Statement statement = SqlAdapterUtils.getConnection().createStatement()) {
            ResultSet resultSet = statement.executeQuery(query);
            if(resultSet.next()) {
                return resultSet.getInt(1);
            } else {
                return 0;
            }
        } catch (SQLException e) {
            throw new RuntimeException("query: " + query, e);
        }
    }
}
