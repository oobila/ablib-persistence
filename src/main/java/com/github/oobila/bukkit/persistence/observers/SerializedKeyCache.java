package com.github.oobila.bukkit.persistence.observers;

import lombok.experimental.Delegate;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class SerializedKeyCache<K> extends SerializedKeyObserver<K> implements Collection<String> {

    @Delegate(excludes = SerializedKeyObserver.class)
    private final List<String> cache = new ArrayList<>();

    @Override
    void onPut(String key) {
        cache.add(key);
        cache.sort(String::compareTo);
    }

    @Override
    void onRemove(String key) {
        cache.remove(key);
    }

    @Override
    void onInit(List<String> keyList) {
        cache.addAll(keyList);
        cache.sort(String::compareTo);
    }
}
