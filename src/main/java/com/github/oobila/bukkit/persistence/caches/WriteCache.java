package com.github.oobila.bukkit.persistence.caches;

import java.util.UUID;

public interface WriteCache<K, V> extends ReadCache<K, V> {

    void save();

    void save(UUID partition);

}
