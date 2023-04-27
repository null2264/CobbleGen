package io.github.null2264.cobblegen.util;

import io.github.null2264.cobblegen.config.AdvancedGen;
import io.github.null2264.cobblegen.config.WeightedBlock;
import io.github.null2264.cobblegen.data.BlockGenerator;
import io.github.null2264.cobblegen.data.GeneratorData;
import io.github.null2264.cobblegen.data.GeneratorResult;
import lombok.val;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FluidBlock;
import net.minecraft.fluid.FlowableFluid;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldEvents;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;

import static io.github.null2264.cobblegen.CobbleGen.getCompat;
import static io.github.null2264.cobblegen.config.ConfigHelper.CONFIG;

public class FluidInteraction
{
    @Nullable
    public static GeneratorResult doAdvancedInteraction(
            WorldAccess world,
            BlockPos pos, Map<String, AdvancedGen> config,
            Direction direction,
            boolean fromTop
    ) {
        BlockPos blockPos = fromTop ? pos : pos.offset(direction.getOpposite());

        Identifier id;
        boolean isFluid;

        BlockState blockState = world.getBlockState(blockPos);
        FluidState fluidState = blockState.getFluidState();
        if (fluidState.isEmpty()) {
            if (fromTop) return null;  // fromTop is not supported for blocks

            id = getCompat().getBlockId(blockState.getBlock());
            isFluid = false;
        } else {
            Fluid curFluid = fluidState.getFluid();
            if (curFluid instanceof FlowableFluid) curFluid = ((FlowableFluid) curFluid).getStill();

            id = getCompat().getFluidId(curFluid);
            isFluid = true;
        }

        if (isFluid && fluidState.isStill()) return null;

        val rootConfig = config.get((isFluid ? "" : "b:") + id.toString());
        if (rootConfig == null) return null;

        Map<String, List<WeightedBlock>> results;
        if (isFluid) results = fromTop ? rootConfig.resultsFromTop : rootConfig.results;
        else results = rootConfig.results;
        if (results == null) return null;

        val replacement = new BlockGenerator(world, pos, results).getReplacement();
        if (replacement != null)
            return new GeneratorResult(replacement, rootConfig.silent);
        return null;
    }

    private static Fluid getStillFluid(FluidState fluidState) {
        try {
            return ((FlowableFluid) fluidState.getFluid()).getStill();
        } catch (ClassCastException ignore) {
            return fluidState.getFluid();
        }
    }

    @Nullable
    public static GeneratorResult doInteraction(WorldAccess world, BlockPos pos, BlockState state, GeneratorData generatorData) {
        FluidState fluidState = state.getFluidState();
        Fluid fluid = getStillFluid(fluidState);
        Identifier fluidId = getCompat().getFluidId(fluid);

        val config = CONFIG.advanced.get(fluidId.toString());
        boolean shouldTryAdvanced = config != null;

        boolean fromTop = generatorData.isFromTop();

        if (fromTop && !fluidState.isEmpty()) {  // Try "stone generators" first when fluid is coming from the top
            GeneratorResult result = null;
            if (shouldTryAdvanced) {
                result = doAdvancedInteraction(world, pos, config, Direction.DOWN, true);
                shouldTryAdvanced = false;
            }

            if (result == null) {
                FluidState fluidStateAbove = world.getFluidState(pos.up());
                if (fluidStateAbove.isIn(FluidTags.LAVA)) {
                    val generator = new BlockGenerator(world, pos, GeneratorType.STONE);
                    result = new GeneratorResult(generator.getReplacement(), false);
                }
            }

            if (result != null)
                return result;
        }

        for (Direction direction : FluidBlock.FLOW_DIRECTIONS) {
            GeneratorResult result = null;
            if (shouldTryAdvanced)
                result = doAdvancedInteraction(world, pos, config, direction, false);

            if (result == null) {
                BlockGenerator generator = null;
                BlockPos blockPos = pos.offset(direction.getOpposite());

                if (fluid == Fluids.LAVA && !fluidState.isStill()){
                    if (world.getFluidState(blockPos).isIn(FluidTags.WATER)) {
                        generator = new BlockGenerator(world, pos, GeneratorType.COBBLE);
                    } else if (world.getBlockState(blockPos).isOf(Blocks.BLUE_ICE)) {
                        generator = new BlockGenerator(world, pos, GeneratorType.BASALT);
                    }
                }

                if (generator == null)
                    continue;

                result = new GeneratorResult(generator.getReplacement(), false);
            }

            return result;
        }
        return null;
    }

    public static boolean interact(WorldAccess world, BlockPos pos, BlockState state) {
        return interact(world, pos, state, new GeneratorData(false));
    }

    public static boolean interact(
            WorldAccess world,
            BlockPos pos,
            BlockState state,
            GeneratorData generatorData
    ) {
        val result = doInteraction(world, pos, state, generatorData);
        if (result != null) {
            world.setBlockState(pos, result.getBlockState(), 3);
            if (!result.isSilent())
                world.syncWorldEvent(WorldEvents.LAVA_EXTINGUISHED, pos, 0);
            return true;
        }
        return false;
    }
}