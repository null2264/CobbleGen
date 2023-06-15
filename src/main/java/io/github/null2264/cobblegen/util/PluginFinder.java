package io.github.null2264.cobblegen.util;

import io.github.null2264.cobblegen.CobbleGenPlugin;
import lombok.Data;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.entrypoint.EntrypointContainer;
import org.jetbrains.annotations.ApiStatus;

import java.util.Comparator;
import java.util.List;

import static io.github.null2264.cobblegen.CobbleGen.MOD_ID;

public class PluginFinder
{
    public static List<PlugInContainer> getModPlugins() {
        //#if FABRIC>=1
        return FabricLoader.getInstance()
                .getEntrypointContainers("cobblegen_plugin", CobbleGenPlugin.class)
                .stream()
                .sorted(Comparator.comparingInt(PluginFinder::priorityEntrypoint))
                .map(entrypoint -> new PlugInContainer(entrypoint.getProvider().getMetadata().getId(), entrypoint.getEntrypoint()))
                .toList();
        //#else
        //#endif
    }

    //#if FABRIC>=1
    private static int priorityEntrypoint(EntrypointContainer<CobbleGenPlugin> plugin) {
        return plugin.getProvider().getMetadata().getId().equals(MOD_ID) ? 0 : 1;
    }
    //#else
    //#endif

    @Data
    @ApiStatus.Internal
    public static class PlugInContainer {
        final String modId;
        final CobbleGenPlugin plugin;
    }
}