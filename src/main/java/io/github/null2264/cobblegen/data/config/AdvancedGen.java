package io.github.null2264.cobblegen.data.config;

import blue.endless.jankson.JsonElement;
import blue.endless.jankson.JsonObject;
import blue.endless.jankson.JsonPrimitive;
import blue.endless.jankson.annotation.Deserializer;
import blue.endless.jankson.annotation.Serializer;
import io.github.null2264.cobblegen.data.JanksonSerializable;

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
        JsonObject json = new JsonObject();
        json.put("silent", JsonPrimitive.of(silent));
        json.put("results", results.toJson());
        json.put("resultsFromTop", resultsFromTop.toJson());
        json.put("obsidian", obsidian.toJson());
        return json;
    }

    @Deserializer
    public static AdvancedGen fromJson(JsonObject json) {
        Boolean silent = json.getBoolean("silent", false);
        GeneratorMap results = GeneratorMap.fromJson(json.getObject("results"));
        GeneratorMap resultsFromTop = GeneratorMap.fromJson(json.getObject("resultsFromTop"));
        GeneratorMap obsidian = GeneratorMap.fromJson(json.getObject("obsidian"));
        return new AdvancedGen(silent, results, resultsFromTop, obsidian);
    }
}