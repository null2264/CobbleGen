package io.github.null2264.cobblegen.data.model;

import lombok.val;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.WorldAccess;
import org.jetbrains.annotations.ApiStatus;

import java.util.Optional;

import static io.github.null2264.cobblegen.data.FluidInteractionHelper.FLOW_DIRECTIONS;

@ApiStatus.Internal
public abstract class BlockGenerator implements BuiltInGenerator
{
    protected abstract Optional<BlockState> tryGenerate(WorldAccess world, BlockPos pos, BlockState state, Direction direction);

    @Override
    public Optional<BlockState> tryGenerate(WorldAccess world, BlockPos pos, BlockState state) {
        for (val direction : FLOW_DIRECTIONS) {
            val candidate = tryGenerate(world, pos, state, direction);
            if (candidate.isEmpty()) continue;
            ;
            return candidate;
        }

        return Optional.empty();
    }

}