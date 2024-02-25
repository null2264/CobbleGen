package io.github.null2264.cobblegen.data.generator;

import io.github.null2264.cobblegen.CobbleGen;
import io.github.null2264.cobblegen.compat.ByteBufCompat;
import io.github.null2264.cobblegen.data.CGIdentifier;
import io.github.null2264.cobblegen.data.Pair;
import io.github.null2264.cobblegen.data.config.ConfigMetaData;
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
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public class CobbleGenerator extends BlockGenerator
{
    private final GeneratorMap possibleBlocks;
    private final GeneratorMap obsidianReplacements;
    private Fluid fluid;
    private final boolean silent;

    public CobbleGenerator(ResultList possibleBlocks, Fluid fluid, boolean silent) {
        this(possibleBlocks, fluid, silent, GeneratorMap.of());
    }

    public CobbleGenerator(ResultList possibleBlocks, Fluid fluid, boolean silent, GeneratorMap obsidianReplacements) {
        this(GeneratorMap.of(Pair.of(CGIdentifier.wildcard(), possibleBlocks)), fluid, silent, obsidianReplacements);
    }

    public CobbleGenerator(GeneratorMap possibleBlocks, Fluid fluid, boolean silent, GeneratorMap obsidianReplacements) {
        this.possibleBlocks = possibleBlocks;
        this.obsidianReplacements = obsidianReplacements;
        this.fluid = fluid;
        this.silent = silent;
    }

    public static CobbleGenerator fromString(Map<String, List<WeightedBlock>> possibleBlocks, Fluid fluid, boolean silent, Map<String, List<WeightedBlock>> obsidianReplacements) {
        final GeneratorMap map1 = new GeneratorMap();
        possibleBlocks.forEach((k, v) -> map1.put(CGIdentifier.of(k), new ResultList(v)));
        final GeneratorMap map2 = new GeneratorMap();
        obsidianReplacements.forEach((k, v) -> map2.put(CGIdentifier.of(k), new ResultList(v)));
        return new CobbleGenerator(
                map1,
                fluid,
                silent,
                map2
        );
    }

    @Override
    public @NotNull GeneratorMap getOutput() {
        return possibleBlocks;
    }

    @Override
    public GeneratorMap getObsidianOutput() {
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
    public void toPacket(ByteBufCompat buf) {
        buf.writeUtf(this.getClass().getName());

        buf.writeResourceLocation(Util.getFluidId(fluid));
        buf.writeBoolean(silent);

        getOutput().toPacket(buf);
        getObsidianOutput().toPacket(buf);
    }

    @SuppressWarnings("unused")
    public static Generator fromPacket(FriendlyByteBuf buf) {
        final Fluid fluid = Util.getFluid(buf.readResourceLocation());
        final boolean silent = buf.readBoolean();

        GeneratorMap outMap = GeneratorMap.fromPacket(buf);
        GeneratorMap obiMap = GeneratorMap.fromPacket(buf);

        return new CobbleGenerator(outMap, fluid, silent, obiMap);
    }
}