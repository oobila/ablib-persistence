package com.github.oobila.bukkit.persistence.observers;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

/**
 * Implemented directly by a cache (rather than registered as a separate observer) that wants to
 * react to a player joining/leaving — see {@code PersistencePlayerJoinListener}, which checks for
 * this interface on every cache registered via {@code CacheManager.registerPlayerCache(...)} and
 * calls it after the cache's own load (on join) or before its unload (on leave). Used by
 * {@code MessageCache} to deliver queued messages on join.
 */
@SuppressWarnings("unused")
public interface PlayerObserver {

    void onJoin(Player player);

    void onLeave(OfflinePlayer player);

}
