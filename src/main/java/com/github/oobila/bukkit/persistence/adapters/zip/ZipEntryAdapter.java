package com.github.oobila.bukkit.persistence.adapters.zip;

import com.github.oobila.bukkit.persistence.adapters.ResourceFileAdapter;
import com.github.oobila.bukkit.persistence.model.PersistedObject;

import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public interface ZipEntryAdapter<V extends PersistedObject> {

    <K> V getValue(ZipEntry entry, ZipFile zip, ResourceFileAdapter<K> resourceFileAdapter);

}
