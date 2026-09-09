package com.github.oobila.bukkit.persistence.adapters.storage;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.ZonedDateTime;

/**
 * The raw, already text-serialized form of one stored record, as read from or written to a
 * {@link StorageAdapter}. This is the common currency between the storage layer and the
 * {@link com.github.oobila.bukkit.persistence.adapters.code.CodeAdapter} that turns {@link #data}
 * into an actual value object.
 */
@RequiredArgsConstructor
@Getter
@Builder(toBuilder = true)
public class StoredData {

    /** The record's name/key as understood by its {@code StorageAdapter} (e.g. a file base name, or SQL record key). */
    private final String name;
    /** The serialized text content, or {@code null} when only metadata was requested (see {@link StorageAdapter#readMetaData}). */
    private final String data;
    /** Size of the stored data in bytes/characters. */
    private final long size;
    /** When this record was last written, or {@code null} where unavailable. */
    private final ZonedDateTime updatedDate;

}
