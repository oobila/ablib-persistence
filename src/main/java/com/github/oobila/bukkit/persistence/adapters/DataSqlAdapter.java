package com.github.oobila.bukkit.persistence.adapters;

import com.github.oobila.bukkit.persistence.SqlRuntimeException;
import com.github.oobila.bukkit.persistence.adapters.sql.SqlValueAdapter;
import com.github.oobila.bukkit.persistence.adapters.utils.SqlAdapterUtils;
import com.github.oobila.bukkit.persistence.caches.BaseCache;
import com.github.oobila.bukkit.persistence.model.PersistedObject;
import com.github.oobila.bukkit.persistence.serializers.Serialization;
import lombok.RequiredArgsConstructor;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


@RequiredArgsConstructor
public class DataSqlAdapter<K, V extends PersistedObject> implements DataCacheAdapter<K, V> {
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final SqlValueAdapter<V> adapter;

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
    public boolean contains(K key, BaseCache<K, V> dataCache) {
        return get(key, dataCache) != null;
    }

    @Override
    public void put(K key, V value, BaseCache<K, V> dataCache) {
        String dateTime = value.getCreatedDate().withZoneSameInstant(ZoneOffset.UTC).toLocalDateTime().format(FORMATTER);
        String query = String.format(
                "INSERT INTO %s (id, created, data) VALUES('%s','%s',?) ON DUPLICATE KEY UPDATE created = '%s', data = ?",
                SqlAdapterUtils.getTableName(dataCache),
                Serialization.serialize(key),
                dateTime,
                dateTime
        );
        try (PreparedStatement statement = SqlAdapterUtils.getConnection().prepareStatement(query)) {
            adapter.addValue(1, value, statement);
            adapter.addValue(2, value, statement);
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new SqlRuntimeException(query, e);
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
                return adapter.getValue(resultSet);
            } else {
                return null;
            }
        } catch (SQLException e) {
            throw new SqlRuntimeException(query, e);
        }
    }

    @Override
    public List<V> get(BaseCache<K, V> dataCache) {
        String query = String.format(
                "SELECT data FROM %s",
                SqlAdapterUtils.getTableName(dataCache)
        );
        try (Statement statement = SqlAdapterUtils.getConnection().createStatement()) {
            ResultSet resultSet = statement.executeQuery(query);
            List<V> list = new ArrayList<>();
            while (resultSet.next()) {
                list.add(adapter.getValue(resultSet));
            }
            return list;
        } catch (SQLException e) {
            throw new SqlRuntimeException(query, e);
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
            throw new SqlRuntimeException(query, e);
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
                valuesRemoved.add(adapter.getValue(resultSet));
            }
        } catch (SQLException e) {
            throw new SqlRuntimeException(query1, e);
        }
        String query2 = String.format(
                "DELETE FROM %s WHERE created<'%s'",
                SqlAdapterUtils.getTableName(dataCache),
                zonedDateTime.withZoneSameInstant(ZoneOffset.UTC).toLocalDateTime().format(FORMATTER)
        );
        try (Statement statement = SqlAdapterUtils.getConnection().createStatement()) {
            statement.executeUpdate(query2);
        } catch (SQLException e) {
            throw new SqlRuntimeException(query2, e);
        }
        return valuesRemoved;
    }

    private void createTableIfNotExists(BaseCache<K,V> dataCache) {
        String query = String.format(
                "CREATE TABLE IF NOT EXISTS %s (id VARCHAR(40) PRIMARY KEY, created DATETIME NOT NULL, data %s NOT NULL)",
                SqlAdapterUtils.getTableName(dataCache),
                adapter.type()
        );
        try (Statement statement = SqlAdapterUtils.getConnection().createStatement()) {
            statement.executeUpdate(query);
        } catch (SQLException e) {
            throw new SqlRuntimeException(query, e);
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
            throw new SqlRuntimeException(query, e);
        }
    }
}
