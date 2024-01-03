package io.github.null2264.cobblegen.compat;

//#if FABRIC>=1
import net.fabricmc.loader.api.FabricLoader;
    //#if MC<=1.16.5
    //$$ import lombok.val;
    //$$ import java.io.IOException;
    //$$ import java.nio.file.Files;
    //#endif
//#else
    //#if FORGE>=2 && MC>=1.20.2
    //$$ import net.neoforged.fml.loading.FMLPaths;
    //$$ import net.neoforged.fml.ModList;
    //$$ import net.neoforged.fml.loading.LoadingModList;
    //#else
    //$$ import net.minecraftforge.fml.loading.FMLPaths;
    //$$ import net.minecraftforge.fml.ModList;
    //$$ import net.minecraftforge.fml.loading.LoadingModList;
    //#endif
//#endif

import java.nio.file.Path;

public class LoaderCompat {
    public static boolean isModLoaded(String mod) {
        //#if FABRIC>=1
        return net.fabricmc.loader.api.FabricLoader.getInstance().isModLoaded(mod);
        //#else
        //$$ ModList modlist = ModList.get();
        //$$ if (modlist == null)  // mainly for MixinConfigPlugin
        //$$     return LoadingModList.get().getModFileById(mod) != null;
        //$$ return modlist.isLoaded(mod);
        //#endif
    }

    public static Path getConfigDir() {
        //#if FABRIC>=1
            //#if MC>1.16.5
            return FabricLoader.getInstance().getConfigDir();
            //#else
            // Not ideal, but configDir is null somehow in 1.16.5
            //$$ val configDir = Path.of(".", "config");
            //$$ if (!Files.exists(configDir)) {  // Stolen from fabric loader
            //$$     try {
            //$$         Files.createDirectories(configDir);
            //$$     } catch (IOException e) {
            //$$         throw new RuntimeException("Creating config directory", e);
            //$$     }
            //$$ }
            //$$ return configDir;
            //#endif
        //#else
        //$$ return FMLPaths.CONFIGDIR.get();
        //#endif
    }

    public static LoaderType getType() {
        //#if FABRIC>=1
            //#if FABRIC==1
            return LoaderType.FABRIC;
            //#else
            //$$ return LoaderType.QUILT;
            //#endif
        //#elseif FORGE>=1
            //#if FORGE==1
            //$$ return LoaderType.FORGE;
            //#else
            //$$ return LoaderType.NEOFORGE;
            //#endif
        //#else
        //$$ throw new RuntimeException("Unsupported Loader");
        //#endif
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
    enum LoaderType {
        FABRIC,
        QUILT,
        FORGE,
        NEOFORGE
    }
}