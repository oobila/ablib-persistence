package com.github.oobila.bukkit.persistence.old.caches.real;

import com.github.oobila.bukkit.persistence.old.adapters.storage.FileStorageAdapter;
import com.github.oobila.bukkit.persistence.old.vehicles.player.PlayerPersistenceVehicle;
import com.github.oobila.bukkit.persistence.old.vehicles.player.PlayerYamlConfigVehicle;
import com.github.oobila.bukkit.persistence.old.caches.standard.PlayerReadAndWriteCache;
import com.github.oobila.bukkit.persistence.old.model.CacheItem;
import com.github.oobila.bukkit.persistence.old.observers.PlayerLoadObserver;
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
                        String.class,
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
