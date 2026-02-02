package io.github.null2264.cobblegen.compat;

import java.nio.file.Path;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public abstract class LoaderCompat {

    private static LoaderCompat INSTANCE;
    public static ConcurrentMap<String, ModContainerCompat> CACHED_CONTAINER = new ConcurrentHashMap<>();

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

    public boolean isModCached(String modId) {
        return CACHED_CONTAINER.containsKey(modId);
    }

    public ModContainerCompat getMod(String modId) {
        return CACHED_CONTAINER.get(modId);
    }

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
