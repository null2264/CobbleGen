package io.github.null2264.cobblegen.config;

import java.util.List;
import java.util.Map;

import static io.github.null2264.cobblegen.compat.CollectionCompat.mapOf;

public class AdvancedGen
{
    public boolean silent = false;
    public Map<String, List<WeightedBlock>> results = mapOf();
    public Map<String, List<WeightedBlock>> resultsFromTop = mapOf();
    public Map<String, List<WeightedBlock>> obsidian = mapOf();
}