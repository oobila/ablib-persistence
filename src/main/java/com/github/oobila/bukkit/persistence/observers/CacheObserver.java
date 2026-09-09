package com.github.oobila.bukkit.persistence.observers;

/** Extends {@link CacheLoadObserver} with notifications for a cache's global unload and save. */
public interface CacheObserver extends CacheLoadObserver {

    void onCacheUnload();

    void onCacheSave();

}
