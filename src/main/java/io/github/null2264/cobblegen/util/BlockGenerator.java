package io.github.null2264.cobblegen.util;

import io.github.null2264.cobblegen.config.WeightedBlock;
import net.minecraft.block.BlockState;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;

public class BlockGenerator {
    private final List<WeightedBlock> expectedBlocks;
    private final World world;
    private final BlockPos pos;

    public BlockGenerator(World world, BlockPos pos, Map<String, List<WeightedBlock>> customGen, List<WeightedBlock> fallback) {
        this.expectedBlocks = Util.getCustomReplacement(world, pos, customGen, fallback);
        this.world = world;
        this.pos = pos;
    }

    public @Nullable BlockState getReplacement() {
        String replacementId = null;
        if (expectedBlocks != null && !expectedBlocks.isEmpty())
            replacementId = Util.randomizeBlockId(
                    expectedBlocks,
                    world.getRegistryKey().getValue().toString(),
                    pos.getY()
            );

        if (replacementId == null)
            return null;

        return Registry.BLOCK.get(new Identifier(replacementId)).getDefaultState();
    }

    public List<WeightedBlock> getExpectedBlocks() {
        return expectedBlocks;
    }
}