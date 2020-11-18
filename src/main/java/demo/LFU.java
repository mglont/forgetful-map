package demo;

import java.util.concurrent.ConcurrentHashMap;

public class LFU<K, V> {
    private final int capacity;
    private final ConcurrentHashMap<K, V> keys;

    public LFU(int capacity) {
        if (capacity < 0)
            throw new IllegalArgumentException("Size should be a positive integer");
        this.capacity = capacity;
        // defer internal resizing of CHM
        keys = new ConcurrentHashMap<>(capacity, 1.0f);
    }

    public void add(K key, V value) {
        keys.put(key, value);
    }

    public V find(K key) {
        return keys.get(key);
    }

    public long size() {
        return keys.size();
    }
}
