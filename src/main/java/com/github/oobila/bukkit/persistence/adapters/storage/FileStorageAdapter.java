package com.github.oobila.bukkit.persistence.adapters.storage;

import com.github.oobila.bukkit.persistence.PersistenceRuntimeException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.apache.commons.io.FilenameUtils;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.logging.Level;

import static com.github.oobila.bukkit.common.ABCommon.log;

@RequiredArgsConstructor
@Getter
public class FileStorageAdapter implements StorageAdapter {

    private final String extension;

    @Override
    public List<StoredData> read(Plugin plugin, String directory) {
        try {
            Path path = getPath(plugin, directory);
            String name = FilenameUtils.getBaseName(path.toString());
            String data = Files.readString(path);
            BasicFileAttributes attributes = Files.readAttributes(path, BasicFileAttributes.class);
            return Collections.singletonList(new StoredData(
                    name,
                    data,
                    attributes.size(),
                    attributes.lastModifiedTime().toInstant().atZone(ZoneId.systemDefault())
            ));
        } catch (IOException e) {
            log(Level.SEVERE, "Could not read contents of file: {}", directory);
            log(Level.SEVERE, e);
            throw new PersistenceRuntimeException(e);
        }
    }

    @Override
    public List<String> poll(Plugin plugin, String directory) {
        File file = new File(plugin.getDataFolder(), directory);
        if (Objects.requireNonNull(file.listFiles()).length > 0) {
            return Arrays.stream(Objects.requireNonNull(file.listFiles()))
                    .map(f -> FilenameUtils.getBaseName(f.getName()))
                    .toList();
        } else {
            return Collections.emptyList();
        }
    }

    @Override
    public void write(Plugin plugin, String directory, List<StoredData> storedDataList) {
        if (storedDataList.size() != 1) {
            log(Level.SEVERE, "Unsupported operation, tried writing {} items but this method only allows 1",
                    storedDataList.size());
            throw new PersistenceRuntimeException("unsupported operation");
        }
        try {
            Path path = getPath(plugin, directory);
            Files.delete(path);
            Files.writeString(path, storedDataList.get(0).getData(), StandardOpenOption.WRITE);
        } catch (IOException e) {
            log(Level.SEVERE, "Could not write contents to file: {}", directory);
            log(Level.SEVERE, e);
            throw new PersistenceRuntimeException(e);
        }
    }

    @Override
    public void copyDefaults(Plugin plugin, String directory) {
        Path path = getPath(plugin, directory);
        try (InputStream inputStream = plugin.getResource(directory);
             OutputStream outputStream = new FileOutputStream(path.toFile())) {
            if (inputStream != null) {
                log(Level.INFO, "Copying defaults for: {}", directory);
                byte[] buffer = new byte[1024];
                int len;
                while((len = inputStream.read(buffer))>0) {
                    outputStream.write(buffer,0,len);
                }
            }
        } catch (IOException e) {
            log(Level.SEVERE, "Could not copy defaults for: {}", directory);
            log(Level.SEVERE, e);
            throw new PersistenceRuntimeException(e);
        }
    }

    @Override
    public boolean exists(Plugin plugin, String directory) {
        Path path = getPath(plugin, directory);
        return path.toFile().exists();
    }

    protected Path getPath(Plugin plugin, String directory) {
        return new File(plugin.getDataFolder(), String.format("%s.%s", directory, getExtension())).toPath();
    }
}
