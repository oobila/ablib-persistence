package com.github.oobila.bukkit.persistence.caches.real;

import com.github.alastairbooth.abid.ABID;
import com.github.oobila.bukkit.persistence.adapters.storage.FileStorageAdapter;
import com.github.oobila.bukkit.persistence.adapters.vehicle.PersistenceVehicle;
import com.github.oobila.bukkit.persistence.adapters.vehicle.YamlConfigVehicle;
import com.github.oobila.bukkit.persistence.caches.standard.ReadOnlyCache;
import com.github.oobila.bukkit.persistence.serializers.Serialization;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

@SuppressWarnings("unused")
public class ConfigCache extends ReadOnlyCache<String, Object> {

    public ConfigCache(String name) {
        super(
                name,
                new YamlConfigVehicle<>(
                        String.class,
                        new FileStorageAdapter("yml")
                )
        );
    }

    public ConfigCache(String name, PersistenceVehicle<String, Object> vehicle) {
        super(name, vehicle);
    }

    public ConfigCache(String name, List<PersistenceVehicle<String, Object>> readVehicles,
                       PersistenceVehicle<String, Object> writeVehicle) {
        super(name, readVehicles, writeVehicle);
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
}
