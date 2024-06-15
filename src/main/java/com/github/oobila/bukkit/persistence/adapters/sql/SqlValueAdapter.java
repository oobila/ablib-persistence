package com.github.oobila.bukkit.persistence.adapters.sql;

import com.github.oobila.bukkit.persistence.model.PersistedObject;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public interface SqlValueAdapter<V extends PersistedObject> {

    String type();
    void addValue(int position, V value, PreparedStatement preparedStatement) throws SQLException;
    V getValue(ResultSet resultSet) throws SQLException;

}
