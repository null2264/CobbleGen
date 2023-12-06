package io.github.null2264.cobblegen.config;

import io.github.null2264.cobblegen.data.CGIdentifier;

import java.util.List;
import java.util.Map;

public class AdvancedGen
{
    public boolean silent = false;
    public Map<CGIdentifier, List<WeightedBlock>> results = Map.of();
    public Map<CGIdentifier, List<WeightedBlock>> resultsFromTop = Map.of();
    public Map<CGIdentifier, List<WeightedBlock>> obsidian = Map.of();
}