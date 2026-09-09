package com.github.oobila.bukkit.persistence.observers;

/** Notified whenever a cache finishes a global (unpartitioned) load. */
public interface CacheLoadObserver {

    void onCacheLoad();

}
