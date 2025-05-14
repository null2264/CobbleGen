package io.github.null2264.cobblegen.mc;

import com.google.common.collect.ImmutableList;
import net.minecraft.core.Direction;

public final class Constants {

    private Constants() {}

    public static final ImmutableList<Direction> FLOW_DIRECTIONS = ImmutableList.of(
        Direction.DOWN, Direction.SOUTH, Direction.NORTH, Direction.EAST, Direction.WEST
    );
}
