package com.github.oobila.bukkit.persistence.serializers;

import org.bukkit.Bukkit;
import org.bukkit.World;

public class WorldSerializer implements KeySerializer<World> {

    private final UUIDSerializer uuidSerializer = new UUIDSerializer();

    @Override
    public String serialize(World world) {
        if (world == null) {
            return "";
        } else {
            return uuidSerializer.serialize(world.getUID());
        }
    }

    @Override
    public World deserialize(String string) {
        return Bukkit.getWorld(uuidSerializer.deserialize(string));
    }
}
