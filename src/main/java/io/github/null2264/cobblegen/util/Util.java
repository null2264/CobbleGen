package io.github.null2264.cobblegen.util;

import io.github.null2264.cobblegen.config.WeightedBlock;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import static io.github.null2264.cobblegen.CobbleGen.MOD_ID;
import static io.github.null2264.cobblegen.CobbleGen.getCompat;
import static io.github.null2264.cobblegen.config.ConfigHelper.CONFIG;

public class Util
{
    // https://stackoverflow.com/a/6737362
    @Deprecated
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

    public static Identifier identifierOf(GeneratorType type) {
        return new Identifier(MOD_ID, type.name().toLowerCase());
    }

    public static Identifier identifierOf(String id) {
        return new Identifier(MOD_ID, id);
    }

    @NotNull
    @Deprecated
    public static Pair<List<WeightedBlock>, Map<String, List<WeightedBlock>>> configFromType(GeneratorType type) {
        Map<String, List<WeightedBlock>> customGen;
        List<WeightedBlock> fallback;
        switch (type) {
            case COBBLE -> {
                customGen = CONFIG.customGen.cobbleGen;
                fallback = CONFIG.cobbleGen;
            }
            case STONE -> {
                customGen = CONFIG.customGen.stoneGen;
                fallback = CONFIG.stoneGen;
            }
            case BASALT -> {
                customGen = CONFIG.customGen.basaltGen;
                fallback = CONFIG.basaltGen;
            }
            default -> {
                customGen = Map.of();
                fallback = List.of();
            }
        }
        return new Pair<>(fallback, customGen);
    }
}