# ablib-persistence

`ablib-persistence` is a generic, pluggable persistence layer for Bukkit / Spigot / Paper plugins.
Its purpose is to take the pain out of **saving and loading data** — most commonly `ItemStack`s and
other `ConfigurationSerializable` objects, but also WorldEdit clipboards/schematics, arbitrary config
values, and simple key/value records — without every plugin having to hand-roll its own YAML, file or
SQL handling code.

It is part of the [`ablib`](../) family of libraries (see `ablib-blocks`, `ablib-itemstack`,
`ablib-worldedit`, `ablib-minigame`, etc., which all depend on this module for their storage needs).

## The problem this solves

A typical Bukkit plugin ends up writing the same boilerplate over and over:

- Read/write YAML config files by hand, merging in new default keys as the plugin evolves.
- Serialize `ItemStack`s (or other `ConfigurationSerializable` objects) to and from disk.
- Store per-player data, keyed by UUID, loading it on join and saving it on quit.
- Occasionally swap file storage for a MySQL table without rewriting all the calling code.

`ablib-persistence` provides a small set of **caches** that do all of this for you: you describe
*what* you want to store (a key type, a value type, and a path/table) and the library handles
*how* it gets serialized and where it physically lives.

## Architecture

Persisting a single piece of data flows through four collaborating layers:

```
   Plugin code
        │
        ▼
   ┌─────────┐   in-memory map of keys → CacheItems, exposes get/put/remove,
   │  Cache  │   partitioned per player (or globally), sync or async
   └────┬────┘
        │ delegates loading/saving to
        ▼
┌────────────────────┐  resolves {uuid}/{key} path placeholders, walks the
│ PersistenceVehicle  │  storage medium for matching entries, turns raw
└──────┬──────┬───────┘  StoredData into CacheItems and back again
       │      │
       ▼      ▼
┌─────────────┐  ┌───────────────┐
│ CodeAdapter │  │ StorageAdapter │
└─────────────┘  └───────────────┘
 object <-> text     text <-> storage medium
 (YAML/zip/plain)    (file / plugin config / SQL table)
```

- **`caches`** (`Cache`, `ReadCache`, `WriteCache`, and the `standard`/`async` sub-packages) —
  the API plugin code actually calls: `getValue`, `putValue`, `remove`, `load`, `save`,
  `transaction(...)`, etc. Caches come in read-only or read/write flavours, and in synchronous
  or asynchronous flavours (the async ones push I/O off the main server thread and are the
  right choice for anything backed by SQL).
- **`caches.real`** — ready-to-use cache implementations you construct directly in your plugin
  (`SimpleFileCache`, `SimpleSqlCache`, `ConfigCache`, `CombiCache`, `ClipboardSqlCache`, …). Most
  plugins never need to touch anything below this layer.
- **`adapters.vehicle`** (`PersistenceVehicle`, `DynamicVehicle`) — the glue between a `Cache`
  and physical storage. A `DynamicVehicle` is configured with a *path template* such as
  `"players/{uuid}/homes/{key}.yml"`; the `{uuid}` and `{key}` placeholders tell it whether/how
  to partition data per player and per record, and it works out what files/rows exist by walking
  the `StorageAdapter`.
- **`adapters.code`** (`CodeAdapter` and implementations) — converts a value object to and from
  the text representation that gets stored (YAML via `ConfigurationSerializable`, a raw string,
  a zipped bundle of resources, a WorldEdit clipboard/schematic, ...).
- **`adapters.storage`** (`StorageAdapter` and implementations) — reads and writes that text to
  an actual medium: a plugin data file, a plugin's `config.yml`-style file (with default merging),
  or rows in a SQL table.
- **`model`** — the data types passed between these layers: `CacheItem`/`OnDemandCacheItem` (one
  cached value plus its metadata), `StoredData` (the raw text form plus size/last-modified info),
  `ResourcePack`/`Resource`, `BackwardsCompatibility`, `SqlConnectionProperties`.
- **`serializers`** — turn *keys* (not values) into short strings usable in file/table names,
  e.g. a `UUID`, `Location`, `OfflinePlayer` or `LocalDate` key. Registered centrally in
  `Serialization` and looked up by key type.
- **`observers`** — optional listeners a plugin can attach to a cache to react to loads, saves,
  puts and removes without touching the cache's own code (`CacheObserver`,
  `ReadCacheOperationObserver`, `WriteCacheOperationObserver`, `PlayerObserver`).
- **`listeners`** — `PersistencePlayerJoinListener` auto-loads/saves/unloads every cache
  registered via `CacheManager.registerPlayerCache(...)` as players join and leave, so
  per-player caches need no manual lifecycle management.

## Partitions and keys

Most data in a Bukkit plugin is either **global** (a config value, a leaderboard) or **per-player**
(homes, kits, warps). `ablib-persistence` models this with a single concept: a **partition**,
identified by a `UUID` (conventionally the player's UUID, but it can be any UUID — e.g. a world
or arena ID). Loading/saving with no partition (`null`) operates on the global data; loading/saving
with a partition operates on just that player's/entity's slice.

Within a partition, individual records are addressed by a **key** of any type that has a
registered `KeySerializer` (`String`, `Integer`, `UUID`, `Location`, `OfflinePlayer`, `World`,
`LocalDate`, `LocalTime`, `ZonedDateTime`, or your own — see `Serialization.register(...)`).

A `DynamicVehicle`'s path template ties these together, e.g.:

- `"scores.yml"` — one global file, no partitioning, no per-key files.
- `"players/{uuid}.yml"` — one file per player.
- `"players/{uuid}/homes/{key}.yml"` — one file per home, per player.
- `"table=homes,partition_id={uuid},record_key={key}"` — the SQL equivalent, one row per home.

## Quick start

Storing an `ItemStack` per player, in a flat file, loaded/saved automatically on join/quit:

```java
public class MyPlugin extends JavaPlugin {

    // one ItemStack "favourite" per player, stored at plugins/MyPlugin/favourites/{uuid}.yml
    private final SimpleFileCache<String, ItemStack> favourites =
            new SimpleFileCache<>("favourites/{uuid}.yml", String.class, ItemStack.class);

    @Override
    public void onEnable() {
        // makes the cache load on player join and save+unload on player quit
        CacheManager.registerPlayerCache(favourites);
        getServer().getPluginManager().registerEvents(new PersistencePlayerJoinListener(), this);
    }

    public void setFavourite(Player player, ItemStack item) {
        favourites.putValue(player.getUniqueId(), "favourite", item);
    }

    public ItemStack getFavourite(Player player) {
        return favourites.getValue(player.getUniqueId(), "favourite");
    }
}
```

For data that should live in MySQL instead (e.g. shared across a server network), swap in
`SimpleSqlCache`/`CombiCache` — the calling code (`getValue`/`putValue`/`remove`) stays the same.

## Choosing a cache (`caches.real`)

| Cache                     | Storage        | Sync/Async | Loaded fully in memory? | Typical use                                   |
|---------------------------|-----------------|------------|--------------------------|------------------------------------------------|
| `SimpleFileCache`         | File (YAML)     | Sync       | Yes                      | Small/medium per-player or global data         |
| `SimpleAsyncFileCache`    | File (YAML)     | Async      | Yes                      | Same, off the main thread                      |
| `ConfigCache`             | Plugin config   | Sync       | Yes                      | Plugin settings, auto-reloads on file change   |
| `IntegerMetricCache`      | File (YAML)     | Sync       | Yes                      | Simple counters/statistics                     |
| `SimpleSqlCache`          | SQL table       | Async      | No — loaded on demand    | Data too large/shared to keep fully in memory  |
| `StringSqlCache`          | SQL table       | Async      | No — loaded on demand    | Simple string records in SQL                   |
| `ClipboardSqlCache`       | SQL table       | Async      | No — loaded on demand    | WorldEdit schematics/clipboards in SQL         |
| `CombiCache`               | File + SQL      | Async      | Depends                  | Migrate/mirror between file and SQL storage    |
| `MessageCache`             | File + SQL      | Async      | Depends                  | Offline messages, delivered on next join       |
| `SimpleAsyncResourceCache` | File (zip)      | Async      | Yes                      | Small number of resource-pack zip bundles      |
| `ResourcePackCache`        | File (zip)      | Async      | No — loaded on demand    | Many resource-pack zip bundles                 |

## Backwards compatibility

`PersistenceVehicle.addBackwardsCompatibility(...)` lets a vehicle register simple
find-and-replace rules (`BackwardsCompatibility`, applied via `BackwardsCompatibilityUtil`) that
are run over stored data as it is read — handy for renaming a serialized class or field without
having to write a one-off migration for every existing save file.
