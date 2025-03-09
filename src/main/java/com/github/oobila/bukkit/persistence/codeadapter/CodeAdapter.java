package com.github.oobila.bukkit.persistence.codeadapter;

import com.github.oobila.bukkit.persistence.codeadapter.model.StoredData;

public interface CodeAdapter<T> {

    StoredData serialize(T t);

    T deserialize(StoredData data);

}
