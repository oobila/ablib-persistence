package com.github.oobila.bukkit.persistence.caches.real;

import com.github.alastairbooth.abid.ABID;
import com.github.oobila.bukkit.persistence.adapters.code.StringCodeAdapter;
import com.github.oobila.bukkit.persistence.adapters.storage.FileStorageAdapter;
import com.github.oobila.bukkit.persistence.adapters.vehicle.PersistenceVehicle;
import com.github.oobila.bukkit.persistence.adapters.vehicle.YamlMultiItemVehicle;
import com.github.oobila.bukkit.persistence.caches.standard.ReadOnlyCache;
import com.github.oobila.bukkit.persistence.serializers.Serialization;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

@SuppressWarnings("unused")
public class ConfigCache extends ReadOnlyCache<String, String> {

    public ConfigCache(String name) {
        super(
                name,
                new YamlMultiItemVehicle<>(
                        String.class,
                        new FileStorageAdapter("yml"),
                        new StringCodeAdapter()
                )
        );
    }

    public ConfigCache(String name, PersistenceVehicle<String, String> vehicle) {
        super(name, vehicle);
    }

    public ConfigCache(String name, PersistenceVehicle<String, String> writeVehicle,
                       List<PersistenceVehicle<String, String>> readVehicles) {
        super(name, writeVehicle, readVehicles);
    }

    public String getString(String key) {
        return getValue(key);
    }

    public int getInt(String key) {
        return Integer.parseInt(getValue(key));
    }

    public double getDouble(String key) {
        return Double.parseDouble(getValue(key));
    }

    public float getFloat(String key) {
        return Float.parseFloat(getValue(key));
    }

    public boolean getBoolean(String key) {
        return Boolean.parseBoolean(getValue(key));
    }

    public LocalDate getDate(String key) {
        return LocalDate.from(DateTimeFormatter.ISO_DATE.parse(getValue(key)));
    }

    public ZonedDateTime getDateTime(String key) {
        return ZonedDateTime.from(DateTimeFormatter.ISO_DATE_TIME.parse(getValue(key)));
    }

    public LocalTime getTime(String key) {
        return LocalTime.from(DateTimeFormatter.ISO_TIME.parse(getValue(key)));
    }

    public UUID getUuid(String key) {
        return Serialization.deserialize(UUID.class, getValue(key));
    }

    public ABID getAbid(String key) {
        return Serialization.deserialize(ABID.class, getValue(key));
    }

    public OfflinePlayer getOfflinePlayer(String key) {
        return Serialization.deserialize(OfflinePlayer.class, getValue(key));
    }

    public World getWorld(String key) {
        return Serialization.deserialize(World.class, getValue(key));
    }

    public Location getLocation(String key) {
        return Serialization.deserialize(Location.class, getValue(key));
    }
}
