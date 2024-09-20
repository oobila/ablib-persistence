package com.github.oobila.bukkit.persistence.adapters.vehicle;

import com.github.oobila.bukkit.persistence.PersistenceRuntimeException;
import com.github.oobila.bukkit.persistence.adapters.code.CodeAdapter;
import com.github.oobila.bukkit.persistence.adapters.storage.StorageAdapter;
import com.github.oobila.bukkit.persistence.adapters.storage.StoredData;
import com.github.oobila.bukkit.persistence.model.CacheItem;
import com.github.oobila.bukkit.persistence.serializers.Serialization;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;

import static com.github.oobila.bukkit.common.ABCommon.log;
import static com.github.oobila.bukkit.persistence.utils.BackwardsCompatibilityUtil.compatibility;

@SuppressWarnings("unused")
@RequiredArgsConstructor
@Getter
public class PlayerYamlMultiItemVehicle<K, V> extends BasePlayerPersistenceVehicle<K, V> {

    private final Class<K> keyType;
    private final StorageAdapter storageAdapter;
    private final CodeAdapter<V> codeAdapter;

    @Override
    public Map<K, CacheItem<K,V>> loadPlayer(Plugin plugin, String directory, UUID playerId) {
        String playerIdString = Serialization.serialize(playerId);
        Map<K, CacheItem<K,V>> map = new HashMap<>();
        try {
            List<StoredData> storedDataList = storageAdapter.read(
                    plugin,
                    String.format("%s%s/%s", getPlayerDirectory(), playerIdString, directory)
            );
            for (StoredData storedData : storedDataList) {
                YamlConfiguration yamlConfiguration = new YamlConfiguration();
                yamlConfiguration.loadFromString(storedData.getData());
                Map<String, Object> objects = yamlConfiguration.getValues(false);
                objects.forEach((name, object) -> {
                    StoredData item = new StoredData(name, (String) object, 0, storedData.getUpdatedDate());
                    K key = Serialization.deserialize(getKeyType(), name);
                    V value = codeAdapter.toObject(compatibility(this, item));
                    CacheItem<K,V> cacheItem = new CacheItem<>(key, value, storedData);
                    map.put(key, cacheItem);
                });
            }
        } catch (InvalidConfigurationException e) {
            log(
                    Level.SEVERE,
                    "Could not load Yaml from: {}{}/{}",
                    getPlayerDirectory(),
                    playerIdString,
                    directory
            );
            log(Level.SEVERE, e);
            throw new PersistenceRuntimeException(e);
        }
        return map;
    }

    @Override
    public void save(Plugin plugin, String directory, Map<UUID, Map<K, CacheItem<K,V>>> map) {
        map.forEach((playerId, innerMap) ->
                savePlayer(plugin, directory, playerId, innerMap)
        );
    }

    @Override
    public void savePlayer(Plugin plugin, String directory, UUID playerId, Map<K, CacheItem<K, V>> map) {
        YamlConfiguration yamlConfiguration = new YamlConfiguration();
        map.forEach((key, value) -> {
            String name = Serialization.serialize(key);
            String data = codeAdapter.fromObject(value.getData());
            yamlConfiguration.set(name, data);
        });
        String data = yamlConfiguration.saveToString();
        StoredData storedData = new StoredData(directory, data, 0, null);
        storageAdapter.write(
                plugin,
                String.format("%s%s/%s", getPlayerDirectory(), Serialization.serialize(playerId), directory),
                List.of(storedData)
        );
    }

    @Override
    public void saveSingle(Plugin plugin, String directory, UUID playerId, CacheItem<K, V> cacheItem) {
        log(Level.WARNING, "Unsupported operation. saveSingle attempted on PlayerYamlMultiItemVehicle");
    }
}
