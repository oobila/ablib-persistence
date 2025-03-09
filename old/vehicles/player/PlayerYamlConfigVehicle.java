package com.github.oobila.bukkit.persistence.old.vehicles.player;

import com.github.oobila.bukkit.persistence.PersistenceRuntimeException;
import com.github.oobila.bukkit.persistence.old.adapters.code.DummyCodeAdapter;
import com.github.oobila.bukkit.persistence.old.adapters.storage.StorageAdapter;
import com.github.oobila.bukkit.persistence.old.adapters.storage.StoredData;
import com.github.oobila.bukkit.persistence.utils.MyYamlConfiguration;
import com.github.oobila.bukkit.persistence.old.model.CacheItem;
import com.github.oobila.bukkit.persistence.serializers.Serialization;
import lombok.Getter;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import static com.github.oobila.bukkit.common.ABCommon.log;
import static com.github.oobila.bukkit.persistence.old.vehicles.utils.DirectoryUtils.playerDir;
import static com.github.oobila.bukkit.persistence.old.vehicles.utils.BackwardsCompatibilityUtil.compatibility;

@SuppressWarnings("unused")
@Getter
public class PlayerYamlConfigVehicle<K, V, C extends CacheItem<K, V>> extends BasePlayerPersistenceVehicle<K, V, C> {

    private final StorageAdapter storageAdapter;
    private final DummyCodeAdapter<V> codeAdapter;

    public PlayerYamlConfigVehicle(Class<K> keyType, Class<V> valueType, StorageAdapter storageAdapter) {
        super(keyType);
        this.storageAdapter = storageAdapter;
        this.codeAdapter = new DummyCodeAdapter<>(valueType);
    }

    @Override
    public void load(Plugin plugin) {
        //do nothing for YAML config vehicle
    }

    @SuppressWarnings("unchecked")
    @Override
    public Map<K, C> loadPlayer(Player player) {
        Map<K, C> map = new HashMap<>();
        try {
            List<StoredData> storedDataList = storageAdapter.read(
                    getPlugin(),
                    playerDir(getPlayerDirectory(), player.getUniqueId(), getCache().getName())
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
                            getCodeAdapter().getType(),
                            key,
                            value,
                            storedData.getSize(),
                            storedData.getUpdatedDate(),
                            player.getUniqueId()
                    );
                    map.put(key, cacheItem);
                }
            }
        } catch (InvalidConfigurationException e) {
            log(
                    Level.SEVERE,
                    "Could not load Yaml from: {0}{1}/{2}",
                    getPlayerDirectory(),
                    Serialization.serialize(player.getUniqueId()),
                    getCache().getName()
            );
            log(Level.SEVERE, e);
            throw new PersistenceRuntimeException(e);
        }
        return map;
    }

    @Override
    public void save(Map<Player, Map<K, C>> map) {
        map.forEach(this::savePlayer);
    }

    @Override
    public void savePlayer(Player player, Map<K, C> map) {
        MyYamlConfiguration yamlConfiguration = new MyYamlConfiguration();
        map.forEach((key, value) -> {
            String name = Serialization.serialize(key);
            yamlConfiguration.set(name, value.getData());
        });
        String data = yamlConfiguration.saveToString();
        StoredData storedData = new StoredData(getCache().getName(), data, 0, null);
        storageAdapter.write(
                getPlugin(),
                playerDir(getPlayerDirectory(), player.getUniqueId(), getCache().getName()),
                List.of(storedData)
        );
    }

    @Override
    public void saveSingle(Player player, C cacheItem) {
        log(Level.WARNING, "Unsupported operation. saveSingle attempted on PlayerYamlMultiItemVehicle");
    }
}