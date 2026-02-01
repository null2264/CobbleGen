package io.github.null2264.cobblegen.util;

import io.github.null2264.cobblegen.CobbleGenPlugin;
import org.jetbrains.annotations.ApiStatus;

import java.util.*;

import static io.github.null2264.cobblegen.compat.CollectionCompat.streamToList;

public abstract class PluginFinder {

    private static PluginFinder INSTANCE;

    public static void init(PluginFinder impl) {
        if (INSTANCE != null) throw new IllegalStateException("Already initialized!");
        INSTANCE = impl;
    }

    public static PluginFinder getInstance() {
        if (INSTANCE == null) throw new IllegalStateException("Not yet initialized!");
        return INSTANCE;
    }

    public abstract List<PlugInContainer> getModPlugins();

    @ApiStatus.Internal
    public static class PlugInContainer {
        final String modId;
        final CobbleGenPlugin plugin;

        public PlugInContainer(String modId, CobbleGenPlugin plugin) {
            this.modId = modId;
            this.plugin = plugin;
        }

        public String getModId() {
            return modId;
        }

        public CobbleGenPlugin getPlugin() {
            return plugin;
        }
    }
}
