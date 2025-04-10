package io.github.null2264.cobblegen.extensions.java.util.List;

#if MC<11700
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
#endif

@manifold.ext.rt.api.Extension
public final class ListExt {
    #if MC<11700
    @SafeVarargs
    @manifold.ext.rt.api.Extension
    public static <T> List<T> of(T... items) {
        return new ArrayList<>(Arrays.asList(items));
    }
    #endif
}
