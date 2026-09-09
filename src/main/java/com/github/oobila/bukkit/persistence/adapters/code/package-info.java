/**
 * Converts value objects to and from the text form that gets handed to a
 * {@link com.github.oobila.bukkit.persistence.adapters.storage.StorageAdapter} for physical
 * storage. Implementations cover Bukkit's {@code ConfigurationSerializable} objects (YAML,
 * including {@code ItemStack}s), plain strings, WorldEdit clipboards/schematics, and zipped
 * bundles of multiple resources (see
 * {@link com.github.oobila.bukkit.persistence.adapters.code.ResourcePackCodeAdapter}).
 */
package com.github.oobila.bukkit.persistence.adapters.code;
