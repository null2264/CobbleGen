package io.github.null2264.cobblegen.compat;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

#if FORGE
    #if FORGE==1
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.loading.LoadingModList;
import net.minecraftforge.forgespi.language.IModInfo;
    #else
import net.neoforged.fml.ModList;
import net.neoforged.fml.loading.LoadingModList;
import net.neoforged.neoforgespi.language.IModInfo;
    #endif
#endif

public class ModContainerCompat {
    public static ConcurrentMap<String, ModContainerCompat> CACHED = new ConcurrentHashMap<>();
    private final
        #if FABRIC
        net.fabricmc.loader.api.ModContainer
        #else
        IModInfo
        #endif
            container;

    private ModContainerCompat(String modid) {
        #if FORGE
        IModInfo modInfo;
        if (ModList.get() == null)  // mainly for MixinConfigPlugin
            // I hate this... but should be fine since it happened on loading state
            modInfo = LoadingModList.get()
                .getModFileById(modid)
                .getMods()
                .stream()
                .filter(i -> i.getModId().equals(modid))
                .findFirst()
                .orElseThrow();
        else
            modInfo = ModList.get()
                .getModContainerById(modid)
                .orElseThrow()
                .getModInfo();
        #endif
        this.container =
        #if FABRIC
            net.fabricmc.loader.api.FabricLoader.getInstance().getModContainer(modid).orElseThrow();
        #else
            modInfo;
        #endif
    }

    public static ModContainerCompat fromLoader(String modid) {
        if (CACHED.containsKey(modid)) return CACHED.get(modid);

        ModContainerCompat rt = new ModContainerCompat(modid);
        CACHED.put(modid, new ModContainerCompat(modid));
        return rt;
    }

    public String getVersionString() {
        #if FABRIC
        return container.getMetadata().getVersion().getFriendlyString();
        #else
        return container.getVersion().toString();
        #endif
    }
}
