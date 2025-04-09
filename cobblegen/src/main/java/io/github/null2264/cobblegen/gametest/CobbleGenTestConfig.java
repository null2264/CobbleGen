package io.github.null2264.cobblegen.gametest;

import io.github.null2264.cobblegen.util.Util;

// NOTE: Do NOT use Minecraft related stuff here!
public class CobbleGenTestConfig {
    public static final boolean ENABLED = Boolean.parseBoolean(
        System.getProperty(
            "null2264.cobblegen.gametest",
            Util.elvis(System.getenv("ENABLE_NULL2264_COBBLEGEN_GAMETEST"), "false")
        )
    );
}
