package com.github.oobila.bukkit.persistence.adapters.code;

import com.github.alastairbooth.placeholderpattern.PlaceholderPattern;
import com.github.oobila.bukkit.persistence.PersistenceRuntimeException;
import com.github.oobila.bukkit.persistence.adapters.storage.StoredData;
import com.github.oobila.bukkit.persistence.model.Resource;
import com.github.oobila.bukkit.persistence.model.ResourcePack;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.logging.log4j.util.Strings;
import org.bukkit.plugin.Plugin;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import static com.github.oobila.bukkit.common.ABCommon.log;

@Getter
public class ResourcePackCodeAdapter implements CodeAdapter<ResourcePack> {

    private final List<Info> infoList = new ArrayList<>();

    @Setter
    private Plugin plugin;

    public ResourcePackCodeAdapter(Map<PlaceholderPattern, CodeAdapter<?>> codeAdapterMap) {
        codeAdapterMap.forEach((placeholderPattern, codeAdapter) -> {
            if (placeholderPattern.getKeys().size() != 1) {
                throw new PersistenceRuntimeException("PlaceholderPattern should have exactly 1 key for the ResourcePackCodeAdapter");
            }
            infoList.add(new Info(
                    codeAdapter.getType(),
                    codeAdapter,
                    placeholderPattern
            ));
        });
    }

    @Override
    public Class<ResourcePack> getType() {
        return null;
    }

    @Override
    public Map<String, ResourcePack> toObjects(StoredData parentStoredData) {
        List<StoredData> storedDataList = new ArrayList<>();
        InputStream inputStream = new ByteArrayInputStream(parentStoredData.getData().getBytes(StandardCharsets.ISO_8859_1));
        ZipInputStream zipInputStream = new ZipInputStream(inputStream);
        ZipEntry zipEntry;
        try {
            while ((zipEntry = zipInputStream.getNextEntry()) != null) {
                if (!zipEntry.isDirectory()) {
                    storedDataList.add(getStoredData(zipInputStream, zipEntry));
                }
            }
        } catch (IOException e) {
            log(Level.SEVERE, "Failed reading zip: {0}", parentStoredData.getName());
            log(Level.SEVERE, e);
            throw new PersistenceRuntimeException(e);
        }

        ResourcePack resourcePack = new ResourcePack(parentStoredData.getName());
        storedDataList.forEach(entryStoredData ->
            resourcePack.put(entryStoredData.getName(), toResource(entryStoredData))
        );
        return Map.of(Strings.EMPTY, resourcePack);
    }

    private Resource<?> toResource(StoredData storedData) {
        for (Info info : infoList) {
            if (info.placeholderPattern.matches(storedData.getName())) {
                Map<String, ?> object = info.codeAdapter.toObjects(storedData);
                return getResource(storedData, object);
            }
        }
        log(Level.SEVERE, "Class not registered with the ResourcePackCodeAdapter: {0}", storedData.getName());
        throw new PersistenceRuntimeException("Class not registered with the ResourcePackCodeAdapter");
    }

    private StoredData getStoredData(ZipInputStream zipInputStream, ZipEntry entry) {
        String name = entry.getName();
        long size = entry.getSize();
        try {
            String data = new String(zipInputStream.readAllBytes(), StandardCharsets.ISO_8859_1);
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
    public String fromObjects(Map<String, ResourcePack> map) {
        ResourcePack resourcePack = map.values().iterator().next();
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        try (ZipOutputStream zipOutputStream = new ZipOutputStream(byteArrayOutputStream)) {
            for (Map.Entry<String, Resource<?>> entry : resourcePack.entrySet()) {
                StoredData storedData = toStoredData(entry.getValue());
                ZipEntry zipEntry = new ZipEntry(storedData.getName());
                zipOutputStream.putNextEntry(zipEntry);
                zipOutputStream.write(storedData.getData().getBytes(StandardCharsets.ISO_8859_1));
            }
        } catch (IOException e) {
            log(Level.SEVERE, "Failed writing zip: {0}", resourcePack.getName());
            log(Level.SEVERE, e);
            throw new PersistenceRuntimeException(e);
        }
        return byteArrayOutputStream.toString(StandardCharsets.ISO_8859_1);
    }

    private StoredData toStoredData(Resource<?> resource) {
        for (Info info : infoList) {
            if (info.type.isAssignableFrom(resource.getType())) {
                String key = resource.getKey();
                if (!info.placeholderPattern.matches(key)) {
                    key = info.placeholderPattern.getWithReplacements(info.placeholderPattern.getKeys().get(0), key);
                }
                return new StoredData(
                        key,
                        fromObject(info.codeAdapter, resource.getData()),
                        0,
                        null
                );
            }
        }
        log(Level.SEVERE, "Class not registered with the ResourcePackCodeAdapter: {0}", resource.getType().getName());
        throw new PersistenceRuntimeException("Class not registered with the ResourcePackCodeAdapter");
    }

    @SuppressWarnings("unchecked")
    private <T> Resource<T> getResource(StoredData storedData, T object) {
        return new Resource<>(
                (Class<T>) object.getClass(),
                storedData.getName(),
                object,
                storedData.getSize(),
                storedData.getUpdatedDate()
        );
    }

    @SuppressWarnings("unchecked")
    private <T> String fromObject(CodeAdapter<T> codeAdapter, Object object) {
        return codeAdapter.fromObjects(Map.of(Strings.EMPTY, (T) object));
    }

    @AllArgsConstructor
    @NoArgsConstructor
    private class Info {
        Class<?> type;
        CodeAdapter<?> codeAdapter;
        PlaceholderPattern placeholderPattern;
    }

}
