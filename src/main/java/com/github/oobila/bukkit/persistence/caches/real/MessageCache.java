package com.github.oobila.bukkit.persistence.caches.real;

import com.github.oobila.bukkit.persistence.adapters.storage.FileStorageAdapter;
import com.github.oobila.bukkit.persistence.adapters.vehicle.PlayerPersistenceVehicle;
import com.github.oobila.bukkit.persistence.adapters.vehicle.PlayerYamlConfigVehicle;
import com.github.oobila.bukkit.persistence.caches.standard.PlayerReadAndWriteCache;
import com.github.oobila.bukkit.persistence.model.CacheItem;
import com.github.oobila.bukkit.persistence.observers.PlayerLoadObserver;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

@SuppressWarnings("unused")
public class MessageCache extends PlayerReadAndWriteCache<ZonedDateTime, String> {

    public MessageCache(String name) {
        this(
                name,
                new PlayerYamlConfigVehicle<>(
                        ZonedDateTime.class,
                        new FileStorageAdapter("yml")
                )
        );
    }

    public MessageCache(
            String name,
            PlayerPersistenceVehicle<ZonedDateTime, String, CacheItem<ZonedDateTime, String>> vehicle
    ) {
        super(name, vehicle);
        addPlayerObserver(
                (PlayerLoadObserver<ZonedDateTime, String, CacheItem<ZonedDateTime, String>>)
                (playerId, loadedData) ->
                        loadedData.entrySet().stream()
                                .sorted(Map.Entry.comparingByKey())
                                .map(entry -> entry.getValue().getData())
                                .forEach(s -> Objects.requireNonNull(Bukkit.getPlayer(playerId)).sendMessage(s))
        );
    }

    public List<String> getMessages(OfflinePlayer offlinePlayer) {
        return getMessages(offlinePlayer.getUniqueId());
    }

    public List<String> getMessages(UUID playerId) {
        return getWithMetadata(playerId).entrySet().stream()
                        .sorted(Map.Entry.comparingByKey())
                        .map(entry -> entry.getValue().getData())
                        .toList();
    }

}
