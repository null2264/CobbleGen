package io.github.null2264.cobblegen.util;

import io.github.null2264.cobblegen.config.WeightedBlock;

import java.util.List;

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
}