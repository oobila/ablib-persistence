package com.github.oobila.bukkit.persistence.utils;

import com.github.oobila.bukkit.persistence.adapters.storage.StoredData;
import com.github.oobila.bukkit.persistence.adapters.vehicle.PersistenceVehicle;
import com.github.oobila.bukkit.persistence.model.BackwardsCompatibility;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class BackwardsCompatibilityUtil {

    public static StoredData compatibility(PersistenceVehicle<?, ?> vehicle, StoredData storedData) {
        for (BackwardsCompatibility backwardsCompatibility : vehicle.getBackwardsCompatibilityList()) {
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
