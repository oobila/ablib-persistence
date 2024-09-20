package com.github.oobila.bukkit.persistence.caches.real;

import com.github.oobila.bukkit.persistence.adapters.code.StringCodeAdapter;
import com.github.oobila.bukkit.persistence.adapters.storage.FileStorageAdapter;
import com.github.oobila.bukkit.persistence.adapters.vehicle.PlayerPersistenceVehicle;
import com.github.oobila.bukkit.persistence.adapters.vehicle.PlayerYamlMultiItemVehicle;
import com.github.oobila.bukkit.persistence.caches.standard.PlayerReadAndWriteCache;
import com.github.oobila.bukkit.persistence.observers.PlayerLoadObserver;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.plugin.Plugin;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

@SuppressWarnings("unused")
public class MessageCache extends PlayerReadAndWriteCache<ZonedDateTime, String> {

    public MessageCache(Plugin plugin, String name) {
        this(
                plugin,
                name,
                new PlayerYamlMultiItemVehicle<>(
                        ZonedDateTime.class,
                        new FileStorageAdapter("yml"),
                        new StringCodeAdapter()
                )
        );
    }

    public MessageCache(Plugin plugin, String name, PlayerPersistenceVehicle<ZonedDateTime, String> vehicle) {
        super(plugin, name, vehicle);
        addPlayerObserver((PlayerLoadObserver<ZonedDateTime, String>) (playerId, loadedData) ->
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
