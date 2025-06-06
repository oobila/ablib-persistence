package com.github.oobila.bukkit.persistence.adapters.storage;

import com.github.oobila.bukkit.persistence.PersistenceRuntimeException;
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
import java.nio.file.attribute.FileTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.logging.Level;

import static com.github.oobila.bukkit.common.ABCommon.log;

@RequiredArgsConstructor
@Getter
public class FileStorageAdapter implements StorageAdapter {

    @Override
    public List<StoredData> read(Plugin plugin, String fileName) {
        try {
            Path path = getPath(plugin, fileName);
            if (!Files.exists(path)) {
                return Collections.emptyList();
            }
            String data = Files.readString(path, StandardCharsets.ISO_8859_1);
            BasicFileAttributes attributes = Files.readAttributes(path, BasicFileAttributes.class);
            return Collections.singletonList(new StoredData(
                    FilenameUtils.getBaseName(fileName),
                    data,
                    attributes.size(),
                    attributes.lastModifiedTime().toInstant().atZone(ZoneId.systemDefault())
            ));
        } catch (IOException e) {
            log(Level.SEVERE, "Could not read contents of file: {0}", fileName);
            log(Level.SEVERE, e);
            throw new PersistenceRuntimeException(e);
        }
    }

    @Override
    public List<StoredData> readMetaData(Plugin plugin, String fileName) {
        try {
            Path path = getPath(plugin, fileName);
            if (!Files.exists(path)) {
                return Collections.emptyList();
            }
            BasicFileAttributes attributes = Files.readAttributes(path, BasicFileAttributes.class);
            return Collections.singletonList(new StoredData(
                    FilenameUtils.getBaseName(fileName),
                    null,
                    attributes.size(),
                    attributes.lastModifiedTime().toInstant().atZone(ZoneId.systemDefault())
            ));
        } catch (Exception e) {
            log(Level.SEVERE, "Could not read metadata of file: {0}", fileName);
            log(Level.SEVERE, e);
            throw new PersistenceRuntimeException(e);
        }
    }

    @Override
    public List<String> poll(Plugin plugin, String fileName) {
        Path path = getPath(plugin, fileName);
        File file = path.toFile();
        String extension = FilenameUtils.getExtension(fileName);
        if (extension != null && !extension.isEmpty()) {
            sneakyForceMkdir(path.getParent().toFile());
            if (file.exists()) {
                return List.of(fileName);
            } else {
                return Collections.emptyList();
            }
        } else {
            sneakyForceMkdir(path.toFile());
            if (Objects.requireNonNull(file.listFiles()).length > 0) {
                return Arrays.stream(Objects.requireNonNull(file.listFiles()))
                        .map(File::getName)
                        .toList();
            } else {
                return Collections.emptyList();
            }
        }
    }

    @Override
    public void write(Plugin plugin, String fileName, List<StoredData> storedDataList) {
        if (storedDataList.size() != 1) {
            log(Level.SEVERE, "Unsupported operation, tried writing {0} items but this method only allows 1",
                    storedDataList.size());
            throw new PersistenceRuntimeException("unsupported operation");
        }
        try {
            Path path = getPath(plugin, fileName);
            sneakyDelete(path.toFile());
            sneakyForceMkdir(path.getParent().toFile());
            Files.writeString(path, storedDataList.get(0).getData(), StandardCharsets.ISO_8859_1,StandardOpenOption.CREATE_NEW);
        } catch (IOException e) {
            log(Level.SEVERE, "Could not write contents to file: {0}", fileName);
            log(Level.SEVERE, e);
            throw new PersistenceRuntimeException(e);
        }
    }

    @Override
    public void copyDefaults(Plugin plugin, String fileName) {
        Path path = getPath(plugin, fileName);
        sneakyForceMkdir(path.getParent().toFile());
        try (InputStream inputStream = plugin.getResource(fileName);
             OutputStream outputStream = new FileOutputStream(path.toFile())) {
            if (inputStream != null) {
                log(Level.INFO, "Copying defaults for: {0}", fileName);
                byte[] buffer = new byte[1024];
                int len;
                while((len = inputStream.read(buffer))>0) {
                    outputStream.write(buffer,0,len);
                }
            }
        } catch (IOException e) {
            log(Level.SEVERE, "Could not copy defaults for: {0}", fileName);
            log(Level.SEVERE, e);
            throw new PersistenceRuntimeException(e);
        }
    }

    @Override
    public void delete(Plugin plugin, String name) {
        File file = new File(plugin.getDataFolder(), name);
        sneakyDelete(file);
    }

    @Override
    public boolean exists(Plugin plugin, String name) {
        Path path = getPath(plugin, name);
        return path.toFile().exists();
    }

    @Override
    public ZonedDateTime getLastUpdated(Plugin plugin, String fileName) {
        try {
            FileTime fileTime = Files.getLastModifiedTime(getPath(plugin, fileName));
            return ZonedDateTime.ofInstant(fileTime.toInstant(), ZoneId.systemDefault());
        } catch (IOException e) {
            throw new PersistenceRuntimeException(e);
        }
    }

    protected Path getPath(Plugin plugin, String directory) {
        return new File(plugin.getDataFolder(), directory).toPath();
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
