package com.github.oobila.bukkit.persistence.old.vehicles.pollmethod;

import com.github.oobila.bukkit.persistence.old.adapters.storage.StorageAdapter;
import com.github.oobila.bukkit.persistence.old.model.CacheItem;
import com.github.oobila.bukkit.persistence.serializers.Serialization;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.bukkit.OfflinePlayer;
import org.bukkit.plugin.Plugin;

import java.util.List;

import static com.github.oobila.bukkit.persistence.old.vehicles.utils.DirectoryUtils.append;

@RequiredArgsConstructor
public class PlayerPollMethod implements PollMethod<OfflinePlayer> {

    private final String path;
    @Setter
    private Plugin plugin;

    @Override
    public List<String> getPaths(StorageAdapter storageAdapter, OfflinePlayer player) {
        return storageAdapter.poll(plugin, path);
    }

    @Override
    public String getPath(StorageAdapter storageAdapter, OfflinePlayer player, CacheItem<?, ?> cacheItem) {
        return storageAdapter.poll(plugin, append(path, Serialization.serialize(cacheItem.getKey()))).get(0);
    }
}
