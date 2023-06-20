package io.github.null2264.cobblegen.config;

import java.util.List;
import java.util.Map;

public class AdvancedGen
{
    public boolean silent = false;
    public Map<String, List<WeightedBlock>> results = Map.of();
    public Map<String, List<WeightedBlock>> resultsFromTop = Map.of();
    public Map<String, List<WeightedBlock>> obsidian = Map.of();
}