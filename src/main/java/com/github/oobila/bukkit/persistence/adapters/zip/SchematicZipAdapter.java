package com.github.oobila.bukkit.persistence.adapters.zip;

import com.github.oobila.bukkit.persistence.adapters.ResourceFileAdapter;
import com.github.oobila.bukkit.persistence.adapters.utils.WorldEditAdapterUtils;
import com.github.oobila.bukkit.persistence.model.SchematicObject;
import org.apache.commons.io.FilenameUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class SchematicZipAdapter implements ZipEntryAdapter<SchematicObject> {

    @Override
    public <K> SchematicObject getValue(ZipEntry entry, ZipFile zip, ResourceFileAdapter<K> resourceFileAdapter) {
        try (InputStream inputStream = zip.getInputStream(entry)) {
            return WorldEditAdapterUtils.loadSchematic(inputStream, null);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean qualifierMatches(String qualifier) {
        return FilenameUtils.getExtension(qualifier).equalsIgnoreCase("schem");
    }
}
