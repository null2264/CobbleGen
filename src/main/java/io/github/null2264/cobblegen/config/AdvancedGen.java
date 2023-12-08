package io.github.null2264.cobblegen.config;

import blue.endless.jankson.*;
import blue.endless.jankson.annotation.Deserializer;
import blue.endless.jankson.annotation.Serializer;
import io.github.null2264.cobblegen.data.CGIdentifier;
import io.github.null2264.cobblegen.data.JanksonSerializable;
import lombok.val;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static io.github.null2264.cobblegen.config.ConfigHelper.*;
import static io.github.null2264.cobblegen.util.Constants.JANKSON;

public class AdvancedGen implements JanksonSerializable
{
    public Boolean silent;
    public Map<CGIdentifier, List<WeightedBlock>> results;
    public Map<CGIdentifier, List<WeightedBlock>> resultsFromTop;
    public Map<CGIdentifier, List<WeightedBlock>> obsidian;

    public AdvancedGen() {
        this(false, new HashMap<>(), new HashMap<>(), new HashMap<>());
    }

    public AdvancedGen(
            Boolean silent,
            Map<CGIdentifier, List<WeightedBlock>> results,
            Map<CGIdentifier, List<WeightedBlock>> resultsFromTop,
            Map<CGIdentifier, List<WeightedBlock>> obsidian
    ) {
        this.silent = silent;
        this.results = results;
        this.resultsFromTop = resultsFromTop;
        this.obsidian = obsidian;
    }

    @Override
    @Serializer
    public JsonElement toJson() {
        val json = new JsonObject();
        json.put("silent", JsonPrimitive.of(silent));
        json.put("results", JANKSON.toJson(results));
        json.put("resultsFromTop", JANKSON.toJson(resultsFromTop));
        json.put("obsidian", JANKSON.toJson(obsidian));
        return json;
    }

    @Deserializer
    public static AdvancedGen fromJson(JsonObject json) {
        val silent = json.getBoolean("silent", false);
        val results = generatorFromJson(json, "results");
        val resultsFromTop = generatorFromJson(json, "resultsFromTop");
        val obsidian = generatorFromJson(json, "obsidian");
        return new AdvancedGen(silent, results, resultsFromTop, obsidian);
    }
}