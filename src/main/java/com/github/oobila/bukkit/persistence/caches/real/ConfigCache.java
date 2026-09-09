package com.github.oobila.bukkit.persistence.caches.real;

import com.github.alastairbooth.abid.ABID;
import com.github.oobila.bukkit.chat.Message;
import com.github.oobila.bukkit.persistence.adapters.code.MapOfConfigurationSerializableCodeAdapter;
import com.github.oobila.bukkit.persistence.adapters.storage.ConfigStorageAdapter;
import com.github.oobila.bukkit.persistence.adapters.vehicle.DynamicVehicle;
import com.github.oobila.bukkit.persistence.caches.standard.ReadOnlyCache;
import com.github.oobila.bukkit.persistence.serializers.Serialization;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.util.Objects;
import java.util.UUID;
import java.util.logging.Level;

import static com.github.oobila.bukkit.common.ABCommon.log;
import static com.github.oobila.bukkit.common.ABCommon.runContinuousTask;

/**
 * A read-only cache for a plugin's config file, with typed convenience getters
 * ({@link #getString}, {@link #getLocation}, etc.) and automatic hot-reloading: it polls the
 * backing file's last-modified time and reloads itself (notifying online operators) whenever it
 * changes on disk, so editing the config while the server is running takes effect without a
 * restart. Uses {@link com.github.oobila.bukkit.persistence.adapters.storage.ConfigStorageAdapter}
 * under the hood, which also merges in newly-added default keys from the plugin's bundled config.
 */
@SuppressWarnings("unused")
public class ConfigCache extends ReadOnlyCache<String, Object> {

    private int fileListenerTask = -1;
    private ZonedDateTime dateTime = ZonedDateTime.now();

    /** @param pathString the config file's path relative to the plugin's data folder, e.g. {@code "config.yml"} */
    public ConfigCache(String pathString) {
        super(
                new DynamicVehicle<>(
                        pathString,
                        false,
                        String.class,
                        new ConfigStorageAdapter(),
                        new MapOfConfigurationSerializableCodeAdapter<>(Object.class)
                )
        );
    }

    @Override
    public void load(Plugin plugin) {
        super.load(plugin);
        startFileListener(plugin);
    }

    public String getString(String key) {
        return (String) getValue(key);
    }

    public int getInt(String key) {
        return (int) getValue(key);
    }

    public double getDouble(String key) {
        return (double) getValue(key);
    }

    public float getFloat(String key) {
        return (float) getValue(key);
    }

    public boolean getBoolean(String key) {
        return (boolean) getValue(key);
    }

    public LocalDate getDate(String key) {
        return Serialization.deserialize(LocalDate.class, (String) getValue(key));
    }

    public ZonedDateTime getDateTime(String key) {
        return Serialization.deserialize(ZonedDateTime.class, (String) getValue(key));
    }

    public LocalTime getTime(String key) {
        return Serialization.deserialize(LocalTime.class, (String) getValue(key));
    }

    public UUID getUuid(String key) {
        return Serialization.deserialize(UUID.class, (String) getValue(key));
    }

    public ABID getAbid(String key) {
        return Serialization.deserialize(ABID.class, (String) getValue(key));
    }

    public OfflinePlayer getOfflinePlayer(String key) {
        return Serialization.deserialize(OfflinePlayer.class, (String) getValue(key));
    }

    public World getWorld(String key) {
        return Serialization.deserialize(World.class, (String) getValue(key));
    }

    public Location getLocation(String key) {
        return Serialization.deserialize(Location.class, (String) getValue(key));
    }

    public Material getMaterial(String key) {
        return Objects.requireNonNull(Material.getMaterial((String) getValue(key)));
    }

    /** Starts (once) a repeating task that reloads this config whenever its file's last-modified time advances. */
    private void startFileListener(Plugin plugin) {
        if (fileListenerTask < 0) {
            BukkitTask bukkitTask = runContinuousTask(() -> {
                ZonedDateTime newTime = getWriteVehicle().getStorageAdapter().getLastUpdated(plugin, getPathString());
                if (newTime.isAfter(this.dateTime)) {
                    this.dateTime = newTime;
                    unload();
                    super.load(plugin);
                    Bukkit.getOnlinePlayers().stream().filter(Player::isOp).forEach(op ->
                            Message.builder("Config {0} was reloaded for plugin: {1}")
                                    .arg(getPathString())
                                    .arg(plugin.getName())
                                    .send(op)
                    );
                    log(Level.INFO, "Config {0} was reloaded for plugin: {1}", getPathString(), plugin.getName());
                }
            }, 200);
            this.fileListenerTask = bukkitTask.getTaskId();
        }
    }
}
