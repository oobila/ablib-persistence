/**
 * Asynchronous cache implementations: reads and writes are dispatched off the main server thread
 * (see the {@code runTaskAsync}/{@code runTaskLater} calls to ablib-common's scheduler helpers),
 * with results delivered via a {@code Consumer} callback instead of a direct return value.
 * <p>
 * {@link com.github.oobila.bukkit.persistence.caches.async.AsyncReadOnlyCache} and
 * {@link com.github.oobila.bukkit.persistence.caches.async.AsyncReadAndWriteCache} behave like
 * their {@code caches.standard} counterparts but keep data fully in memory once loaded, just
 * asynchronously. {@link com.github.oobila.bukkit.persistence.caches.async.AsyncOnDemandCache} is
 * different: it does <em>not</em> hold values in memory long-term. It loads
 * {@link com.github.oobila.bukkit.persistence.model.OnDemandCacheItem}s that fetch their data lazily
 * from storage the first time it's read and then automatically unload it again after a fixed
 * retention window ({@code RETENTION_TICKS}), which is what makes it safe to back with a SQL table
 * containing far more data than could reasonably be kept resident in memory (see
 * {@code SimpleSqlCache}, {@code ClipboardSqlCache}, {@code StringSqlCache} in {@code caches.real}).
 */
package com.github.oobila.bukkit.persistence.caches.async;
