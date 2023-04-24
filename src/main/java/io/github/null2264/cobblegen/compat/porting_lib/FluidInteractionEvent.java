package io.github.null2264.cobblegen.compat.porting_lib;

import io.github.fabricators_of_create.porting_lib.event.common.FluidPlaceBlockCallback;
import io.github.null2264.cobblegen.util.BlockGenerator;
import io.github.null2264.cobblegen.util.GeneratorType;
import lombok.val;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.fluid.LavaFluid;
import net.minecraft.fluid.WaterFluid;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;

import static net.minecraft.client.render.WorldRenderer.DIRECTIONS;

public class FluidInteractionEvent
{
    public static BlockState whenFluidsMeet(WorldAccess world, BlockPos pos, BlockState state) {
        FluidState fluidState = state.getFluidState();
        if (fluidState.isEmpty() || (fluidState.isStill() && fluidState.isOf(Fluids.LAVA))) return null;

        FluidState fluidStateAbove = world.getFluidState(pos.up());
        if (fluidState.getFluid() instanceof WaterFluid && fluidStateAbove.getFluid() instanceof LavaFluid) {
            val generator = new BlockGenerator(
                    (World) world,
                    pos,
                    GeneratorType.STONE
            );
            return generator.getReplacement();
        }

        for (Direction direction : DIRECTIONS) {
            if (direction == Direction.UP) continue;

            FluidState metFluidState = fluidState.isStill() ? fluidState : world.getFluidState(pos.offset(direction));
            BlockPos blockPos = pos.offset(direction.getOpposite());
            if (metFluidState.getFluid() instanceof WaterFluid) {
                val generator = new BlockGenerator(
                        (World) world,
                        pos,
                        GeneratorType.COBBLE
                );
                return generator.getReplacement();
            } else if (world.getBlockState(blockPos).isOf(Blocks.BLUE_ICE)) {
                val generator = new BlockGenerator((World) world, pos, GeneratorType.BASALT);
                return generator.getReplacement();
            }
        }
        return null;
    }

    public static void register() {
        FluidPlaceBlockCallback.EVENT.register(FluidInteractionEvent::whenFluidsMeet);
    }
}