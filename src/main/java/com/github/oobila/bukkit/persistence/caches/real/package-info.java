/**
 * Ready-to-use caches that plugins construct directly, each wiring together a
 * {@link com.github.oobila.bukkit.persistence.adapters.vehicle.DynamicVehicle} with a matching
 * {@link com.github.oobila.bukkit.persistence.adapters.storage.StorageAdapter} and
 * {@link com.github.oobila.bukkit.persistence.adapters.code.CodeAdapter} so callers don't have to
 * assemble those pieces themselves. This is the layer most plugins should use — see the module
 * {@code README.md} for a table of which class to pick for a given storage medium and access
 * pattern (file vs. SQL, synchronous vs. asynchronous, fully in-memory vs. on-demand).
 */
package com.github.oobila.bukkit.persistence.caches.real;
