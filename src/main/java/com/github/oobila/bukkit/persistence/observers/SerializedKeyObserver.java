package com.github.oobila.bukkit.persistence.observers;

import com.github.oobila.bukkit.persistence.serializers.Serialization;

import java.util.List;

public abstract class SerializedKeyObserver<K> extends KeyObserver<K> {

    @Override
    public void onPut(K key) {
        SerializedKeyObserver.this.onPut(Serialization.serialize(key));
    }

    @Override
    public void onRemove(K key) {
        SerializedKeyObserver.this.onRemove(Serialization.serialize(key));
    }

    @Override
    public void onOpen(List<K> keyList) {
        SerializedKeyObserver.this.onInit(keyList.stream().map(Serialization::serialize).toList());
    }

    abstract void onPut(String key);
    abstract void onRemove(String key);
    abstract void onInit(List<String> keyList);
}
