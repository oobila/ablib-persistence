package com.github.oobila.bukkit.persistence;

@SuppressWarnings("unused")
public class PersistenceRuntimeException extends RuntimeException {

    public PersistenceRuntimeException(String message) {
        super(message);
    }

    public PersistenceRuntimeException(String message, Throwable cause) {
        super(message, cause);
    }

    public PersistenceRuntimeException(Throwable cause) {
        super(cause);
    }
}
