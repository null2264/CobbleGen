package io.github.null2264.cobblegen.data.model;

import net.minecraft.fluid.Fluid;

public interface CGRegistry {
    void addGenerator(Fluid fluid, Generator generator);
}