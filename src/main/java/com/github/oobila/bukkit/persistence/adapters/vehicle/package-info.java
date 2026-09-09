/**
 * The layer that sits between a {@link com.github.oobila.bukkit.persistence.caches.Cache} and
 * physical storage. A {@code PersistenceVehicle} knows how to turn a
 * {@link com.github.oobila.bukkit.persistence.adapters.storage.StorageAdapter}'s raw
 * {@link com.github.oobila.bukkit.persistence.adapters.storage.StoredData} into
 * {@link com.github.oobila.bukkit.persistence.model.CacheItem}s and back again, using a
 * {@link com.github.oobila.bukkit.persistence.adapters.code.CodeAdapter} to (de)serialize the
 * value objects themselves.
 * <p>
 * {@link com.github.oobila.bukkit.persistence.adapters.vehicle.DynamicVehicle} is the one
 * concrete implementation in active use: it is configured with a path template (e.g.
 * {@code "players/{uuid}/homes/{key}.yml"}) containing the placeholders
 * {@link com.github.oobila.bukkit.persistence.adapters.vehicle.DynamicVehicle#PARTITION_STRING}
 * and {@link com.github.oobila.bukkit.persistence.adapters.vehicle.DynamicVehicle#KEY_STRING},
 * and works out at runtime whether/how data is partitioned per player and per record, resolving
 * concrete paths by polling the underlying {@code StorageAdapter}.
 */
package com.github.oobila.bukkit.persistence.adapters.vehicle;
