/**
 * The data types passed between the {@code caches}, {@code adapters.vehicle},
 * {@code adapters.code} and {@code adapters.storage} layers: a cached value plus its metadata
 * ({@link com.github.oobila.bukkit.persistence.model.CacheItem} and its lazily-loaded subclass
 * {@link com.github.oobila.bukkit.persistence.model.OnDemandCacheItem}), a named group of them
 * ({@link com.github.oobila.bukkit.persistence.model.CacheItems}), the resource-pack container
 * types ({@link com.github.oobila.bukkit.persistence.model.ResourcePack},
 * {@link com.github.oobila.bukkit.persistence.model.Resource}), simple config value types
 * ({@link com.github.oobila.bukkit.persistence.model.SqlConnectionProperties},
 * {@link com.github.oobila.bukkit.persistence.model.MessageQueue}), and the find/replace rule
 * type used for migrating old save data
 * ({@link com.github.oobila.bukkit.persistence.model.BackwardsCompatibility}).
 */
package com.github.oobila.bukkit.persistence.model;
