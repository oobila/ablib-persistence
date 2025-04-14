package com.github.oobila.bukkit.persistence.observers;

public interface CacheObserver extends CacheLoadObserver {

    void onCacheUnload();

    void onCacheSave();

}
