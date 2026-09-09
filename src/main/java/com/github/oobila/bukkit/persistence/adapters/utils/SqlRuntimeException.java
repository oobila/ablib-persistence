package com.github.oobila.bukkit.persistence.adapters.utils;

/** Unchecked wrapper for {@code SQLException}/{@code ClassNotFoundException} failures in {@link SqlAdapterUtils}. */
public class SqlRuntimeException extends RuntimeException {
    public SqlRuntimeException(Throwable e) {
        super(e);
    }
}
