package com.github.oobila.bukkit.persistence.caches;

import com.github.oobila.bukkit.persistence.adapters.PlayerCacheAdapter;
import com.github.oobila.bukkit.persistence.adapters.PlayerFileAdapter;
import com.github.oobila.bukkit.persistence.model.PersistedObject;
import lombok.Setter;
import org.bukkit.OfflinePlayer;
import org.bukkit.plugin.Plugin;

public class PlayerCache<K, V extends PersistedObject> extends BaseCache<K,V> implements IPlayerCache<K, V> {

    private static final String SUB_FOLDER_NAME = "playerData";

    @Setter
    private PlayerCacheAdapter<K,V> adapter;

    public PlayerCache(String name, Class<K> keyType, Class<V> type) {
        this(name, keyType, type, new PlayerFileAdapter<>());
    }

    public PlayerCache(String name, Class<K> keyType, Class<V> type, PlayerCacheAdapter<K, V> adapter) {
        super(name, keyType, type);
        this.adapter = adapter;
    }

    @Override
    public String getSubFolderName() {
        return SUB_FOLDER_NAME;
    }

    @Override
    public void open(Plugin plugin) {
        this.plugin = plugin;
        adapter.open(this);
    }

    @Override
    public void open(OfflinePlayer player) {
        adapter.open(player, this);
    }

    @Override
    public void close(){
        adapter.close(this);
    }

    @Override
    public void put(OfflinePlayer player, K key, V value) {
        adapter.put(player, key, value, this);
    }

    @Override
    public V get(OfflinePlayer player, K key) {
        return adapter.get(player, key, this);
    }

    @Override
    public void remove(OfflinePlayer offlinePlayer, K key) {
        adapter.remove(offlinePlayer, key, this);
    }

    @Override
    public void remove(OfflinePlayer offlinePlayer) {
        adapter.remove(offlinePlayer, this);
    }

}