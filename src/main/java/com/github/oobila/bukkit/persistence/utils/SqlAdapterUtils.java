package com.github.oobila.bukkit.persistence.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import static com.github.oobila.bukkit.common.ABCommon.log;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class SqlAdapterUtils {

//    private static Set<BaseCache<?,?>> connectionHolders = new HashSet<>();
//    @Getter
//    private static Connection connection;
//
//    @SuppressWarnings("java:S4925") //this is required to load the correct SQL driver
//    public static Connection createConnection(BaseCache<?,?> cache) {
//        if(connectionHolders.isEmpty()) {
//            try {
//                Class.forName("com.mysql.jdbc.Driver");
//                connection = DriverManager.getConnection(
//                        "jdbc:mysql://" + cache.getSqlConnectionProperties().getHostname() + ":" +
//                                cache.getSqlConnectionProperties().getPort() + "/" +
//                                cache.getSqlConnectionProperties().getDatabase(),
//                        cache.getSqlConnectionProperties().getUsername(),
//                        cache.getSqlConnectionProperties().getPassword()
//                );
//            } catch (SQLException | ClassNotFoundException e) {
//                throw new SqlRuntimeException(e);
//            }
//        }
//        connectionHolders.add(cache);
//        return connection;
//    }
//
//    public static void closeConnection(BaseCache<?,?> cache) {
//        connectionHolders.remove(cache);
//        if (connectionHolders.isEmpty()) {
//            try {
//                connection.close();
//                connection = null;
//            } catch (SQLException e) {
//                throw new SqlRuntimeException(e);
//            }
//        }
//    }
//
//    public static String getTableName(BaseCache<?, ?> cache) {
//        return StringUtils.replace(cache.getPlugin().getName() + "_" + cache.getName(), "-", "_");
//    }
//
//    public static <V extends PersistedObject> String serializeData(V data) {
//        Map<String,Object> map = data.serialize();
//        YamlConfiguration yamlConfiguration = new MyYamlConfiguration();
//        map.forEach(yamlConfiguration::set);
//        String yaml = yamlConfiguration.saveToString();
//        return StringUtils.replace(yaml, "'", "''");
//    }

}
