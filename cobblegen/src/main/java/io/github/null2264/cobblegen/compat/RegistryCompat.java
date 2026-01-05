package io.github.null2264.cobblegen.compat;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Fluid;

public class RegistryCompat {
    public static net.minecraft.core.DefaultedRegistry<Fluid> fluid() {
        #if MC<=11902
        return net.minecraft.core.Registry.FLUID;
        #else
        return net.minecraft.core.registries.BuiltInRegistries.FLUID;
        #endif
    }

    public static net.minecraft.core.DefaultedRegistry<Block> block() {
        #if MC<=11902
        return net.minecraft.core.Registry.BLOCK;
        #else
        return net.minecraft.core.registries.BuiltInRegistries.BLOCK;
        #endif
    }
}
