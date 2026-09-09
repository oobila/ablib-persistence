package com.github.oobila.bukkit.persistence;

/**
 * Unchecked exception thrown for any persistence failure (I/O errors, unsupported operations for
 * a given {@link com.github.oobila.bukkit.persistence.adapters.vehicle.PersistenceVehicle}
 * configuration, misconfigured serialization, etc.). Kept unchecked so that
 * {@link com.github.oobila.bukkit.persistence.adapters.code.CodeAdapter}/
 * {@link com.github.oobila.bukkit.persistence.adapters.storage.StorageAdapter} implementations
 * don't force checked-exception handling onto every caller; wrap and rethrow the original cause
 * where one exists so it isn't lost.
 */
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
