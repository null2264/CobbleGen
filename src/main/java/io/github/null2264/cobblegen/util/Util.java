package io.github.null2264.cobblegen.util;

import io.github.null2264.cobblegen.config.WeightedBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;

import java.util.List;
import java.util.Map;

import static io.github.null2264.cobblegen.CobbleGen.CONFIG;

public class Util
{
    // https://stackoverflow.com/a/6737362
    public static String randomizeBlockId(List<WeightedBlock> blockIds) {
        if (blockIds.size() == 1)
            return blockIds.get(0).id;

        double totalWeight = 0.0;

        for (WeightedBlock block : blockIds) {
            totalWeight += block.weight;
        }

        int idx = 0;
        for (double r = Math.random() * totalWeight; idx < blockIds.size() - 1; ++idx) {
            r -= blockIds.get(idx).weight;
            if (r <= 0.0) break;
        }

        return blockIds.get(idx).id;
    }

    public static List<WeightedBlock> getCustomReplacement(World world, BlockPos pos, Map<String, List<WeightedBlock>> customGen, List<WeightedBlock> fallback) {
        List<WeightedBlock> replacements = null;

        if (customGen != null)
            replacements = customGen.get(
                Registry.BLOCK.getId(
                    world.getBlockState(pos.down()).getBlock()).toString()
            );

        if (replacements == null || replacements.size() < 1)
            replacements = fallback;

        return replacements;
    }
}