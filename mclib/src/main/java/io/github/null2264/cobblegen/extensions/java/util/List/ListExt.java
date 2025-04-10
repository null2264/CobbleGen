package io.github.null2264.cobblegen.extensions.java.util.List;

import manifold.ext.rt.api.Extension;

#if MC<11700
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
#endif

@Extension
public final class ListExt {
    #if MC<11700
    @SafeVarargs
    @Extension
    public static <T> List<T> of(T... items) {
        return new ArrayList<>(Arrays.asList(items));
    }
    #endif
}
