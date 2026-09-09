package com.github.oobila.bukkit.persistence.utils;

import com.github.oobila.bukkit.persistence.adapters.storage.StoredData;
import com.github.oobila.bukkit.persistence.adapters.vehicle.PersistenceVehicle;
import com.github.oobila.bukkit.persistence.model.BackwardsCompatibility;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@SuppressWarnings("unused")
public class BackwardsCompatibilityUtil {

    /**
     * Applies every {@link BackwardsCompatibility} rule registered on {@code vehicle} (via
     * {@link PersistenceVehicle#addBackwardsCompatibility}) to {@code storedData}'s content, in
     * registration order, and returns the rewritten copy. Intended to be called on data as it's
     * read, before deserialization, so old save files referencing a since-renamed class or field
     * still load correctly.
     */
    public static StoredData compatibility(PersistenceVehicle<?, ?, ?> vehicle, StoredData storedData) {
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
