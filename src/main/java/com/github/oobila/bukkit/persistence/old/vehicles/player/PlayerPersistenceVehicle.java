package com.github.oobila.bukkit.persistence.old.vehicles.player;

import com.github.oobila.bukkit.persistence.old.model.CacheItem;
import com.github.oobila.bukkit.persistence.old.vehicles.temp.PersistenceVehicle;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.Map;


public interface PlayerPersistenceVehicle<K, V, C extends CacheItem<K, V>> extends PersistenceVehicle<K, V, C> {

    void load(Plugin plugin);

    Map<K, C> loadPlayer(Player player);

    void save(Map<Player, Map<K, C>> map);

    void savePlayer(Player player, Map<K, C> map);

    void saveSingle(Player player, C cacheItem);

    default String getPlayerDirectory() {
        return "playerData/";
    }

}
