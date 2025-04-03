package com.github.oobila.bukkit.persistence.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Delegate;

import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
@Getter
public class CacheItems<K, V> implements Map<K, CacheItem<K, V>> {

    private final String parentKey;

    @Delegate
    private final Map<K, CacheItem<K, V>> map = new HashMap<>();

}
