package com.github.oobila.bukkit.persistence.adapters.code;

import com.github.oobila.bukkit.persistence.adapters.storage.StoredData;

public interface CodeAdapter<T> {

    Class<T> getType();

    default String getTypeName() {
        return getType().getName();
    }

    T toObject(StoredData storedData);

    String fromObject(T object);

}
