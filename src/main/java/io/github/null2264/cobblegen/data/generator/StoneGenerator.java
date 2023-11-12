package io.github.null2264.cobblegen.data.generator;

import io.github.null2264.cobblegen.config.WeightedBlock;
import io.github.null2264.cobblegen.data.model.BuiltInGenerator;
import io.github.null2264.cobblegen.data.model.Generator;
import io.github.null2264.cobblegen.util.GeneratorType;
import io.github.null2264.cobblegen.util.Util;
import lombok.val;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

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
    public Map<String, List<WeightedBlock>> getObsidianOutput() {
        return Map.of("*", List.of(WeightedBlock.fromBlock(Blocks.STONE, 100D)));
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
    public boolean check(LevelAccessor level, BlockPos pos, BlockState state, boolean fromTop) {
        return fromTop;
    }

    @Override
    public Optional<BlockState> tryGenerate(LevelAccessor level, BlockPos pos, BlockState state) {
        return tryGenerate(level, pos, state.getFluidState(), level.getFluidState(pos));
    }

    @Override
    public Optional<BlockState> tryGenerate(LevelAccessor level, BlockPos pos, FluidState source, FluidState neighbour) {
        Fluid fluid = Generator.getStillFluid(neighbour);
        if (getFluid() == fluid) {
            return getBlockCandidate(level, pos, getOutput());
        }

        return Optional.empty();
    }

    @Override
    public void toPacket(FriendlyByteBuf buf) {
        buf.writeUtf(this.getClass().getName());

        buf.writeResourceLocation(Util.getFluidId(fluid));
        buf.writeBoolean(silent);

        val outMap = getOutput();
        buf.writeMap(
                outMap,
                FriendlyByteBuf::writeUtf, (o, blocks) -> o.writeCollection(blocks, (p, block) -> block.toPacket(p))
        );
    }

    @SuppressWarnings("unused")
    public static Generator fromPacket(FriendlyByteBuf buf) {
        val fluid = Util.getFluid(buf.readResourceLocation());
        val silent = buf.readBoolean();

        Map<String, List<WeightedBlock>> outMap =
                buf.readMap(FriendlyByteBuf::readUtf, (o) -> o.readList(WeightedBlock::fromPacket));

        return new StoneGenerator(outMap, fluid, silent);
    }
}