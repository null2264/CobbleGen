package io.github.null2264.cobblegen.data;

import io.github.null2264.cobblegen.data.model.CGRegistry;
import io.github.null2264.cobblegen.data.model.Generator;
import net.minecraft.world.level.material.Fluid;

import static io.github.null2264.cobblegen.CobbleGen.FLUID_INTERACTION;

public class CGRegistryImpl implements CGRegistry
{
    @Override
    public void addGenerator(Fluid fluid, Generator generator) {
        FLUID_INTERACTION.addGenerator(fluid, generator);
    }
}