package com.github.oobila.bukkit.persistence.adapters.code;

import com.github.oobila.bukkit.persistence.PersistenceRuntimeException;
import com.github.oobila.bukkit.persistence.adapters.storage.StoredData;
import com.github.oobila.bukkit.persistence.model.Resource;
import lombok.Getter;
import lombok.Setter;
import org.apache.logging.log4j.util.Strings;
import org.bukkit.plugin.Plugin;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.regex.Pattern;

import static com.github.oobila.bukkit.common.ABCommon.log;

@Getter
public class ResourcePackCodeAdapter implements CodeAdapter<Resource<?>> {

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
    public Class<Resource<?>> getType() {
        return null;
    }

    @Override
    public Map<String, Resource<?>> toObjects(StoredData storedData) {
        for (Map.Entry<Pattern, CodeAdapter<?>> entry : patternMap.entrySet()) {
            Pattern pattern = entry.getKey();
            if (pattern.matcher(storedData.getName()).matches()) {
                CodeAdapter<?> codeAdapter = entry.getValue();
                Map<String, ?> object = codeAdapter.toObjects(storedData);
                return Map.of(Strings.EMPTY, getResource(storedData, object));
            }
        }
        log(Level.SEVERE, "Class not registered with the ResourcePackCodeAdapter: {0}", storedData.getName());
        throw new PersistenceRuntimeException("Class not registered with the ResourcePackCodeAdapter");
    }

    @Override
    public String fromObjects(Map<String, Resource<?>> map) {
        Resource<?> resource = map.values().iterator().next();
        for (Map.Entry<Class<?>, CodeAdapter<?>> entry : typeMap.entrySet()) {
            if (entry.getKey().isAssignableFrom(resource.getType())) {
                return fromObject(entry.getValue(), resource.getData());
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
