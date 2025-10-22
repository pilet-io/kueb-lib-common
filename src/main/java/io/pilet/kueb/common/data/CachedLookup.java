package io.pilet.kueb.common.data;

import org.apache.commons.collections4.map.PassiveExpiringMap;

public class CachedLookup<T> implements Lookup<T> {
    private final Lookup<T> lookup;
    private final PassiveExpiringMap<String,T> cache;

    public CachedLookup(Lookup<T> lookup) {
        this(lookup, 20 * 60000);
    }

    public CachedLookup(Lookup<T> lookup, long ttl) {
        this.lookup = lookup;
        this.cache = new PassiveExpiringMap<>(ttl);
    }

    @Override
    public T get(String... id) {
        String key = id(id);
        if(cache.containsKey(key)) {
            return cache.get(key);
        } else {
            T data = lookup.get(id);
            if(data != null) {
                cache.put(key, data);
            }
            return data;
        }
    }

    String id(String... id) {
        StringBuilder builder = new StringBuilder();
        for(int i=0; i<id.length; i++) {
            builder.append(id[i]);
            builder.append(",");
        }
        return builder.toString();
    }
}
