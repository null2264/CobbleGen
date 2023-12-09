package io.github.null2264.cobblegen.data.generator;

import io.github.null2264.cobblegen.CobbleGen;
import io.github.null2264.cobblegen.data.config.ConfigMetaData;
import io.github.null2264.cobblegen.data.config.WeightedBlock;
import io.github.null2264.cobblegen.data.CGIdentifier;
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
import java.util.stream.Collectors;

public class CobbleGenerator extends BlockGenerator
{
    private final Map<CGIdentifier, List<WeightedBlock>> possibleBlocks;
    private final Map<CGIdentifier, List<WeightedBlock>> obsidianReplacements;
    private Fluid fluid;
    private final boolean silent;

    public CobbleGenerator(List<WeightedBlock> possibleBlocks, Fluid fluid, boolean silent) {
        this(possibleBlocks, fluid, silent, Map.of());
    }

    public CobbleGenerator(List<WeightedBlock> possibleBlocks, Fluid fluid, boolean silent, Map<CGIdentifier, List<WeightedBlock>> obsidianReplacements) {
        this(Map.of(CGIdentifier.wildcard(), possibleBlocks), fluid, silent, obsidianReplacements);
    }

    public CobbleGenerator(Map<CGIdentifier, List<WeightedBlock>> possibleBlocks, Fluid fluid, boolean silent, Map<CGIdentifier, List<WeightedBlock>> obsidianReplacements) {
        this.possibleBlocks = possibleBlocks;
        this.obsidianReplacements = obsidianReplacements;
        this.fluid = fluid;
        this.silent = silent;
    }

    public static CobbleGenerator fromString(Map<String, List<WeightedBlock>> possibleBlocks, Fluid fluid, boolean silent, Map<String, List<WeightedBlock>> obsidianReplacements) {
        return new CobbleGenerator(
                possibleBlocks.entrySet().stream()
                        .map(e -> Map.entry(CGIdentifier.of(e.getKey()), e.getValue()))
                        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)),
                fluid,
                silent,
                obsidianReplacements.entrySet().stream()
                        .map(e -> Map.entry(CGIdentifier.of(e.getKey()), e.getValue()))
                        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue))
        );
    }

    @Override
    public @NotNull Map<CGIdentifier, List<WeightedBlock>> getOutput() {
        return possibleBlocks;
    }

    @Override
    public Map<CGIdentifier, List<WeightedBlock>> getObsidianOutput() {
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

    @Override
    public void toPacket(FriendlyByteBuf buf) {
        buf.writeUtf(this.getClass().getName());

        buf.writeResourceLocation(Util.getFluidId(fluid));
        buf.writeBoolean(silent);

        buf.writeMap(
                getOutput(),
                (o, key) -> key.writeToBuf(o), (o, blocks) -> o.writeCollection(blocks, (p, block) -> block.toPacket(p))
        );
        buf.writeMap(
                getObsidianOutput(),
                (o, key) -> key.writeToBuf(o), (o, blocks) -> o.writeCollection(blocks, (p, block) -> block.toPacket(p))
        );
    }

    @SuppressWarnings("unused")
    public static Generator fromPacket(FriendlyByteBuf buf) {
        val fluid = Util.getFluid(buf.readResourceLocation());
        val silent = buf.readBoolean();

        Map<CGIdentifier, List<WeightedBlock>> outMap =
                buf.readMap(CGIdentifier::readFromBuf, (o) -> o.readList(WeightedBlock::fromPacket));
        Map<CGIdentifier, List<WeightedBlock>> obiMap =
                buf.readMap(CGIdentifier::readFromBuf, (o) -> o.readList(WeightedBlock::fromPacket));

        return new CobbleGenerator(outMap, fluid, silent, obiMap);
    }
}