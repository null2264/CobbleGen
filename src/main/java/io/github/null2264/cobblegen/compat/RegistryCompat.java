package io.github.null2264.cobblegen.compat;

import net.minecraft.core.DefaultedRegistry;
import net.minecraft.core.Registry;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Fluid;

public class RegistryCompat {
    public static DefaultedRegistry<Fluid> fluid() {
        //#if MC<=11902
        return Registry.FLUID;
        //#else
        //$$ net.minecraft.core.registries.BuiltInRegistries.FLUID;
        //#endif
    }

    public static DefaultedRegistry<Block> block() {
        //#if MC<=11902
        return Registry.BLOCK;
        //#else
        //$$ net.minecraft.core.registries.BuiltInRegistries.BLOCK;
        //#endif
    }
}