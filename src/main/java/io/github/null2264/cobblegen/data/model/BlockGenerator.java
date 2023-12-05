package io.github.null2264.cobblegen.data.model;

import lombok.val;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.ApiStatus;

import java.util.Optional;

import static io.github.null2264.cobblegen.util.Constants.FLOW_DIRECTIONS;

@ApiStatus.Internal
public abstract class BlockGenerator implements BuiltInGenerator
{
    protected abstract Optional<BlockState> tryGenerate(LevelAccessor level, BlockPos pos, BlockState state, Direction direction);

    @Override
    public Optional<BlockState> tryGenerate(LevelAccessor level, BlockPos pos, BlockState state) {
        for (val direction : FLOW_DIRECTIONS) {
            val candidate = tryGenerate(level, pos, state, direction);
            if (candidate.isEmpty()) continue;
            return candidate;
        }

        return Optional.empty();
    }

}