package io.github.null2264.cobblegen.config;

import java.util.List;
import java.util.Map;

public class CustomGen
{
    public Map<String, List<WeightedBlock>> cobbleGen;
    public Map<String, List<WeightedBlock>> stoneGen;

    public CustomGen(Map<String, List<WeightedBlock>> cobbleGen, Map<String, List<WeightedBlock>> stoneGen) {
        this.cobbleGen = cobbleGen;
        this.stoneGen = stoneGen;
    }
}