package io.github.null2264.cobblegen.data.model;

import net.minecraft.world.level.material.Fluid;

public interface CGRegistry {
    void addGenerator(Fluid fluid, Generator generator);
}