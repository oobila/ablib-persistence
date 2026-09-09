/**
 * The public-facing API that plugin code interacts with: {@code get}/{@code put}/{@code remove}
 * style access to persisted data, plus lifecycle methods ({@code load}/{@code save}/
 * {@code unload}) and short-lived {@code transaction(...)} helpers that load a player's partition,
 * run some code, and unload it again if it wasn't already loaded.
 * <p>
 * {@link com.github.oobila.bukkit.persistence.caches.Cache} is the root marker interface,
 * {@link com.github.oobila.bukkit.persistence.caches.ReadCache} adds loading, and
 * {@link com.github.oobila.bukkit.persistence.caches.WriteCache} adds saving. These interfaces
 * are intentionally minimal and generic (key type {@code K}, value type {@code V}, and the
 * {@code CacheItem} subtype {@code C} used to wrap each value) — see the {@code standard} and
 * {@code async} sub-packages for the concrete flavours (synchronous vs. asynchronous access) that
 * most code actually implements/extends, and the {@code real} sub-package for ready-to-use cache
 * implementations.
 */
package com.github.oobila.bukkit.persistence.caches;
