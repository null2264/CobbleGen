package io.github.null2264.cobblegen.util;

import lombok.val;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.fluid.FlowableFluid;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.minecraft.world.WorldEvents;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.NoSuchElementException;

import static io.github.null2264.cobblegen.CobbleGen.getCompat;
import static io.github.null2264.cobblegen.config.ConfigHelper.CONFIG;
import static net.minecraft.client.render.WorldRenderer.DIRECTIONS;

public class FluidInteraction
{
    public static boolean doInteraction(World world, BlockPos pos, BlockState state, FlowableFluid fluid) {
        Identifier flowingId;
        try {
            flowingId = getCompat().getFluidId(fluid.getStill());
        } catch (NoSuchElementException ignored) {
            return false;
        }

        val flowingConfig = CONFIG.advanced.get(flowingId.toString());
        if (flowingConfig == null) return false;

        Block blockBelow = world.getBlockState(pos.down()).getBlock();
        for (Direction direction : DIRECTIONS) {
            if (direction == Direction.UP) continue;

            BlockPos blockPos = pos.offset(direction.getOpposite());

            Identifier id;
            boolean isFluid = false;

            FluidState fluidState = world.getFluidState(blockPos);
            if (!fluidState.isEmpty()) {
                Fluid curFluid = fluidState.getFluid();
                if (curFluid instanceof FlowableFluid) curFluid = ((FlowableFluid) curFluid).getStill();

                id = getCompat().getFluidId(curFluid);
                isFluid = true;
            } else {
                BlockState blockState = world.getBlockState(blockPos);
                id = getCompat().getBlockId(blockState.getBlock());
            }

            val rootConfig = flowingConfig.getOrDefault((isFluid ? "" : "b:") + id.toString(), Map.of());
            val possibleGens = rootConfig.getOrDefault(
                    getCompat().getBlockId(blockBelow).toString(),
                    rootConfig.get("*")
            );
            if (possibleGens == null) continue;

            val replacement = new BlockGenerator(world, pos, possibleGens).getReplacement();
            if (replacement != null) {
                world.setBlockState(pos, replacement);
                world.syncWorldEvent(WorldEvents.LAVA_EXTINGUISHED, pos, 0);
                return true;
            }
        }
        return false;
    }
}