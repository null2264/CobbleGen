package io.github.null2264.cobblegen.util;

import io.github.null2264.cobblegen.config.WeightedBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Util {
    // https://stackoverflow.com/a/6737362
    public static String randomizeBlockId(List<WeightedBlock> blockIds, String dim, Integer yLevel) {
        if (blockIds.size() == 1)
            return blockIds.get(0).id;

        ArrayList<WeightedBlock> filteredBlockIds = new ArrayList<>();
        double totalWeight = 0.0;

        for (WeightedBlock block : blockIds) {
            if (block.dimensions != null && !block.dimensions.contains(dim))
                continue;

            if (block.excludedDimensions != null && block.excludedDimensions.contains(dim))
                continue;

            if (block.maxY != null && block.maxY <= yLevel)
                continue;

            if (block.minY != null && block.minY >= yLevel)
                continue;

            filteredBlockIds.add(block);
            totalWeight += block.weight;
        }

        int idx = 0;
        for (double r = Math.random() * totalWeight; idx < filteredBlockIds.size() - 1; ++idx) {
            r -= filteredBlockIds.get(idx).weight;
            if (r <= 0.0) break;
        }

        if (filteredBlockIds.isEmpty())
            return null;
        return filteredBlockIds.get(idx).id;
    }

    public static List<WeightedBlock> getCustomReplacement(World world, BlockPos pos, Map<String, List<WeightedBlock>> customGen, List<WeightedBlock> fallback) {
        List<WeightedBlock> replacements = null;

        if (customGen != null)
            replacements = customGen.get(
                    Registry.BLOCK.getId(
                            world.getBlockState(pos.down()).getBlock()).toString()
            );

        if (replacements == null || replacements.isEmpty())
            replacements = fallback;

        return replacements;
    }
}