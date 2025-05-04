package io.github.null2264.cobblegen.gametest;

import java.util.List;

public interface Constants {
    boolean IS_GAMETEST_ENABLED = boolFromString(
        System.getProperty("null2264.cobblegen.gametest", System.getenv("ENABLE_NULL2264_COBBLEGEN_GAMETEST"))
    );

    /**
     * Basically {@link Boolean#parseBoolean(String string)} but with support for other boolean representation
     * to be more user-friendly.
     *
     * @param string the String containing the boolean representation
     * @return the boolean represented by the string argument
     */
    private static boolean boolFromString(String string) {
        if (string == null) return false;

        List<String> yes = List.of("yes", "y", "true", "t", "1", "enable", "on");
        List<String> no = List.of("no", "n", "false", "f", "0", "disable", "off");

        if (yes.contains(string.toLowerCase())) return true;
        else if (no.contains(string.toLowerCase())) return false;

        // We supposed to throw an exception here, but we'll fallback to false instead
        return false;
    }
}
