package com.github.oobila.bukkit.persistence.adapters.zip;

import com.github.oobila.bukkit.persistence.adapters.CacheReader;
import com.github.oobila.bukkit.persistence.adapters.ResourceFileAdapter;
import com.github.oobila.bukkit.persistence.adapters.utils.AdapterUtils;
import com.github.oobila.bukkit.persistence.model.PersistedObject;
import lombok.RequiredArgsConstructor;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

@RequiredArgsConstructor
public class YamlZipAdapter<V extends PersistedObject> implements ZipEntryAdapter<V> {

    private final CacheReader cacheReader;
    private final Class<V> type;

    @Override
    public <K> V getValue(ZipEntry entry, ZipFile zip, ResourceFileAdapter<K> resourceFileAdapter) {
        try(
                InputStream inputStream = zip.getInputStream(entry);
                InputStreamReader reader = new InputStreamReader(inputStream)
        ) {
            return AdapterUtils.deserializeData(cacheReader, reader, type);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
