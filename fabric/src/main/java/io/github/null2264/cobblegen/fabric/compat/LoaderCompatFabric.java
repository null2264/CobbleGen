package io.github.null2264.cobblegen.forge.compat;

import io.github.null2264.cobblegen.compat.LoaderCompat;
import io.github.null2264.cobblegen.compat.ModContainerCompat;

import java.nio.file.Path;

public class LoaderCompatFabric extends LoaderCompat {

    @Override
    public boolean isModLoaded(String mod) {
        return net.fabricmc.loader.api.FabricLoader.getInstance().isModLoaded(mod);
    }

    @Override
    public Path getConfigDir() {
        #if MC>11605
        return FabricLoader.getInstance().getConfigDir();
        #else
        // Not ideal, but configDir is null somehow in 1.16.5
        Path configDir = FileSystems.getDefault().getPath(".", "config");
        if (!Files.exists(configDir)) {  // Stolen from fabric loader
            try {
                Files.createDirectories(configDir);
            } catch (IOException e) {
                throw new RuntimeException("Creating config directory", e);
            }
        }
        return configDir;
        #endif
    }

    @Override
    public LoaderType getType() {
        return LoaderType.FABRIC;
    }

    @Override
    public ModContainerCompat getMod(String modId) {
        if (isModCached(modId)) return super.getMod(modId);

        ModContainerCompat rt = new ModContainerFabric(modid);
        CACHED.put(modid, rt);
        return rt;
    }
}
