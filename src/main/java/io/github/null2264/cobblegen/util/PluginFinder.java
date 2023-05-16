package io.github.null2264.cobblegen.util;

import io.github.null2264.cobblegen.CobbleGenPlugin;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.entrypoint.EntrypointContainer;

import java.util.Comparator;
import java.util.List;

import static io.github.null2264.cobblegen.CobbleGen.MOD_ID;

public class PluginFinder
{
    public static List<EntrypointContainer<CobbleGenPlugin>> getModPlugins() {
        return FabricLoader.getInstance()
                .getEntrypointContainers("cobblegen_plugin", CobbleGenPlugin.class)
                .stream()
                .sorted(Comparator.comparingInt(PluginFinder::priorityEntrypoint))
                .toList();
    }

    private static int priorityEntrypoint(EntrypointContainer<CobbleGenPlugin> plugin) {
        return plugin.getProvider().getMetadata().getId().equals(MOD_ID) ? 0 : 1;
    }
}