package io.github.null2264.cobblegen.forge.compat;

import io.github.null2264.cobblegen.compat.LoaderCompat;
import io.github.null2264.cobblegen.compat.ModContainerCompat;

import java.nio.file.Path;

#if MC>=12002
import net.neoforged.fml.loading.FMLPaths;
import net.neoforged.fml.ModList;
import net.neoforged.fml.loading.LoadingModList;
#else
import net.minecraftforge.fml.loading.FMLPaths;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.loading.LoadingModList;
#endif

public class LoaderCompatForge extends LoaderCompat {

    @Override
    public boolean isModLoaded(String mod) {
        ModList modlist = ModList.get();
        if (modlist == null)  // mainly for MixinConfigPlugin
            return LoadingModList.get().getModFileById(mod) != null;
        return modlist.isLoaded(mod);
    }

    @Override
    public Path getConfigDir() {
        return FMLPaths.CONFIGDIR.get();
    }

    @Override
    public LoaderType getType() {
        #if MC>=12002
        return LoaderType.NEOFORGE;
        #else
        return LoaderType.FORGE;
        #endif
    }

    @Override
    public ModContainerCompat getMod(String modId) {
        if (isModCached(modId)) return super.getMod(modId);

        ModContainerCompat rt = new ModContainerForge(modid);
        CACHED.put(modid, rt);
        return rt;
    }
}
