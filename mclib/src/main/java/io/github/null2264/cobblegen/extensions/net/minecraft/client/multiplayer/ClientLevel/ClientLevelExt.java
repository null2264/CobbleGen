package io.github.null2264.cobblegen.extensions.net.minecraft.client.multiplayer.ClientLevel;

import manifold.ext.rt.api.Extension;
import manifold.ext.rt.api.This;
import net.minecraft.client.multiplayer.ClientLevel;

@Extension
public class ClientLevelExt {
    #if MC>12101
    public static int getMinBuildHeight(@This ClientLevel thiz) {
        return thiz.getMinY();
    }

    public static int getMaxBuildHeight(@This ClientLevel thiz) {
        return thiz.getMaxY();
    }
    #endif
}
