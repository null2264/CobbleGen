package io.github.null2264.cobblegen.data.config;

import blue.endless.jankson.JsonObject;
import blue.endless.jankson.annotation.Deserializer;
import blue.endless.jankson.annotation.Serializer;
import io.github.null2264.cobblegen.data.CGIdentifier;
import io.github.null2264.cobblegen.data.JanksonSerializable;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;

import static io.github.null2264.cobblegen.util.Constants.JANKSON;

public class CustomGen implements JanksonSerializable
{
    @Nullable
    public GeneratorMap cobbleGen;
    @Nullable
    public GeneratorMap stoneGen;
    @Nullable
    public GeneratorMap basaltGen;

    public CustomGen(
            @Nullable
            GeneratorMap cobbleGen,
            @Nullable
            GeneratorMap stoneGen,
            @Nullable
            GeneratorMap basaltGen
    ) {
        this.cobbleGen = cobbleGen;
        this.stoneGen = stoneGen;
        this.basaltGen = basaltGen;
    }

    @Override
    @Serializer
    public JsonObject toJson() {
        JsonObject json = new JsonObject();
        if (cobbleGen != null) json.put("cobbleGen", cobbleGen.toJson());
        if (stoneGen != null) json.put("stoneGen", JANKSON.toJson(stoneGen));
        if (basaltGen != null) json.put("basaltGen", JANKSON.toJson(basaltGen));
        return json;
    }

    @Deserializer
    public static CustomGen fromJson(JsonObject json) {
        if (json == null) return null;

        GeneratorMap cobbleGen = GeneratorMap.fromJson(json.getObject("cobbleGen"));
        GeneratorMap stoneGen = GeneratorMap.fromJson(json.getObject("stoneGen"));
        GeneratorMap basaltGen = GeneratorMap.fromJson(json.getObject("basaltGen"));
        return new CustomGen(cobbleGen, stoneGen, basaltGen);
    }
}