package io.github.null2264.cobblegen.data.generator;

import io.github.null2264.cobblegen.config.WeightedBlock;
import io.github.null2264.cobblegen.data.model.BlockGenerator;
import io.github.null2264.cobblegen.data.model.Generator;
import io.github.null2264.cobblegen.util.GeneratorType;
import io.github.null2264.cobblegen.util.Util;
import lombok.val;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class CobbleGenerator extends BlockGenerator
{
    private final Map<String, List<WeightedBlock>> possibleBlocks;
    private Fluid fluid;
    private final boolean silent;

    public CobbleGenerator(List<WeightedBlock> possibleBlocks, Fluid fluid, boolean silent) {
        this(Map.of("*", possibleBlocks), fluid, silent);
    }

    public CobbleGenerator(Map<String, List<WeightedBlock>> possibleBlocks, Fluid fluid, boolean silent) {
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
        return GeneratorType.COBBLE;
    }

    @Override
    public Fluid getFluid() {
        return fluid;
    }

    @Override
    public void setFluid(Fluid fluid) {
        this.fluid = fluid;
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
    public Optional<BlockState> tryGenerate(LevelAccessor level, BlockPos pos, BlockState state, Direction direction) {
        BlockPos blockPos = pos.relative(direction.getOpposite());
        return tryGenerate(level, pos, state.getFluidState(), level.getFluidState(blockPos));
    }

    @Override
    public Optional<BlockState> tryGenerate(LevelAccessor level, BlockPos pos, FluidState source, FluidState neighbour) {
        if (Generator.getStillFluid(neighbour) == getFluid()) {
            if (source.getType() == Fluids.LAVA && source.isSource())
                return Optional.of(Blocks.OBSIDIAN.defaultBlockState());

            return getBlockCandidate(level, pos);
        }
        return Optional.empty();
    }

    public void toPacket(FriendlyByteBuf buf) {
        buf.writeUtf(this.getClass().getName());
        val outMap = getOutput();
        buf.writeInt(outMap.size());

        for (Map.Entry<String, List<WeightedBlock>> out : outMap.entrySet()) {
            buf.writeUtf(out.getKey());

            val blocks = out.getValue();
            buf.writeInt(blocks.size());

            for (WeightedBlock block : blocks) {
                block.toPacket(buf);
            }
        }

        buf.writeResourceLocation(Util.getFluidId(fluid));
        buf.writeBoolean(silent);
    }

    @SuppressWarnings("unused")
    public static Generator fromPacket(FriendlyByteBuf buf) {
        val _outSize = buf.readInt();
        val outMap = new HashMap<String, List<WeightedBlock>>(_outSize);
        for (int i = 0; i < _outSize; i++) {
            val key = buf.readUtf();

            val _blocksSize = buf.readInt();
            val blocks = new ArrayList<WeightedBlock>(_blocksSize);

            for (int j = 0; j < _blocksSize; j++) {
                blocks.add(WeightedBlock.fromPacket(buf));
            }
            outMap.put(key, blocks);
        }

        val fluid = Util.getFluid(buf.readResourceLocation());
        val silent = buf.readBoolean();
        return new CobbleGenerator(outMap, fluid, silent);
    }
}