package io.github.null2264.cobblegen.forge.compat;

import io.github.null2264.cobblegen.compat.ModContainerCompat;

#if MC>=12002
import net.neoforged.fml.ModList;
#if MC>=12111
import net.neoforged.fml.loading.FMLLoader;
#endif
import net.neoforged.fml.loading.LoadingModList;
import net.neoforged.neoforgespi.language.IModInfo;
#else
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.loading.LoadingModList;
import net.minecraftforge.forgespi.language.IModInfo;
#endif

public class ModContainerForge extends ModContainerCompat {

    private final IModInfo container;

    public ModContainerForge(String modId) {
        IModInfo modInfo;
        if (ModList.get() == null) {  // mainly for MixinConfigPlugin
            LoadingModList modList =
                #if MC>=12111
                FMLLoader.getCurrent().getLoadingModList();
                #else
                LoadingModList.get();
                #endif
            // I hate this... but should be fine since it happened on loading state
            this.container = modList
                .getModFileById(modId)
                .getMods()
                .stream()
                .filter(i -> i.getModId().equals(modId))
                .findFirst()
                #if MC<=11605
                .orElseThrow(NullPointerException::new);
                #else
                .orElseThrow();
                #endif
        } else {
            this.container = ModList.get()
                .getModContainerById(modId)
                #if MC<=11605
                .orElseThrow(NullPointerException::new)
                #else
                .orElseThrow()
                #endif
                .getModInfo();
        }
    }

    @Override
    public String getVersionString() {
        return container.getVersion().toString();
    }
}
