package io.github.null2264.cobblegen.data.generator;

import io.github.null2264.cobblegen.CobbleGen;
import io.github.null2264.cobblegen.compat.ByteBufCompat;
import io.github.null2264.cobblegen.config.ConfigMetaData;
import io.github.null2264.cobblegen.config.WeightedBlock;
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
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

import static io.github.null2264.cobblegen.compat.CollectionCompat.mapOf;

public class CobbleGenerator extends BlockGenerator
{
    private final Map<String, List<WeightedBlock>> possibleBlocks;
    private final Map<String, List<WeightedBlock>> obsidianReplacements;
    private Fluid fluid;
    private final boolean silent;

    public CobbleGenerator(List<WeightedBlock> possibleBlocks, Fluid fluid, boolean silent) {
        this(possibleBlocks, fluid, silent, mapOf());
    }

    public CobbleGenerator(List<WeightedBlock> possibleBlocks, Fluid fluid, boolean silent, Map<String, List<WeightedBlock>> obsidianReplacements) {
        this(mapOf("*", possibleBlocks), fluid, silent, obsidianReplacements);
    }

    public CobbleGenerator(Map<String, List<WeightedBlock>> possibleBlocks, Fluid fluid, boolean silent, Map<String, List<WeightedBlock>> obsidianReplacements) {
        this.possibleBlocks = possibleBlocks;
        this.obsidianReplacements = obsidianReplacements;
        this.fluid = fluid;
        this.silent = silent;
    }

    @Override
    public @NotNull Map<String, List<WeightedBlock>> getOutput() {
        return possibleBlocks;
    }

    @Override
    public Map<String, List<WeightedBlock>> getObsidianOutput() {
        return obsidianReplacements;
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
            if (source.getType() == Fluids.LAVA && source.isSource()) {
                if (Util.optional(CobbleGen.META_CONFIG).orElse(new ConfigMetaData()).enableExperimentalFeatures)
                    return getBlockCandidate(level, pos, getObsidianOutput(), Blocks.OBSIDIAN);
                return Optional.of(Blocks.OBSIDIAN.defaultBlockState());
            }

            return getBlockCandidate(level, pos, getOutput());
        }
        return Optional.empty();
    }

    @SuppressWarnings("RedundantCast")
    @Override
    public void toPacket(ByteBufCompat buf) {
        buf.writeUtf(this.getClass().getName());

        buf.writeResourceLocation(Util.getFluidId(fluid));
        buf.writeBoolean(silent);

        buf.writeMap(
                getOutput(),
                FriendlyByteBuf::writeUtf, (o, blocks) -> ((ByteBufCompat) o).writeCollection(blocks, (p, block) -> block.toPacket((ByteBufCompat) p))
        );
        buf.writeMap(
                getObsidianOutput(),
                FriendlyByteBuf::writeUtf, (o, blocks) -> ((ByteBufCompat) o).writeCollection(blocks, (p, block) -> block.toPacket((ByteBufCompat) p))
        );
    }

    @SuppressWarnings({"unused", "RedundantCast"})
    public static Generator fromPacket(FriendlyByteBuf buf) {
        final Fluid fluid = Util.getFluid(buf.readResourceLocation());
        final boolean silent = buf.readBoolean();

        Map<String, List<WeightedBlock>> outMap =
                ((ByteBufCompat) buf).readMap(FriendlyByteBuf::readUtf, (o) -> ((ByteBufCompat) o).readList(WeightedBlock::fromPacket));
        Map<String, List<WeightedBlock>> obiMap =
                ((ByteBufCompat) buf).readMap(FriendlyByteBuf::readUtf, (o) -> ((ByteBufCompat) o).readList(WeightedBlock::fromPacket));

        return new CobbleGenerator(outMap, fluid, silent, obiMap);
    }
}