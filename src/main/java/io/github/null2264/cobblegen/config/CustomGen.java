package io.github.null2264.cobblegen.config;

import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;

public class CustomGen
{
    @Nullable
    public Map<String, List<WeightedBlock>> cobbleGen;
    @Nullable
    public Map<String, List<WeightedBlock>> stoneGen;
    @Nullable
    public Map<String, List<WeightedBlock>> basaltGen;

    public CustomGen(
            @Nullable
            Map<String, List<WeightedBlock>> cobbleGen,
            @Nullable
            Map<String, List<WeightedBlock>> stoneGen,
            @Nullable
            Map<String, List<WeightedBlock>> basaltGen
    ) {
        this.cobbleGen = cobbleGen;
        this.stoneGen = stoneGen;
        this.basaltGen = basaltGen;
    }
}