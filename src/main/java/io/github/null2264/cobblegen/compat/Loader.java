package io.github.null2264.cobblegen.compat;

public class Loader {
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
}