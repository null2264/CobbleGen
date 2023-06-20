package io.github.null2264.cobblegen.compat;

import net.minecraft.core.Registry;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Fluid;

public class RegistryCompat {
    // for some reason DefaultedRegistry turned into DefaultedMappedRegistry, that's why it's like this
    //#if MC<=11902
    public static net.minecraft.core.DefaultedRegistry<Fluid> fluid() {
    //#else
    //$$ public static net.minecraft.core.DefaultedRegistry<Fluid> fluid() {
    //#endif
        //#if MC<=11902
        return Registry.FLUID;
        //#else
        //$$ return net.minecraft.core.registries.BuiltInRegistries.FLUID;
        //#endif
    }

    //#if MC<=11902
    public static net.minecraft.core.DefaultedRegistry<Block> block() {
    //#else
    //$$ public static net.minecraft.core.DefaultedRegistry<Block> block() {
    //#endif
        //#if MC<=11902
        return Registry.BLOCK;
        //#else
        //$$ return net.minecraft.core.registries.BuiltInRegistries.BLOCK;
        //#endif
    }
}