package io.github.null2264.cobblegen.integration.jei;

import io.github.null2264.cobblegen.config.WeightedBlock;
import io.github.null2264.cobblegen.util.GeneratorType;
import lombok.Data;
import net.minecraft.block.Block;
import org.jetbrains.annotations.Nullable;

@Data
public class FluidInteractionRecipeHolder {
    private final WeightedBlock result;
    private final GeneratorType type;
    private final @Nullable Block modifier;
}