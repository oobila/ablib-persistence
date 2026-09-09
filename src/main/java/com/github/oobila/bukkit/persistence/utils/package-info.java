/**
 * Small top-level utilities shared across the persistence module. Currently just
 * {@link com.github.oobila.bukkit.persistence.utils.BackwardsCompatibilityUtil}, which applies a
 * {@code PersistenceVehicle}'s registered
 * {@link com.github.oobila.bukkit.persistence.model.BackwardsCompatibility} find/replace rules to
 * data as it is read, to support renaming serialized classes/fields without breaking existing
 * save files.
 */
package com.github.oobila.bukkit.persistence.utils;
