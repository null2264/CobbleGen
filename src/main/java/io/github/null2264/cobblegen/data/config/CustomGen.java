package io.github.null2264.cobblegen.data.config;

import blue.endless.jankson.JsonObject;
import blue.endless.jankson.annotation.Deserializer;
import blue.endless.jankson.annotation.Serializer;
import io.github.null2264.cobblegen.data.CGIdentifier;
import io.github.null2264.cobblegen.data.JanksonSerializable;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;

import static io.github.null2264.cobblegen.data.config.ConfigHelper.generatorFromJson;
import static io.github.null2264.cobblegen.util.Constants.JANKSON;

public class CustomGen implements JanksonSerializable
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

    @Override
    @Serializer
    public JsonObject toJson() {
        JsonObject json = new JsonObject();
        json.put("cobbleGen", JANKSON.toJson(cobbleGen));
        json.put("stoneGen", JANKSON.toJson(stoneGen));
        json.put("basaltGen", JANKSON.toJson(basaltGen));
        return json;
    }

    @Deserializer
    public static CustomGen fromJson(JsonObject json) {
        Map<CGIdentifier, List<WeightedBlock>> cobbleGen = generatorFromJson(json, "cobbleGen");
        Map<CGIdentifier, List<WeightedBlock>> stoneGen = generatorFromJson(json, "stoneGen");
        Map<CGIdentifier, List<WeightedBlock>> basaltGen = generatorFromJson(json, "basaltGen");
        return new CustomGen(cobbleGen, stoneGen, basaltGen);
    }
}