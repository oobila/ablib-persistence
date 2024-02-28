package com.github.oobila.bukkit.persistence.caches;

import com.github.oobila.bukkit.persistence.adapters.PlayerCacheAdapter;
import com.github.oobila.bukkit.persistence.adapters.PlayerFileAdapter;
import com.github.oobila.bukkit.persistence.model.PersistedObject;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.plugin.Plugin;

import java.util.function.Consumer;

public class AsyncPlayerCache <K, V extends PersistedObject> extends BaseCache<K, V>{

    private static final String SUB_FOLDER_NAME = "playerData";

    @Setter
    private PlayerCacheAdapter<K,V> adapter;

    public AsyncPlayerCache(String name, Class<K> keyType, Class<V> type) {
        this(name, keyType, type, new PlayerFileAdapter<>());
    }

    public AsyncPlayerCache(String name, Class<K> keyType, Class<V> type, PlayerCacheAdapter<K, V> adapter) {
        super(name, keyType, type);
        this.adapter = adapter;
    }

    @Override
    public String getSubFolderName() {
        return SUB_FOLDER_NAME;
    }

    public void open(Plugin plugin) {
        this.plugin = plugin;
        adapter.open(this);
    }

    public void open(OfflinePlayer player) {
        adapter.open(player, this);
    }

    public void close(){
        adapter.close(this);
    }

    public void put(OfflinePlayer player, K key, V value, Runnable runnable) {
        Bukkit.getScheduler().runTaskAsynchronously(getPlugin(), () -> {
            adapter.put(player, key, value, this);
            if (runnable != null) {
                runnable.run();
            }
        });
    }

    public void get(OfflinePlayer player, K key, Consumer<V> consumer) {
        Bukkit.getScheduler().runTaskAsynchronously(getPlugin(), () -> {
            V v = adapter.get(player, key, this);
            if (consumer != null) {
                consumer.accept(v);
            }
        });
    }

    public void remove(OfflinePlayer offlinePlayer, K key, Runnable runnable) {
        Bukkit.getScheduler().runTaskAsynchronously(getPlugin(), () -> {
            adapter.remove(offlinePlayer, key, this);
            if (runnable != null) {
                runnable.run();
            }
        });
    }

    public void remove(OfflinePlayer offlinePlayer, Runnable runnable) {
        Bukkit.getScheduler().runTaskAsynchronously(getPlugin(), () -> {
            adapter.remove(offlinePlayer, this);
            if (runnable != null) {
                runnable.run();
            }
        });
    }

}
