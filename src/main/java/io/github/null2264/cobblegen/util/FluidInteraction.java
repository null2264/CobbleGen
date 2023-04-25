package io.github.null2264.cobblegen.util;

import io.github.null2264.cobblegen.config.WeightedBlock;
import lombok.val;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.fluid.FlowableFluid;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldEvents;

import java.util.List;
import java.util.Map;

import static io.github.null2264.cobblegen.CobbleGen.getCompat;
import static io.github.null2264.cobblegen.config.ConfigHelper.CONFIG;
import static net.minecraft.client.render.WorldRenderer.DIRECTIONS;

public class FluidInteraction
{
    public static boolean doInteraction(WorldAccess world, BlockPos pos, FlowableFluid fluid) {
        return doInteraction(world, pos, fluid, false);
    }

    public static boolean doInteraction(WorldAccess world, BlockPos pos, FlowableFluid fluid, boolean fromTop) {
        if (CONFIG.advanced == null || CONFIG.advanced.isEmpty()) return false;

        Identifier flowingId;
        flowingId = getCompat().getFluidId(fluid.getStill());

        val flowingConfig = CONFIG.advanced.get(flowingId.toString());
        if (flowingConfig == null) return false;

        Block blockBelow = world.getBlockState(pos.down()).getBlock();
        for (Direction direction : DIRECTIONS) {
            if (direction == Direction.UP) continue;
            if (fromTop && direction != Direction.DOWN) continue;

            BlockPos blockPos = fromTop ? pos : pos.offset(direction.getOpposite());

            Identifier id;
            boolean isFluid = false;

            BlockState blockState = world.getBlockState(blockPos);
            FluidState fluidState = blockState.getFluidState();
            if (!fluidState.isEmpty()) {
                Fluid curFluid = fluidState.getFluid();
                if (curFluid instanceof FlowableFluid) curFluid = ((FlowableFluid) curFluid).getStill();

                id = getCompat().getFluidId(curFluid);
                isFluid = true;
            } else {
                if (fromTop) return false;
                id = getCompat().getBlockId(blockState.getBlock());
            }

            val rootConfig = flowingConfig.get((isFluid ? "" : "b:") + id.toString());
            if (rootConfig == null) continue;

            Map<String, List<WeightedBlock>> results;
            if (!isFluid) results = rootConfig.results;
            else results = fromTop ? rootConfig.resultsFromTop : rootConfig.results;
            if (results == null) continue;

            val possibleGens = results.getOrDefault(getCompat().getBlockId(blockBelow).toString(), results.get("*"));
            if (possibleGens == null) continue;

            val replacement = new BlockGenerator(world, pos, possibleGens).getReplacement();
            if (replacement != null) {
                world.setBlockState(pos, replacement, 3);
                world.syncWorldEvent(WorldEvents.LAVA_EXTINGUISHED, pos, 0);
                return true;
            }
        }
        return false;
    }
}