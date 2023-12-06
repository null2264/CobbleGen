package io.github.null2264.cobblegen.config;

import io.github.null2264.cobblegen.data.CGIdentifier;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;

public class CustomGen
{
    @Nullable
    public Map<CGIdentifier, List<WeightedBlock>> cobbleGen;
    @Nullable
    public Map<CGIdentifier, List<WeightedBlock>> stoneGen;
    @Nullable
    public Map<CGIdentifier, List<WeightedBlock>> basaltGen;

    public CustomGen(
            @Nullable
            Map<CGIdentifier, List<WeightedBlock>> cobbleGen,
            @Nullable
            Map<CGIdentifier, List<WeightedBlock>> stoneGen,
            @Nullable
            Map<CGIdentifier, List<WeightedBlock>> basaltGen
    ) {
        this.cobbleGen = cobbleGen;
        this.stoneGen = stoneGen;
        this.basaltGen = basaltGen;
    }
}