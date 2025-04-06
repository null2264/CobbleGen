package io.github.null2264.cobblegen.extensions.net.minecraft.world.level.Level;

import manifold.ext.rt.api.Extension;
import manifold.ext.rt.api.This;
import net.minecraft.world.level.Level;

@Extension
public final class LevelExt {
    #if MC>12101
    public static int getMinBuildHeight(@This Level thiz) {
        return thiz.getMinY();
    }

    public static int getMaxBuildHeight(@This Level thiz) {
        return thiz.getMaxY();
    }
    #endif
}
