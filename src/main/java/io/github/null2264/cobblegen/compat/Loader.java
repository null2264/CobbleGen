package io.github.null2264.cobblegen.compat;

public class Loader {
    public static boolean isModLoaded(String mod) {
        //#if FABRIC>=1
        return net.fabricmc.loader.api.FabricLoader.getInstance().isModLoaded(mod);
        //#else
        //$$ return net.minecraftforge.fml.ModList.get().isLoaded(mod);
        //#endif
    }
}