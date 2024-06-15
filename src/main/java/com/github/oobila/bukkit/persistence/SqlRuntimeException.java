package com.github.oobila.bukkit.persistence;

public class SqlRuntimeException extends RuntimeException {

    public SqlRuntimeException(String query, Exception e) {
        super("query: " + query, e);
    }

    public SqlRuntimeException(Exception e) {
        super(e);
    }

}
