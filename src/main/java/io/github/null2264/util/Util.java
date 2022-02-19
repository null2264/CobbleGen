package io.github.null2264.util;

import java.util.List;

public class Util
{
    // https://stackoverflow.com/a/6737362
    public static String randomizeBlockId(List<WeightedBlock> blockIds) {
        double totalWeight = 0.0;

        for (WeightedBlock block : blockIds) {
            totalWeight += block.getWeight();
        }

        int idx = 0;
        for (double r = Math.random() * totalWeight; idx < blockIds.size() - 1; ++idx) {
            r -= blockIds.get(idx).getWeight();
            if (r <= 0.0) break;
        }

        return blockIds.get(idx).getId();
    }
}