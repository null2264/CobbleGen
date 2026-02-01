package io.github.null2264.cobblegen.fabric.compat;

import io.github.null2264.cobblegen.compat.ModContainerCompat;

public class ModContainerFabric  extends ModContainerCompat {

    private final net.fabricmc.loader.api.ModContainer container;

    public ModContainerFabric(String modid) {
        this.container = net.fabricmc.loader.api.FabricLoader.getInstance().getModContainer(modid)
            #if MC<=11605
            .orElseThrow(NullPointerException::new);
            #else
            .orElseThrow();
            #endif
    }

    @Override
    public String getVersionString() {
        return container.getMetadata().getVersion().getFriendlyString();
    }
}
