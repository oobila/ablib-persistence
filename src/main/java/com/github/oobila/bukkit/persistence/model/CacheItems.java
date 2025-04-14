package com.github.oobila.bukkit.persistence.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Delegate;

import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
@Getter
public class CacheItems<K, V, C extends CacheItem<K, V>> implements Map<K, C> {

    private final String parentKey;

    @Delegate
    private final Map<K, C> map = new HashMap<>();

}
