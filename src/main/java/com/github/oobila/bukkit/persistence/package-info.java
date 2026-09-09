/**
 * Root package of ablib-persistence: a generic, pluggable persistence layer for Bukkit / Spigot /
 * Paper plugins, used to store and retrieve data (most commonly {@code ItemStack}s and other
 * {@code ConfigurationSerializable} objects, but also WorldEdit clipboards, config values, and
 * arbitrary key/value records) without hand-writing file or SQL I/O for every plugin.
 * <p>
 * See the module {@code README.md} for a full architecture overview. In short: plugin code talks
 * to a {@link com.github.oobila.bukkit.persistence.caches.Cache} (see the {@code caches} package
 * and its {@code standard}/{@code async}/{@code real} sub-packages), which delegates the actual
 * reading and writing to a
 * {@link com.github.oobila.bukkit.persistence.adapters.vehicle.PersistenceVehicle} (the
 * {@code adapters.vehicle} package), which in turn combines a
 * {@link com.github.oobila.bukkit.persistence.adapters.code.CodeAdapter} (object &lt;-&gt; text,
 * see {@code adapters.code}) with a
 * {@link com.github.oobila.bukkit.persistence.adapters.storage.StorageAdapter} (text &lt;-&gt;
 * file/SQL/config, see {@code adapters.storage}).
 */
package com.github.oobila.bukkit.persistence;
