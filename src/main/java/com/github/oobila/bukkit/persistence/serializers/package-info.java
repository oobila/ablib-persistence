/**
 * Converts cache <em>keys</em> (not the values being stored — see {@code adapters.code} for
 * that) to and from short strings usable in file names, paths and SQL columns. Each supported key
 * type has a {@link com.github.oobila.bukkit.persistence.serializers.KeySerializer}
 * implementation, registered centrally in
 * {@link com.github.oobila.bukkit.persistence.serializers.Serialization} and looked up by the
 * key's type (with support for matching a supertype, e.g. any {@code OfflinePlayer}
 * implementation). Register a serializer for your own key type via
 * {@link com.github.oobila.bukkit.persistence.serializers.Serialization#register}.
 */
package com.github.oobila.bukkit.persistence.serializers;
