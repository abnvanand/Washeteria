package github.abnvanand.washeteria.utils;

import java.util.HashMap;

public class BidirectionalMap<K, V> extends HashMap<K, V> {
    private static final long serialVersionUID = 1L;
    public HashMap<V, K> inversedMap = new HashMap<>();

    public K getKey(V value) {
        return inversedMap.get(value);
    }

    @Override
    public boolean isEmpty() {
        return this.size() > 0;
    }

    @Override
    public V remove(Object key) {
        V val = super.remove(key);
        inversedMap.remove(val);
        return val;
    }

    @Override
    public V get(Object key) {
        return super.get(key);
    }

    @Override
    public V put(K key, V value) {
        inversedMap.put(value, key);
        return super.put(key, value);
    }
}