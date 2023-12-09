package io.github.null2264.cobblegen.data.config;

import blue.endless.jankson.*;
import blue.endless.jankson.annotation.Deserializer;
import blue.endless.jankson.annotation.Serializer;
import io.github.null2264.cobblegen.data.CGIdentifier;
import io.github.null2264.cobblegen.data.JanksonSerializable;
import lombok.val;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static io.github.null2264.cobblegen.util.Constants.JANKSON;

public class AdvancedGen implements JanksonSerializable
{
    public Boolean silent;
    public GeneratorMap results;
    public GeneratorMap resultsFromTop;
    public GeneratorMap obsidian;

    public AdvancedGen() {
        this(false, new GeneratorMap(), new GeneratorMap(), new GeneratorMap());
    }

    public AdvancedGen(
            Boolean silent,
            GeneratorMap results,
            GeneratorMap resultsFromTop,
            GeneratorMap obsidian
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
        json.put("results", results.toJson());
        json.put("resultsFromTop", resultsFromTop.toJson());
        json.put("obsidian", obsidian.toJson());
        return json;
    }

    @Deserializer
    public static AdvancedGen fromJson(JsonObject json) {
        val silent = json.getBoolean("silent", false);
        val results = GeneratorMap.fromJson(json.getObject("results"));
        val resultsFromTop = GeneratorMap.fromJson(json.getObject("resultsFromTop"));
        val obsidian = GeneratorMap.fromJson(json.getObject("obsidian"));
        return new AdvancedGen(silent, results, resultsFromTop, obsidian);
    }
}