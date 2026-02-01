package io.github.null2264.cobblegen.fabric.util;

import io.github.null2264.cobblegen.CobbleGenPlugin;
import io.github.null2264.cobblegen.util.PluginFinder;
import net.fabricmc.loader.api.FabricLoader;

import java.util.List;

import static io.github.null2264.cobblegen.compat.CollectionCompat.streamToList;

public class PluginFinderFabric extends PluginFinder {

    @Override
    public List<PlugInContainer> getModPlugins() {
        return streamToList(
                FabricLoader.getInstance()
                        .getEntrypointContainers("cobblegen_plugin", CobbleGenPlugin.class)
                        .stream()
                        .map(entrypoint -> new PlugInContainer(entrypoint.getProvider().getMetadata().getId(), entrypoint.getEntrypoint()))
        );
    }
}
