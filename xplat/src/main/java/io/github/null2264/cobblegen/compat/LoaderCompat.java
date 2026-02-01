package io.github.null2264.cobblegen.compat;

import java.nio.file.Path;

#if FABRIC
import net.fabricmc.loader.api.FabricLoader;
    #if MC<=11605
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
    #endif
#endif

public abstract class LoaderCompat {

    private static LoaderCompat INSTANCE;

    public static void init(LoaderCompat impl) {
        if (INSTANCE != null) throw new IllegalStateException("Already initialized!");
        INSTANCE = impl;
    }

    public static LoaderCompat getInstance() {
        if (INSTANCE == null) throw new IllegalStateException("Not yet initialized!");
        return INSTANCE;
    }

    public abstract boolean isModLoaded(String mod);

    public abstract Path getConfigDir();

    public abstract LoaderType getType();

    public Boolean isFabric() {
        return getType() == LoaderType.FABRIC;
    }

    public Boolean isQuilt() {
        return getType() == LoaderType.QUILT;
    }

    public Boolean isFabricLike() {
        return isFabric() || isQuilt();
    }

    public Boolean isForge() {
        return getType() == LoaderType.FORGE;
    }

    public Boolean isNeoForge() {
        return getType() == LoaderType.NEOFORGE;
    }

    public Boolean isForgeLike() {
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
