package io.github.null2264.cobblegen.mc;

import com.google.common.collect.ImmutableList;
import net.minecraft.core.Direction;

// FIXME: Move back to :mclib once I figure out how to fix that stupid "NoClassFound" error on Forge-alike
public final class Constants {

    private Constants() {}

    public static final ImmutableList<Direction> FLOW_DIRECTIONS = ImmutableList.of(
        Direction.DOWN, Direction.SOUTH, Direction.NORTH, Direction.EAST, Direction.WEST
    );
    public static final ImmutableList<Direction> DIRECTIONS = ImmutableList.of(
        Direction.UP, Direction.DOWN, Direction.SOUTH, Direction.NORTH, Direction.EAST, Direction.WEST
    );
}
