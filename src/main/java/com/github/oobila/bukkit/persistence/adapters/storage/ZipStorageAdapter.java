package com.github.oobila.bukkit.persistence.adapters.storage;

import com.github.oobila.bukkit.persistence.PersistenceRuntimeException;
import org.bukkit.plugin.Plugin;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.logging.Level;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

import static com.github.oobila.bukkit.common.ABCommon.log;

public class ZipStorageAdapter extends FileStorageAdapter {

    @Override
    public List<StoredData> read(Plugin plugin, String name) {
        List<StoredData> storedDataList = new ArrayList<>();
        Path path = getPath(plugin, name);
        try (ZipFile zip = new ZipFile(path.toFile())) {
            for (Enumeration<? extends ZipEntry> zipEntries = zip.entries(); zipEntries.hasMoreElements(); ) {
                ZipEntry entry = zipEntries.nextElement();
                if (!entry.isDirectory()) {
                    storedDataList.add(getStoredData(zip, entry));
                }
            }
        } catch (IOException e) {
            log(Level.SEVERE, "Failed reading zip: {0}", name);
            log(Level.SEVERE, e);
            throw new PersistenceRuntimeException(e);
        }
        return storedDataList;
    }

    private StoredData getStoredData(ZipFile zip, ZipEntry entry) {
        String name = entry.getName();
        long size = entry.getSize();
        try(InputStream inputStream = zip.getInputStream(entry)) {
            String data = new String(inputStream.readAllBytes(), StandardCharsets.ISO_8859_1);
            return new StoredData(
                    name,
                    data,
                    size,
                    ZonedDateTime.ofInstant(entry.getLastModifiedTime().toInstant(), ZoneId.systemDefault())
            );
        } catch (IOException e) {
            log(Level.SEVERE, "Failed reading zip entry: {0}", name);
            log(Level.SEVERE, e);
            throw new PersistenceRuntimeException(e);
        }
    }

    @Override
    public void write(Plugin plugin, String name, List<StoredData> storedDataList) {
        Path path = getPath(plugin, name);
        if (path.toFile().exists()) {
            sneakyDelete(path.toFile());
        }
        try (ZipOutputStream zipOutputStream = new ZipOutputStream(new FileOutputStream(path.toFile()))) {
            for (StoredData storedData : storedDataList) {
                ZipEntry zipEntry = new ZipEntry(storedData.getName());
                zipOutputStream.putNextEntry(zipEntry);
                zipOutputStream.write(storedData.getData().getBytes(StandardCharsets.ISO_8859_1));
            }
            zipOutputStream.flush();
        } catch (IOException e) {
            log(Level.SEVERE, "Failed writing zip: {0}", name);
            log(Level.SEVERE, e);
            throw new PersistenceRuntimeException(e);
        }
    }
}
