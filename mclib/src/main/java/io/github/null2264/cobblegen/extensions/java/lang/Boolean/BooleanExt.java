package io.github.null2264.cobblegen.extensions.java.lang.Boolean;

import org.jetbrains.annotations.Nullable;

import java.util.Arrays;

@manifold.ext.rt.api.Extension
public final class BooleanExt {
    @manifold.ext.rt.api.Extension
    public static boolean parse(@Nullable String string) {
        if (string == null) return false;

        String[] yes = {"yes", "y", "true", "t", "1", "enable", "on"};
        //String[] no = {"no", "n", "false", "f", "0", "disable", "off"};

        return Arrays.asList(yes).contains(string.toLowerCase());
        //else if (Arrays.asList(no).contains(string.toLowerCase())) return false;
    }
}
