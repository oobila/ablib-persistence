package com.github.oobila.bukkit.persistence.serializers;

import org.bukkit.Location;

public class LocationSerializer implements KeySerializer<Location> {

    WorldSerializer worldSerializer = new WorldSerializer();

    @Override
    public String serialize(Location location) {
        return String.format(
                "%s_%s_%s_%s",
                worldSerializer.serialize(location.getWorld()),
                location.getX(),
                location.getY(),
                location.getZ()
        );
    }

    @Override
    public Location deserialize(String string) {
        String[] split = string.split("_");
        return new Location(
                split[0].isEmpty() ? null : worldSerializer.deserialize(split[0]),
                Double.parseDouble(split[1]),
                Double.parseDouble(split[2]),
                Double.parseDouble(split[3])
        );
    }
}
