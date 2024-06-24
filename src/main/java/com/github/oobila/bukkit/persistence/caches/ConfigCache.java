package com.github.oobila.bukkit.persistence.caches;

import com.github.oobila.bukkit.chat.Message;
import com.github.oobila.bukkit.persistence.adapters.ConfigCacheAdapter;
import com.github.oobila.bukkit.persistence.adapters.ConfigFileAdapter;
import com.github.oobila.bukkit.persistence.adapters.utils.FileAdapterUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import static com.github.oobila.bukkit.common.ABCommon.log;

public class ConfigCache<K, V> extends BaseCache<K, V> {

    private static long scheduledTaskId = 0;
    private static int reloadIterator;
    private static final List<ConfigCache<?, ?>> configCaches = new ArrayList<>();
    private final ConfigCacheAdapter<K,V> adapter = new ConfigFileAdapter<>();
    private long lastModified = 0;
    private List<Runnable> observers = new ArrayList<>();

    public ConfigCache(String name, Class<K> keyType, Class<V> type) {
        super(name, keyType, type);
    }

    @Override
    public String getSubFolderName() {
        return null;
    }

    @Override
    public void onOpen(Plugin plugin) {
        this.plugin = plugin;
        adapter.open(this);
        observers.forEach(Runnable::run);

        if (!configCaches.contains(this)) {
            configCaches.add(this);
            if (scheduledTaskId == 0) {
                startFileListener(plugin);
            }
        }
    }

    @Override
    public void onClose() {
        //do nothing
    }

    public void addObserver(Runnable r) {
        observers.add(r);
    }

    public V get(K key) {
        return adapter.get(key);
    }

    public List<K> keys() {
        return adapter.keys();
    }

    public List<V> values() {
        return adapter.values();
    }

    public LocalDateTime lastUpdated() {
        return adapter.getLastUpdated();
    }

    private static void startFileListener(Plugin plugin) {
        Bukkit.getScheduler().runTaskTimer(plugin,() -> {
            reloadIterator++;
            if (reloadIterator >= configCaches.size()) {
                reloadIterator = 0;
            }

            ConfigCache<?, ?> cache = configCaches.get(reloadIterator);

            File file = FileAdapterUtils.getSaveFile(cache, null);
            if (cache.lastModified == 0) {
                cache.lastModified = file.lastModified();
            } else if (file.lastModified() != cache.lastModified) {
                cache.lastModified = file.lastModified();
                cache.open(cache.getPlugin());
                Bukkit.getOnlinePlayers().stream().filter(Player::isOp).forEach(op ->
                        Message.builder("Config {0} was reloaded for plugin: {1}")
                                .arg(cache.getName())
                                .arg(cache.getPlugin().getName())
                                .send(op)
                );
                log(Level.INFO, "Config {0} was reloaded for plugin: {1}" , cache.getName(), cache.getPlugin().getName());
            }
        },1000,100);
    }

}
