package io.github.null2264.cobblegen.util;

import net.fabricmc.loader.api.FabricLoader;

import java.util.List;

public class PluginFinder
{
    public static List<CobbleGenPlugin> getModPlugins() {
        return FabricLoader.getInstance().getEntrypoints("cobblegen_plugin", CobbleGenPlugin.class);
    }
}