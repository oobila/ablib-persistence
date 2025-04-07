package com.github.oobila.bukkit.persistence.adapters.code;

import com.github.oobila.bukkit.persistence.PersistenceRuntimeException;
import com.github.oobila.bukkit.persistence.adapters.storage.StoredData;
import com.github.oobila.bukkit.persistence.model.Resource;
import com.github.oobila.bukkit.persistence.model.ResourcePack;
import lombok.Getter;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import static com.github.oobila.bukkit.common.ABCommon.log;

@Getter
public class ResourcePackCodeAdapter implements CodeAdapter<ResourcePack> {

    private final Map<Class<?>, CodeAdapter<?>> typeMap = new HashMap<>();
    private final Map<Pattern, CodeAdapter<?>> patternMap = new HashMap<>();

    @Setter
    private Plugin plugin;

    public ResourcePackCodeAdapter(Map<Pattern, CodeAdapter<?>> codeAdapterMap) {
        codeAdapterMap.forEach((string, codeAdapter) -> {
            typeMap.put(codeAdapter.getType(), codeAdapter);
            patternMap.put(string, codeAdapter);
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
        for (Map.Entry<Pattern, CodeAdapter<?>> entry : patternMap.entrySet()) {
            Pattern pattern = entry.getKey();
            if (pattern.matcher(storedData.getName()).matches()) {
                CodeAdapter<?> codeAdapter = entry.getValue();
                Map<String, ?> object = codeAdapter.toObjects(storedData);
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
        for (Map.Entry<Class<?>, CodeAdapter<?>> entry : typeMap.entrySet()) {
            if (entry.getKey().isAssignableFrom(resource.getType())) {
                return new StoredData(
                        resource.getKey(),
                        fromObject(entry.getValue(), resource.getData()),
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

}
