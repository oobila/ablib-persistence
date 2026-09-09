/**
 * Synchronous cache implementations: all reads and writes happen on the calling thread, and every
 * partition that has been loaded is kept fully in memory ({@code nullCache} for global/unpartitioned
 * data, {@code localCache} keyed by player/partition UUID for the rest).
 * <p>
 * {@link com.github.oobila.bukkit.persistence.caches.standard.ReadOnlyCache} is the base
 * implementation (load/unload/get); {@link com.github.oobila.bukkit.persistence.caches.standard.ReadAndWriteCache}
 * adds put/remove/save on top of it. Prefer these (via the ready-made classes in
 * {@code caches.real}, e.g. {@code SimpleFileCache}) for data that is small enough to hold
 * entirely in memory and where blocking file I/O on the calling thread is acceptable; use the
 * {@code caches.async} equivalents instead for anything backed by SQL or otherwise too slow/large
 * to touch synchronously.
 */
package com.github.oobila.bukkit.persistence.caches.standard;
