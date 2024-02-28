package io.github.null2264.cobblegen.data.generator;

import io.github.null2264.cobblegen.data.CGIdentifier;
import io.github.null2264.cobblegen.data.Pair;
import io.github.null2264.cobblegen.data.config.GeneratorMap;
import io.github.null2264.cobblegen.data.config.ResultList;
import io.github.null2264.cobblegen.data.config.WeightedBlock;
import io.github.null2264.cobblegen.data.model.BlockGenerator;
import io.github.null2264.cobblegen.data.model.Generator;
import io.github.null2264.cobblegen.util.GeneratorType;
import io.github.null2264.cobblegen.util.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public class BasaltGenerator extends BlockGenerator
{
    private final GeneratorMap possibleBlocks;
    private final Block block;
    private final boolean silent;

    public BasaltGenerator(ResultList possibleBlocks, Block block, boolean silent) {
        this(GeneratorMap.of(Pair.of(CGIdentifier.fromMC(Util.getBlockId(Blocks.SOUL_SOIL)), possibleBlocks)), block, silent);
    }

    public BasaltGenerator(GeneratorMap possibleBlocks, Block block, boolean silent) {
        this.possibleBlocks = possibleBlocks;
        this.block = block;
        this.silent = silent;
    }

    public static BasaltGenerator fromString(Map<String, List<WeightedBlock>> possibleBlocks, Block block, boolean silent) {
        final GeneratorMap map = new GeneratorMap();
        possibleBlocks.forEach((k, v) -> map.put(CGIdentifier.of(k), new ResultList(v)));
        return new BasaltGenerator(
                map,
                block,
                silent
        );
    }

    @Override
    public @NotNull GeneratorMap getOutput() {
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
    public Optional<BlockState> tryGenerate(LevelAccessor level, BlockPos pos, BlockState state, Direction direction) {
        BlockPos blockPos = pos.relative(direction.getOpposite());
        if (level.getBlockState(blockPos).getBlock() == getBlock())
            return getBlockCandidate(level, pos, getOutput());
        return Optional.empty();
    }

    @Override
    public void toPacket(FriendlyByteBuf buf) {
        buf.writeUtf(this.getClass().getName());

        buf.writeUtf(Util.getBlockId(block).toString());
        buf.writeBoolean(silent);

        getOutput().toPacket(buf);
    }

    @SuppressWarnings("unused")
    public static Generator fromPacket(FriendlyByteBuf buf) {
        final Block block = Util.getBlock(buf.readResourceLocation());
        final boolean silent = buf.readBoolean();

        GeneratorMap outMap = GeneratorMap.fromPacket(buf);

        return new BasaltGenerator(outMap, block, silent);
    }
}