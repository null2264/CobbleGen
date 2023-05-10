package io.github.null2264.cobblegen.data;

import io.github.null2264.cobblegen.config.WeightedBlock;
import io.github.null2264.cobblegen.util.GeneratorType;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.fluid.Fluid;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.WorldAccess;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static io.github.null2264.cobblegen.CobbleGen.getCompat;

public class BasaltGenerator extends BlockGenerator
{
    private final Map<String, List<WeightedBlock>> possibleBlocks;
    private final Block block;
    private final boolean silent;

    public BasaltGenerator(List<WeightedBlock> possibleBlocks, Block block, boolean silent) {
        this(Map.of(getCompat().getBlockId(Blocks.SOUL_SOIL).toString(), possibleBlocks), block, silent);
    }

    public BasaltGenerator(Map<String, List<WeightedBlock>> possibleBlocks, Block block, boolean silent) {
        this.possibleBlocks = possibleBlocks;
        this.block = block;
        this.silent = silent;
    }

    @Override
    public @NotNull Map<String, List<WeightedBlock>> getOutput() {
        return possibleBlocks;
    }

    @Override
    public @NotNull GeneratorType getType() {
        return GeneratorType.BASALT;
    }

    @Override
    public Fluid getFluid() {
        return null;
    }

    @NotNull
    @Override
    public Block getBlock() {
        return block;
    }

    @Override
    public boolean isSilent() {
        return silent;
    }

    @Override
    public Optional<BlockState> tryGenerate(WorldAccess world, BlockPos pos, BlockState state, Direction direction) {
        BlockPos blockPos = pos.offset(direction.getOpposite());
        if (world.getBlockState(blockPos).getBlock() == getBlock())
            return getBlockCandidate(world, pos);
        return Optional.empty();
    }
}