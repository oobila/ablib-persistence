package com.github.oobila.bukkit.persistence.caches;

public interface WriteCache<K, V> extends ReadCache<K, V> {

    void save();

}
