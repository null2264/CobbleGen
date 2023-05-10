package io.github.null2264.cobblegen.data;

import lombok.Data;
import net.minecraft.block.BlockState;

@Data
public class GeneratorResult
{
    private final BlockState blockState;
    private final boolean silent;
}