package io.github.null2264.cobblegen.integration.viewer;

import io.github.null2264.cobblegen.data.config.WeightedBlock;
import io.github.null2264.cobblegen.util.GeneratorType;
import lombok.Data;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Fluid;
import org.jetbrains.annotations.NotNull;

@Data
public class FluidInteractionRecipeHolder
{
    private final @NotNull Fluid sourceFluid;
    private final @NotNull Fluid neighbourFluid;
    private final @NotNull Block neighbourBlock;
    private final @NotNull WeightedBlock result;
    private final @NotNull GeneratorType type;
    private final @NotNull Block modifier;
}