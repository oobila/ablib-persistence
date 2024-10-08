package com.github.oobila.bukkit.persistence.adapters.vehicle;

import com.github.oobila.bukkit.persistence.PersistenceRuntimeException;
import com.github.oobila.bukkit.persistence.adapters.code.DummyCodeAdapter;
import com.github.oobila.bukkit.persistence.adapters.storage.StorageAdapter;
import com.github.oobila.bukkit.persistence.adapters.storage.StoredData;
import com.github.oobila.bukkit.persistence.adapters.utils.MyYamlConfiguration;
import com.github.oobila.bukkit.persistence.model.CacheItem;
import com.github.oobila.bukkit.persistence.serializers.Serialization;
import lombok.Getter;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.plugin.Plugin;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;

import static com.github.oobila.bukkit.common.ABCommon.log;
import static com.github.oobila.bukkit.persistence.adapters.utils.DirectoryUtils.playerDir;
import static com.github.oobila.bukkit.persistence.utils.BackwardsCompatibilityUtil.compatibility;

@SuppressWarnings("unused")
@Getter
public class PlayerYamlConfigVehicle<K, V, C extends CacheItem<K, V>>
        extends BasePlayerPersistenceVehicle<K, V, C> {

    private final Class<K> keyType;
    private final StorageAdapter storageAdapter;
    private final DummyCodeAdapter<V> codeAdapter;

    public PlayerYamlConfigVehicle(Class<K> keyType, Class<V> valueType, StorageAdapter storageAdapter) {
        this.keyType = keyType;
        this.storageAdapter = storageAdapter;
        this.codeAdapter = new DummyCodeAdapter<>(valueType);
    }

    @SuppressWarnings("unchecked")
    @Override
    public Map<K, C> loadPlayer(Plugin plugin, String directory, UUID playerId) {
        Map<K, C> map = new HashMap<>();
        try {
            List<StoredData> storedDataList = storageAdapter.read(
                    plugin,
                    playerDir(getPlayerDirectory(), playerId, directory)
            );
            for (StoredData storedData : storedDataList) {
                storedData = compatibility(this, storedData);
                MyYamlConfiguration yamlConfiguration = new MyYamlConfiguration();
                yamlConfiguration.loadFromString(storedData.getData());
                Map<String, Object> objects = yamlConfiguration.getValues(false);
                for (Map.Entry<String, Object> entry : objects.entrySet()) {
                    K key = Serialization.deserialize(getKeyType(), entry.getKey());
                    V value = (V) entry.getValue();
                    C cacheItem = (C) new CacheItem<>(
                            getCodeAdapter().getType(), key, value, storedData
                    );
                    map.put(key, cacheItem);
                }
            }
        } catch (InvalidConfigurationException e) {
            log(
                    Level.SEVERE,
                    "Could not load Yaml from: {0}{1}/{2}",
                    getPlayerDirectory(),
                    Serialization.serialize(playerId),
                    directory
            );
            log(Level.SEVERE, e);
            throw new PersistenceRuntimeException(e);
        }
        return map;
    }

    @Override
    public void save(Plugin plugin, String directory, Map<UUID, Map<K, C>> map) {
        map.forEach((playerId, innerMap) ->
                savePlayer(plugin, directory, playerId, innerMap)
        );
    }

    @Override
    public void savePlayer(Plugin plugin, String directory, UUID playerId, Map<K, C> map) {
        MyYamlConfiguration yamlConfiguration = new MyYamlConfiguration();
        map.forEach((key, value) -> {
            String name = Serialization.serialize(key);
            yamlConfiguration.set(name, value.getData());
        });
        String data = yamlConfiguration.saveToString();
        StoredData storedData = new StoredData(directory, data, 0, null);
        storageAdapter.write(
                plugin,
                playerDir(getPlayerDirectory(), playerId, directory),
                List.of(storedData)
        );
    }

    @Override
    public void saveSingle(Plugin plugin, String directory, UUID playerId, C cacheItem) {
        log(Level.WARNING, "Unsupported operation. saveSingle attempted on PlayerYamlMultiItemVehicle");
    }
}