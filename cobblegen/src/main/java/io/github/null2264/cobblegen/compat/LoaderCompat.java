package io.github.null2264.cobblegen.compat;

import java.nio.file.Path;

#if FABRIC
import net.fabricmc.loader.api.FabricLoader;
    #if MC<=11605
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
    #endif
#elif FORGE
    #if FORGE>=2 && MC>=12002
import net.neoforged.fml.loading.FMLPaths;
import net.neoforged.fml.ModList;
import net.neoforged.fml.loading.LoadingModList;
    #else
import net.minecraftforge.fml.loading.FMLPaths;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.loading.LoadingModList;
    #endif
#else
    #error "Invalid loader name"
#endif

public class LoaderCompat {
    public static boolean isModLoaded(String mod) {
        #if FABRIC
        return net.fabricmc.loader.api.FabricLoader.getInstance().isModLoaded(mod);
        #else
        ModList modlist = ModList.get();
        if (modlist == null)  // mainly for MixinConfigPlugin
            return LoadingModList.get().getModFileById(mod) != null;
        return modlist.isLoaded(mod);
        #endif
    }

    public static Path getConfigDir() {
        #if FABRIC
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
        #elif FORGE
        return FMLPaths.CONFIGDIR.get();
        #else
        #error "Invalid loader name"
        #endif
    }

    public static LoaderType getType() {
        #if FABRIC
            #if FABRIC==1
        return LoaderType.FABRIC;
            #else
        return LoaderType.QUILT;
            #endif
        #elif FORGE
            #if FORGE==1
        return LoaderType.FORGE;
            #else
        return LoaderType.NEOFORGE;
            #endif
        #else
        #error "Invalid loader name"
        #endif
    }

    public static Boolean isForge() {
        return getType() == LoaderType.FORGE;
    }

    public static Boolean isNeoForge() {
        return getType() == LoaderType.NEOFORGE;
    }

    public static Boolean isForgeLike() {
        return isForge() || isNeoForge();
    }

    @SuppressWarnings("unused")
    public enum LoaderType {
        FABRIC,
        QUILT,
        FORGE,
        NEOFORGE
    }
}
