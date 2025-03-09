package com.github.oobila.bukkit.persistence.old.vehicles.utils;

import com.github.oobila.bukkit.persistence.old.adapters.storage.StoredData;
import com.github.oobila.bukkit.persistence.old.vehicles.global.GlobalPersistenceVehicle;
import com.github.oobila.bukkit.persistence.old.vehicles.player.PlayerPersistenceVehicle;
import com.github.oobila.bukkit.persistence.old.model.BackwardsCompatibility;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class BackwardsCompatibilityUtil {

    public static StoredData compatibility(GlobalPersistenceVehicle<?, ?, ?> vehicle, StoredData storedData) {
        return compatibility(vehicle.getBackwardsCompatibilityList(), storedData);
    }

    public static StoredData compatibility(PlayerPersistenceVehicle<?, ?, ?> vehicle, StoredData storedData) {
        return compatibility(vehicle.getBackwardsCompatibilityList(), storedData);
    }

    public static StoredData compatibility(List<BackwardsCompatibility> backwardsCompatibilities, StoredData storedData) {
        for (BackwardsCompatibility backwardsCompatibility : backwardsCompatibilities) {
            storedData = storedData.toBuilder()
                    .data(storedData.getData().replaceAll(
                            backwardsCompatibility.stringToReplace(),
                            backwardsCompatibility.replacement()
                    ))
                    .build();
        }
        return storedData;
    }

}
