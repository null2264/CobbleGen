package io.github.null2264.cobblegen.compat;

//#if FABRIC>=1
import net.fabricmc.loader.api.FabricLoader;
//#else
//$$ import net.minecraftforge.fml.loading.FMLPaths;
//#endif

import java.nio.file.Path;

public class LoaderCompat {
    public static boolean isModLoaded(String mod) {
        //#if FABRIC>=1
        return net.fabricmc.loader.api.FabricLoader.getInstance().isModLoaded(mod);
        //#else
        //$$ net.minecraftforge.fml.ModList modlist = net.minecraftforge.fml.ModList.get();
        //$$ if (modlist == null)  // mainly for MixinConfigPlugin
        //$$     return net.minecraftforge.fml.loading.LoadingModList.get().getModFileById(mod) != null;
        //$$ return modlist.isLoaded(mod);
        //#endif
    }

    public static Path getConfigDir() {
        //#if FABRIC>=1
        return FabricLoader.getInstance().getConfigDir();
        //#else
        //$$ return FMLPaths.CONFIGDIR.get();
        //#endif
    }

    public static LoaderType getType() {
        //#if FABRIC==1
        return LoaderType.FABRIC;
        //#else
        //#if FABRIC==2
        //$$ return LoaderType.QUILT;
        //#else
        //$$ return LoaderType.FORGE;
        //#endif
        //#endif
    }

    public static Boolean isForge() {
        return getType() == LoaderType.FORGE;
    }

    @SuppressWarnings("unused")
    enum LoaderType {
        FABRIC,
        FORGE,
        QUILT
    }
}