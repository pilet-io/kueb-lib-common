package io.pilet.kueb.common.data;

import org.reflections.Reflections;
import org.reflections.scanners.Scanners;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Service
public class Stores {
    private final static Map<Class<?>, Store<?>> driverMap = new HashMap<>();
    private static JdbcTemplate jdbcTemplate;

    public static void init(JdbcTemplate jdbc, String... packages) {
        jdbcTemplate = jdbc;
        for(String packageName : packages) {
            init(packageName);
        }
    }

    static void init(String packageName) {
        Reflections reflections = new Reflections(packageName, Scanners.TypesAnnotated);
        Set<Class<?>> classes = reflections.getTypesAnnotatedWith(Table.class);
        for(Class<?> clazz : classes) {
            Table table = clazz.getAnnotation(Table.class);
            Store<?> store = new JdbcStoreDriver<>(jdbcTemplate, clazz);
            if(table.cached()) {
                driverMap.put(clazz, new CachedStore<>(store));
            } else {
                driverMap.put(clazz, store);
            }
        }
    }

    public static <T> Store<T> of(Class<T> clazz) {
        return (Store<T>) driverMap.get(clazz);
    }
}
