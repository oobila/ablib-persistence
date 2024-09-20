package com.github.oobila.bukkit.persistence.adapters.code;

import com.github.oobila.bukkit.persistence.adapters.storage.StoredData;

public class StringCodeAdapter implements CodeAdapter<String> {

    @Override
    public Class<String> getType() {
        return String.class;
    }

    @Override
    public String toObject(StoredData storedData) {
        return storedData.getData();
    }

    @Override
    public String fromObject(String object) {
        return object;
    }
}
