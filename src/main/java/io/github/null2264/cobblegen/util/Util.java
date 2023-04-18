package io.github.null2264.cobblegen.util;

import io.github.null2264.cobblegen.config.WeightedBlock;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import static io.github.null2264.cobblegen.CobbleGen.getCompat;

public class Util
{
    // https://stackoverflow.com/a/6737362
    public static String randomizeBlockId(List<WeightedBlock> blockIds, String dim, Integer yLevel) {
        if (blockIds.size() == 1) return blockIds.get(0).id;

        ArrayList<WeightedBlock> filteredBlockIds = new ArrayList<>();
        AtomicReference<Double> totalWeight = new AtomicReference<>(0.0);

        for (WeightedBlock block : blockIds) {
            if (block.dimensions != null && !block.dimensions.contains(dim)) continue;

            if (block.excludedDimensions != null && block.excludedDimensions.contains(dim)) continue;

            if (block.maxY != null && block.maxY <= yLevel) continue;

            if (block.minY != null && block.minY >= yLevel) continue;

            if (block.id.startsWith("#")) {
                List<Identifier> ids = getCompat().getTaggedBlockIds(new Identifier(block.id.substring(1)));
                for (Identifier blockId : ids) {
                    filteredBlockIds.add(new WeightedBlock(blockId.toString(), block.weight));
                    totalWeight.updateAndGet(v -> v + block.weight);
                }
            } else {
                filteredBlockIds.add(block);
                totalWeight.updateAndGet(v -> v + block.weight);
            }
        }

        int idx = 0;
        for (double r = Math.random() * totalWeight.get(); idx < filteredBlockIds.size() - 1; ++idx) {
            r -= filteredBlockIds.get(idx).weight;
            if (r <= 0.0) break;
        }

        if (filteredBlockIds.isEmpty()) return null;
        return filteredBlockIds.get(idx).id;
    }
}