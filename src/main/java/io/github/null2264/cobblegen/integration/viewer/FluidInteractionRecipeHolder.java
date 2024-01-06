//#if MC>1.16.5
package io.github.null2264.cobblegen.integration.viewer;

import io.github.null2264.cobblegen.config.WeightedBlock;
import io.github.null2264.cobblegen.util.GeneratorType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Fluid;
import org.jetbrains.annotations.NotNull;

public class FluidInteractionRecipeHolder
{
    private final @NotNull Fluid sourceFluid;
    private final @NotNull Fluid neighbourFluid;
    private final @NotNull Block neighbourBlock;
    private final @NotNull WeightedBlock result;
    private final @NotNull GeneratorType type;
    private final @NotNull Block modifier;

    public FluidInteractionRecipeHolder(@NotNull Fluid sourceFluid, @NotNull Fluid neighbourFluid, @NotNull Block neighbourBlock, @NotNull WeightedBlock result, @NotNull GeneratorType type, @NotNull Block modifier) {
        this.sourceFluid = sourceFluid;
        this.neighbourFluid = neighbourFluid;
        this.neighbourBlock = neighbourBlock;
        this.result = result;
        this.type = type;
        this.modifier = modifier;
    }

    public @NotNull Fluid getSourceFluid() {
        return sourceFluid;
    }

    public @NotNull Fluid getNeighbourFluid() {
        return neighbourFluid;
    }

    public @NotNull Block getNeighbourBlock() {
        return neighbourBlock;
    }

    public @NotNull WeightedBlock getResult() {
        return result;
    }

    public @NotNull GeneratorType getType() {
        return type;
    }

    public @NotNull Block getModifier() {
        return modifier;
    }
}
//#endif