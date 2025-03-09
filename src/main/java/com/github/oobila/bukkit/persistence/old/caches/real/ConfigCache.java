package com.github.oobila.bukkit.persistence.old.caches.real;

import com.github.alastairbooth.abid.ABID;
import com.github.oobila.bukkit.persistence.old.adapters.storage.FileStorageAdapter;
import com.github.oobila.bukkit.persistence.old.vehicles.CacheVehicle;
import com.github.oobila.bukkit.persistence.old.vehicles.global.GlobalPersistenceVehicle;
import com.github.oobila.bukkit.persistence.old.caches.standard.ReadOnlyCache;
import com.github.oobila.bukkit.persistence.old.model.CacheItem;
import com.github.oobila.bukkit.persistence.serializers.Serialization;
import com.github.oobila.bukkit.persistence.old.vehicles.readmethod.DefaultReader;
import com.github.oobila.bukkit.persistence.old.vehicles.utils.CacheItemFactory;
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
                CacheVehicle.builder()
                        .keyType(String.class)
                        .valueType(Object.class)
                        .storageAdapter(new FileStorageAdapter(".yml"))
                        .cacheItemFactory(new CacheItemFactory<>(false))
                        .readMethod(new DefaultReader())
                        .pollMethod()
                        .build()
        );
        getReadVehicles().forEach(vehicle -> vehicle.register(this));
    }

    public ConfigCache(String name, GlobalPersistenceVehicle<String, Object, CacheItem<String, Object>> vehicle) {
        super(name, vehicle);
    }

    public ConfigCache(
            String name,
            List<GlobalPersistenceVehicle<String, Object, CacheItem<String, Object>>> readVehicles,
            GlobalPersistenceVehicle<String, Object, CacheItem<String, Object>> writeVehicle
    ) {
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
