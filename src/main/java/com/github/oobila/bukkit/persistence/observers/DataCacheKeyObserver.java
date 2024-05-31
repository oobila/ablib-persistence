package com.github.oobila.bukkit.persistence.observers;

import java.util.List;

public interface DataCacheKeyObserver<K> {

    void onPut(K key);
    void onRemove(K key);
    void onOpen(List<K> keyList);

}
