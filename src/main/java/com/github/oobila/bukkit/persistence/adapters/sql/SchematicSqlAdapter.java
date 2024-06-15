package com.github.oobila.bukkit.persistence.adapters.sql;

import com.github.oobila.bukkit.persistence.SqlRuntimeException;
import com.github.oobila.bukkit.persistence.model.SchematicObject;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static com.github.oobila.bukkit.persistence.Constants.DATA;

public class SchematicSqlAdapter implements SqlValueAdapter<SchematicObject> {

    @Override
    public String type() {
        return "LONGBLOB";
    }

    @Override
    public void addValue(int position, SchematicObject schematicObject, PreparedStatement preparedStatement) throws SQLException {
        try {
            byte[] data = convertToBytes(schematicObject.getClipboard());
            preparedStatement.setBytes(position, data);
        } catch (IOException e) {
            preparedStatement.setBytes(position, null);
            throw new SqlRuntimeException(e);
        }
    }

    @Override
    public SchematicObject getValue(ResultSet resultSet) throws SQLException {
        try {
            return (SchematicObject) convertFromBytes(resultSet.getBytes(DATA));
        } catch (IOException | ClassNotFoundException e) {
            throw new SqlRuntimeException(e);
        }
    }

    private byte[] convertToBytes(Object object) throws IOException {
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream();
             ObjectOutputStream out = new ObjectOutputStream(bos)) {
            out.writeObject(object);
            return bos.toByteArray();
        }
    }

    private Object convertFromBytes(byte[] bytes) throws IOException, ClassNotFoundException {
        try (ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
             ObjectInputStream in = new ObjectInputStream(bis)) {
            return in.readObject();
        }
    }

}
