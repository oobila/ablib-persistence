package com.github.oobila.bukkit.persistence.adapters.storage;

import org.bukkit.plugin.Plugin;

import java.time.ZonedDateTime;
import java.util.List;

/**
 * Reads and writes already-serialized text to and from a physical storage medium (a file, a SQL
 * table, ...). Implementations know nothing about the shape of the data they store — that's the
 * job of a {@link com.github.oobila.bukkit.persistence.adapters.code.CodeAdapter} one layer up —
 * they only deal in {@code name}s (resolved paths/keys, produced by a
 * {@link com.github.oobila.bukkit.persistence.adapters.vehicle.PersistenceVehicle}) and
 * {@link StoredData} (the raw text plus size/last-modified metadata).
 */
public interface StorageAdapter {

    /** Reads the full data (content included) stored under {@code name}. */
    List<StoredData> read(Plugin plugin, String name);

    /** Reads just the metadata (size/last-modified) for the data stored under {@code name}, without its content — used for lazy/on-demand loading. */
    List<StoredData> readMetaData(Plugin plugin, String name);

    /** Lists the names that currently exist directly under {@code name} (e.g. files in a directory, or keys within a partition). */
    List<String> poll(Plugin plugin, String name);

    /** Writes (overwriting) the data stored under {@code name}. */
    void write(Plugin plugin, String name, List<StoredData> storedDataList);

    /** Copies the plugin's bundled default resource for {@code name} into storage if nothing is there yet, where supported. */
    void copyDefaults(Plugin plugin, String name);

    /** Deletes the data stored under {@code name}. */
    void delete(Plugin plugin, String name);

    /** Whether any data currently exists under {@code name}. */
    boolean exists(Plugin plugin, String name);

    /** When the data stored under {@code name} was last modified. */
    ZonedDateTime getLastUpdated(Plugin plugin, String name);

}
