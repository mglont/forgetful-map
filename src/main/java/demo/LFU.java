package demo;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;

/**
 * A simple Least Frequently Used map.
 *
 * Assumptions
 *  * all map entries fit in memory, no need to overflow to disk
 *  * this is not a multimap - there can only be at most 1 value per key
 *  * null keys and values are not accepted
 *  * only find() increases the frequency of an association
 * @param <K>
 * @param <V>
 */
public final class LFU<K, V> {
    private final int capacity;
    private final AtomicInteger size = new AtomicInteger();
    private final Deque<K> queue;
    private final Map<K, V> lookup;
    private static final ReentrantLock modificationLock = new ReentrantLock();

    /**
     * Constructs a LFU of the given maximum size.
     * @param capacity a positive integer (greater than or equal to zero) and less than
     *                 Integer.MAX_VALUE - 8
     */
    public LFU(int capacity) {
        if (capacity < 0)
            throw new IllegalArgumentException("Size should be a positive integer");
        this.capacity = capacity;
        // defer internal resizing of HashMap
        lookup = new HashMap<>(capacity, 1.0f);
        queue = new ArrayDeque<>();
    }

    /**
     * Adds a new association or updates the value of an existing one.
     * Removes old entries if already at capacity.
     *
     * @param key non-null key
     * @param value non-null value to store with the given key
     */
    public void add(K key, V value) {
        if (null == key || null == value) throw new NullPointerException();
        if (0 == capacity) return;

        modificationLock.lock();
        try {
            if (!lookup.containsKey(key)) {
                if (capacity == size.get()) {
                    // remove last and insert this
                    final K toRemove = queue.removeLast();
                    lookup.remove(toRemove);
                } else {
                    size.incrementAndGet();
                }
                lookup.put(key, value);
                queue.offerLast(key);
            } else {
                // if the key is already in the map, update in-place
                // frequencies are controlled by lookups, not insertions
                lookup.put(key, value);
            }
        } finally {
            modificationLock.unlock();
        }
    }

    /**
     * Returns the value associated with the given key, if it exists.
     * If an association is present, its frequency is increased
     * @param key the key for which to look up the association
     * @return the value associated with the key, if it exists.
     */
    public V find(K key) {
        if (null == key) throw new NullPointerException();

        modificationLock.lock();
        try {
            if (!lookup.containsKey(key)) {
                return null;
            }
            queue.remove(key);
            queue.addFirst(key);
        } finally {
            modificationLock.unlock();
        }
        return lookup.get(key);
    }

    /**
     * Returns the current size of the collection.
     * @return the current map size
     */
    public int size() {
        return size.get();
    }

    /* convenience methods or unit tests */

    /* package local */ Map<K, V> getLookup() {
        return lookup;
    }

    /* package local */ Deque<K> getQueue() {
        return queue;
    }
}
