package io.github.null2264.cobblegen.data.generator;

import io.github.null2264.cobblegen.config.WeightedBlock;
import io.github.null2264.cobblegen.data.model.BlockGenerator;
import io.github.null2264.cobblegen.data.model.Generator;
import io.github.null2264.cobblegen.util.CGLog;
import io.github.null2264.cobblegen.util.GeneratorType;
import io.github.null2264.cobblegen.util.Util;
import lombok.val;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class BasaltGenerator extends BlockGenerator
{
    private final Map<String, List<WeightedBlock>> possibleBlocks;
    private final Block block;
    private final boolean silent;

    public BasaltGenerator(List<WeightedBlock> possibleBlocks, Block block, boolean silent) {
        this(Map.of(Util.getBlockId(Blocks.SOUL_SOIL).toString(), possibleBlocks), block, silent);
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
    public Optional<BlockState> tryGenerate(LevelAccessor level, BlockPos pos, BlockState state, Direction direction) {
        BlockPos blockPos = pos.relative(direction.getOpposite());
        if (level.getBlockState(blockPos).getBlock() == getBlock())
            return getBlockCandidate(level, pos);
        return Optional.empty();
    }

    @Override
    public void toPacket(FriendlyByteBuf buf) {
        buf.writeUtf(this.getClass().getName());

        buf.writeUtf(Util.getBlockId(block).toString());
        buf.writeBoolean(silent);

        val outMap = getOutput();
        buf.writeMap(
                outMap,
                FriendlyByteBuf::writeUtf, (o, blocks) -> o.writeCollection(blocks, (p, block) -> block.toPacket(p))
        );
    }

    @SuppressWarnings("unused")
    public static Generator fromPacket(FriendlyByteBuf buf) {
        val block = Util.getBlock(buf.readResourceLocation());
        val silent = buf.readBoolean();

        Map<String, List<WeightedBlock>> outMap =
                buf.readMap(FriendlyByteBuf::readUtf, (o) -> o.readList(WeightedBlock::fromPacket));

        return new BasaltGenerator(outMap, block, silent);
    }
}