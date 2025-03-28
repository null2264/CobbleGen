package io.github.null2264.cobblegen.compat;

import java.util.*;
import java.util.stream.Stream;

public class CollectionCompat {
    @SafeVarargs
    public static <T> List<T> listOf(T... items) {
        return new ArrayList<>(Arrays.asList(items));
    }

    public static <K, V> Map<K, V> mapOf() {
        return new HashMap<>();
    }

    public static <K, V> Map<K, V> mapOf(K key, V value) {
        Map<K, V> rt = new HashMap<>();
        rt.put(key, value);
        return rt;
    }

    @SuppressWarnings("unchecked")
    public static <T> List<T> streamToList(Stream<T> stream) {
        return (List<T>) Collections.unmodifiableList(new ArrayList<>(Arrays.asList(stream.toArray())));
    }
}