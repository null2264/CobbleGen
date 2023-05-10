package io.github.null2264.cobblegen.data;

import io.github.null2264.cobblegen.config.WeightedBlock;
import io.github.null2264.cobblegen.util.GeneratorType;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldAccess;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public class StoneGenerator implements BuiltInGenerator
{
    private final Map<String, List<WeightedBlock>> possibleBlocks;
    private final Fluid fluid;
    private final boolean silent;

    public StoneGenerator(List<WeightedBlock> possibleBlocks, Fluid fluid, boolean silent) {
        this(Map.of("*", possibleBlocks), fluid, silent);
    }

    public StoneGenerator(Map<String, List<WeightedBlock>> possibleBlocks, Fluid fluid, boolean silent) {
        this.possibleBlocks = possibleBlocks;
        this.fluid = fluid;
        this.silent = silent;
    }

    @Override
    public @NotNull Map<String, List<WeightedBlock>> getOutput() {
        return possibleBlocks;
    }

    @Override
    public @NotNull GeneratorType getType() {
        return GeneratorType.STONE;
    }

    @Override
    public Fluid getFluid() {
        return fluid;
    }

    @Override
    public @Nullable Block getBlock() {
        return null;
    }

    @Override
    public boolean isSilent() {
        return silent;
    }

    @Override
    public boolean check(WorldAccess world, BlockPos pos, BlockState state, boolean fromTop) {
        return fromTop;
    }

    @Override
    public Optional<BlockState> tryGenerate(WorldAccess world, BlockPos pos, BlockState state) {
        return tryGenerate(world, pos, state.getFluidState(), world.getFluidState(pos));
    }

    @Override
    public Optional<BlockState> tryGenerate(WorldAccess world, BlockPos pos, FluidState source, FluidState neighbour) {
        Fluid fluid = Generator.getStillFluid(neighbour);
        if (getFluid() == fluid) {
            return getBlockCandidate(world, pos);
        }

        return Optional.empty();
    }
}