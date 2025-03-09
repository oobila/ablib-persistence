package com.github.oobila.bukkit.persistence.old.adapters.code;

import com.github.oobila.bukkit.persistence.PersistenceRuntimeException;
import com.github.oobila.bukkit.persistence.old.adapters.storage.StoredData;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.bukkit.plugin.Plugin;

@RequiredArgsConstructor
@Getter
public class DummyCodeAdapter<T> implements CodeAdapter<T> {

    private final Class<T> type;
    @Setter
    private Plugin plugin;

    @Override
    public T toObject(StoredData storedData) {
        throw new PersistenceRuntimeException("this class should not be used for adapting");
    }

    @Override
    public String fromObject(T object) {
        throw new PersistenceRuntimeException("this class should not be used for adapting");
    }
}
