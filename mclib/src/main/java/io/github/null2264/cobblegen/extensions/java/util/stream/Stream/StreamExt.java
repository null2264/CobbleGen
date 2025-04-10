package io.github.null2264.cobblegen.extensions.java.util.stream.Stream;

import manifold.ext.rt.api.Extension;

#if MC<11700
import manifold.ext.rt.api.This;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;
#endif

@Extension
public final class StreamExt {

    #if MC<11700
    @SuppressWarnings("unchecked")
    public static <T> List<T> toList(@This Stream<T> stream) {
        return (List<T>) Collections.unmodifiableList(new ArrayList<>(Arrays.asList(stream.toArray())));
    }
    #endif
}
