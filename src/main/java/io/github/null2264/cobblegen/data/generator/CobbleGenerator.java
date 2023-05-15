package io.github.null2264.cobblegen.data.generator;

import io.github.null2264.cobblegen.config.WeightedBlock;
import io.github.null2264.cobblegen.data.model.BlockGenerator;
import io.github.null2264.cobblegen.data.model.Generator;
import io.github.null2264.cobblegen.util.GeneratorType;
import lombok.val;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.WorldAccess;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

import static io.github.null2264.cobblegen.CobbleGen.getCompat;

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
    public Optional<BlockState> tryGenerate(WorldAccess world, BlockPos pos, BlockState state, Direction direction) {
        BlockPos blockPos = pos.offset(direction.getOpposite());
        return tryGenerate(world, pos, state.getFluidState(), world.getFluidState(blockPos));
    }

    @Override
    public Optional<BlockState> tryGenerate(WorldAccess world, BlockPos pos, FluidState source, FluidState neighbour) {
        if (Generator.getStillFluid(neighbour) == getFluid())
            return getBlockCandidate(world, pos);
        return Optional.empty();
    }

    public void toPacket(PacketByteBuf buf) {
        buf.writeString(this.getClass().getName());
        val outMap = getOutput();
        buf.writeInt(outMap.size());

        for (Map.Entry<String, List<WeightedBlock>> out : outMap.entrySet()) {
            buf.writeString(out.getKey());

            val blocks = out.getValue();
            buf.writeInt(blocks.size());

            for (WeightedBlock block : blocks) {
                block.toPacket(buf);
            }
        }

        buf.writeIdentifier(getCompat().getFluidId(fluid));
        buf.writeBoolean(silent);
    }

    @SuppressWarnings("unused")
    public static Generator fromPacket(PacketByteBuf buf) {
        val _outSize = buf.readInt();
        val outMap = new HashMap<String, List<WeightedBlock>>(_outSize);
        for (int i = 0; i < _outSize; i++) {
            val key = buf.readString();

            val _blocksSize = buf.readInt();
            val blocks = new ArrayList<WeightedBlock>(_blocksSize);

            for (int j = 0; j < _blocksSize; j++) {
                blocks.add(WeightedBlock.fromPacket(buf));
            }
            outMap.put(key, blocks);
        }

        val fluid = getCompat().getFluid(buf.readIdentifier());
        val silent = buf.readBoolean();
        return new CobbleGenerator(outMap, fluid, silent);
    }
}