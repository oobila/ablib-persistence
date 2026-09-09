package com.github.oobila.bukkit.persistence.model;

/**
 * A single find/replace rule applied to serialized data as it's read (see
 * {@link com.github.oobila.bukkit.persistence.utils.BackwardsCompatibilityUtil}), most commonly
 * used to rewrite an old fully-qualified class name embedded in stored YAML to its new name after
 * a refactor, without needing a one-off migration for every existing save file.
 *
 * @param stringToReplace regex pattern to match in the stored text
 * @param replacement      text to substitute in its place
 */
public record BackwardsCompatibility(String stringToReplace, String replacement) {

}
