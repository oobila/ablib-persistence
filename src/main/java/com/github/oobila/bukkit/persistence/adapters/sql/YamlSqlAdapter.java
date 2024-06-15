package com.github.oobila.bukkit.persistence.adapters.sql;

import com.github.oobila.bukkit.persistence.adapters.CacheReader;
import com.github.oobila.bukkit.persistence.adapters.utils.SqlAdapterUtils;
import com.github.oobila.bukkit.persistence.model.PersistedObject;
import lombok.RequiredArgsConstructor;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static com.github.oobila.bukkit.persistence.Constants.DATA;

@RequiredArgsConstructor
public class YamlSqlAdapter<V extends PersistedObject> implements SqlValueAdapter<V> {

    private final CacheReader cacheReader;
    private final Class<V> type;

    @Override
    public String type() {
        return "TEXT";
    }

    @Override
    public void addValue(int position, V value, PreparedStatement preparedStatement) throws SQLException {
        String yaml = SqlAdapterUtils.serializeData(value);
        preparedStatement.setString(position, yaml);
    }

    @Override
    public V getValue(ResultSet resultSet) throws SQLException {
        String data = resultSet.getString(DATA);
        return SqlAdapterUtils.deserializeData(cacheReader, data, type);
    }

}
