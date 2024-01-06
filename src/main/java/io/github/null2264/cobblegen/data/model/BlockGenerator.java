package io.github.null2264.cobblegen.data.model;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.ApiStatus;

import java.util.Optional;

import static io.github.null2264.cobblegen.data.FluidInteractionHelper.FLOW_DIRECTIONS;

@ApiStatus.Internal
public abstract class BlockGenerator implements BuiltInGenerator
{
    protected abstract Optional<BlockState> tryGenerate(LevelAccessor level, BlockPos pos, BlockState state, Direction direction);

    @Override
    public Optional<BlockState> tryGenerate(LevelAccessor level, BlockPos pos, BlockState state) {
        for (Direction direction : FLOW_DIRECTIONS) {
            final Optional<BlockState> candidate = tryGenerate(level, pos, state, direction);
            if (!candidate.isPresent()) continue;
            return candidate;
        }

        return Optional.empty();
    }

}