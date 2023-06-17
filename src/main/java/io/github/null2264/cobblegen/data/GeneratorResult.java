package io.github.null2264.cobblegen.data;

import lombok.Data;
import net.minecraft.world.level.block.state.BlockState;

@Data
public class GeneratorResult
{
    private final BlockState blockState;
    private final boolean silent;
}