package io.github.null2264.cobblegen.data.model;

import net.minecraft.world.level.material.Fluid;

public interface CGRegistry {
    /**
     * Add new custom generator
     * @param fluid A fluid that will touch neighbouring fluid(s)
     * @param generator The generator
     */
    void addGenerator(Fluid fluid, Generator generator);
}