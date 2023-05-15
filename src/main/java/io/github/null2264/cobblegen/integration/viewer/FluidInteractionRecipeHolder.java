package io.github.null2264.cobblegen.integration.viewer;

import io.github.null2264.cobblegen.config.WeightedBlock;
import io.github.null2264.cobblegen.util.GeneratorType;
import lombok.Data;
import net.minecraft.block.Block;
import net.minecraft.fluid.Fluid;
import org.jetbrains.annotations.Nullable;

@Data
public class FluidInteractionRecipeHolder
{
    private final Fluid sourceFluid;
    private final @Nullable Fluid neighbourFluid;
    private final @Nullable Block neighbourBlock;
    private final WeightedBlock result;
    private final GeneratorType type;
    private final @Nullable Block modifier;
}