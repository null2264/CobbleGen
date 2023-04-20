package io.github.null2264.cobblegen.config;

import net.minecraft.block.Block;
import net.minecraft.util.Identifier;

import java.util.List;

import static io.github.null2264.cobblegen.CobbleGen.getCompat;

public class WeightedBlock
{
    public String id;
    public Double weight;
    public List<String> dimensions;
    public List<String> excludedDimensions;
    public Integer maxY;
    public Integer minY;

    public WeightedBlock(String id, Double weight) {
        this(id, weight, null, null);
    }

    public WeightedBlock(String id, Double weight, List<String> dimIds) {
        this(id, weight, dimIds, null);
    }

    public WeightedBlock(String id, Double weight, List<String> dimIds, List<String> excludedDimensions) {
        this(id, weight, dimIds, excludedDimensions, null, null);
    }

    public WeightedBlock(String id, Double weight, List<String> dimIds, List<String> excludedDimensions, Integer maxY, Integer minY) {
        this.id = id;
        this.weight = weight;
        this.dimensions = dimIds;
        this.excludedDimensions = excludedDimensions;
        this.maxY = maxY;
        this.minY = minY;
    }

    public Block getBlock() {
        return getCompat().getBlock(Identifier.tryParse(id));
    }
}