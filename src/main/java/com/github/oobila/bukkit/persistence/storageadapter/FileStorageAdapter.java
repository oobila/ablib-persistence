package com.github.oobila.bukkit.persistence.storageadapter;

import com.github.oobila.bukkit.persistence.PersistenceRuntimeException;
import com.github.oobila.bukkit.persistence.codeadapter.model.StoredData;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
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
public class FileStorageAdapter extends BaseStorageAdapter {

    @Override
    public void write(String path, StoredData data) {
        try {
            Path filePath = getPath(getPlugin(), path);
            sneakyDelete(filePath.toFile());
            sneakyForceMkdir(filePath.getParent().toFile());
            Files.writeString(filePath, data.getData(), StandardCharsets.ISO_8859_1, StandardOpenOption.CREATE_NEW);
        } catch (IOException e) {
            log(Level.SEVERE, "Could not write contents to file: {0}", path);
            log(Level.SEVERE, e);
            throw new PersistenceRuntimeException(e);
        }
    }

    @Override
    public List<String> poll(String path) {
        File file = new File(getPlugin().getDataFolder(), path);
        sneakyForceMkdir(file);
        if (Objects.requireNonNull(file.listFiles()).length > 0) {
            return Arrays.stream(Objects.requireNonNull(file.listFiles()))
                    .map(f -> FilenameUtils.getBaseName(f.getName()))
                    .toList();
        } else {
            return Collections.emptyList();
        }
    }

    @Override
    public StoredData read(String path) {
        try {
            Path filePath = getPath(getPlugin(), path);
            if (!Files.exists(filePath)) {
                return null;
            }
            String data = Files.readString(filePath, StandardCharsets.ISO_8859_1);
            BasicFileAttributes attributes = Files.readAttributes(filePath, BasicFileAttributes.class);
            return new StoredData(
                    path,
                    data,
                    attributes.size(),
                    attributes.lastModifiedTime().toInstant().atZone(ZoneId.systemDefault())
            );
        } catch (IOException e) {
            log(Level.SEVERE, "Could not read contents of file: {0}", path);
            log(Level.SEVERE, e);
            throw new PersistenceRuntimeException(e);
        }
    }

    @Override
    public StoredData readMetadata(String path) {
        try {
            Path filePath = getPath(getPlugin(), path);
            if (!Files.exists(filePath)) {
                return null;
            }
            BasicFileAttributes attributes = Files.readAttributes(filePath, BasicFileAttributes.class);
            return new StoredData(
                    path,
                    null,
                    attributes.size(),
                    attributes.lastModifiedTime().toInstant().atZone(ZoneId.systemDefault())
            );
        } catch (IOException e) {
            log(Level.SEVERE, "Could not read metadata of file: {0}", path);
            log(Level.SEVERE, e);
            throw new PersistenceRuntimeException(e);
        }
    }

    @Override
    public void delete(String path) {
        File file = new File(getPlugin().getDataFolder(), path);
        sneakyDelete(file);
    }

    public void copyDefaults(String path) {
        Path filePath = getPath(getPlugin(), path);
        sneakyForceMkdir(filePath.getParent().toFile());
        try (InputStream inputStream = getPlugin().getResource(path);
             OutputStream outputStream = new FileOutputStream(filePath.toFile())) {
            if (inputStream != null) {
                log(Level.INFO, "Copying defaults for: {0}", path);
                byte[] buffer = new byte[1024];
                int len;
                while((len = inputStream.read(buffer))>0) {
                    outputStream.write(buffer,0,len);
                }
            }
        } catch (IOException e) {
            log(Level.SEVERE, "Could not copy defaults for: {0}", path);
            log(Level.SEVERE, e);
            throw new PersistenceRuntimeException(e);
        }
    }

    protected Path getPath(Plugin plugin, String path) {
        return new File(plugin.getDataFolder(), path).toPath();
    }

    protected void sneakyDelete(File file) {
        try {
            FileUtils.delete(file);
        } catch (IOException e) {
            //do nothing
        }
    }

    protected void sneakyForceMkdir(File file) {
        try {
            FileUtils.forceMkdir(file);
        } catch (IOException e) {
            //do nothing
        }
    }

}
