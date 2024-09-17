package com.github.oobila.bukkit.persistence.adapters.zip;

import com.github.oobila.bukkit.persistence.model.PersistedObject;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public abstract class BaseZipAdapter<V extends PersistedObject> implements ZipEntryAdapter<V> {

}
