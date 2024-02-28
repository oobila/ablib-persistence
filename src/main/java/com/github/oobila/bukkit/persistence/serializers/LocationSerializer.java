package com.github.oobila.bukkit.persistence.serializers;

import org.bukkit.Bukkit;
import org.bukkit.Location;

import java.util.UUID;

public class LocationSerializer implements KeySerializer<Location> {

    @Override
    public String serialize(Location location) {
        return location.getWorld().getUID().toString() +
                "#" + location.getBlockX() +
                "#" + location.getBlockY() +
                "#" + location.getBlockZ();
    }

    @Override
    public Location deserialize(String string) {
        String[] split = string.split("#");
        return new Location(
                Bukkit.getWorld(UUID.fromString(split[0])),
                Double.parseDouble(split[1]),
                Double.parseDouble(split[2]),
                Double.parseDouble(split[3])
        );
    }
}
