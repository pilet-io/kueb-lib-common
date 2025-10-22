package io.pilet.kueb.common.data;

import lombok.extern.log4j.Log4j2;
import org.apache.commons.collections4.map.PassiveExpiringMap;

import java.util.List;

@Log4j2
public class CachedStore<T> implements Store<T> {
    private final Store<T> store;
    private final PassiveExpiringMap<String,T> cache;

    public CachedStore(Store<T> store) {
        this(store, 60L * 60000);
    }

    public CachedStore(Store<T> store, long ttl) {
        this.store = store;
        this.cache = new PassiveExpiringMap<>(ttl);
    }

    @Override
    public T save(T data) {
        return store.save(data);
    }

    @Override
    public void delete(String... id) {
        store.delete(id);
    }

    @Override
    public List<T> list(Filter... filters) {
        return store.list(filters);
    }

    @Override
    public T get(String... id) {
        String key = key(id);
        if(cache.containsKey(key)) {
            return cache.get(key);
        } else {
            log.info("Getting data for key: {}", key);
            T data = store.get(id);
            if(data != null) {
                cache.put(key, data);
            }
            return data;
        }
    }

    String key(String... id) {
        StringBuilder builder = new StringBuilder();
        for(int i=0; i<id.length; i++) {
            builder.append(id[i]);
            builder.append(",");
        }
        return builder.toString();
    }
}
