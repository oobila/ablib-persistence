/**
 * Reads and writes already-serialized text ({@link com.github.oobila.bukkit.persistence.adapters.storage.StoredData})
 * to and from a physical storage medium: plain plugin data files
 * ({@link com.github.oobila.bukkit.persistence.adapters.storage.FileStorageAdapter}), a
 * plugin-style config file that merges in new defaults as they're added
 * ({@link com.github.oobila.bukkit.persistence.adapters.storage.ConfigStorageAdapter}), or rows
 * in a SQL table ({@link com.github.oobila.bukkit.persistence.adapters.storage.SqlStorageAdapter}).
 * This layer knows nothing about the shape of the data it stores — that's the job of the
 * {@code adapters.code} package one level up.
 */
package com.github.oobila.bukkit.persistence.adapters.storage;
