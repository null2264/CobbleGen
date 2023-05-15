package io.github.null2264.cobblegen.data.generator;

import io.github.null2264.cobblegen.config.WeightedBlock;
import io.github.null2264.cobblegen.data.model.BlockGenerator;
import io.github.null2264.cobblegen.data.model.Generator;
import io.github.null2264.cobblegen.util.GeneratorType;
import lombok.val;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.fluid.Fluid;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.WorldAccess;
import org.jetbrains.annotations.NotNull;

import java.util.*;

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

    @Override
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

        buf.writeIdentifier(getCompat().getBlockId(block));
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

        val block = getCompat().getBlock(buf.readIdentifier());
        val silent = buf.readBoolean();
        return new BasaltGenerator(outMap, block, silent);
    }
}