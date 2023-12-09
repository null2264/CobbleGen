package io.github.null2264.cobblegen.data.config;

import blue.endless.jankson.JsonArray;
import blue.endless.jankson.JsonObject;
import blue.endless.jankson.annotation.Deserializer;
import blue.endless.jankson.annotation.Serializer;
import io.github.null2264.cobblegen.data.CGIdentifier;
import io.github.null2264.cobblegen.data.JanksonSerializable;
import io.github.null2264.cobblegen.data.Pair;
import lombok.val;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import static io.github.null2264.cobblegen.data.config.ConfigHelper.listFromJson;
import static io.github.null2264.cobblegen.util.Constants.JANKSON;

public class GeneratorMap extends HashMap<CGIdentifier, List<WeightedBlock>> implements JanksonSerializable {
    @SafeVarargs
    public static GeneratorMap of(Pair<CGIdentifier, List<WeightedBlock>>... gens) {
        val map = new GeneratorMap();
        Arrays.stream(gens).forEach(pair -> map.put(pair.getFirst(), pair.getSecond()));
        return map;
    }

    @Override
    @Serializer
    public JsonObject toJson() {
        return (JsonObject) JANKSON.toJson(this);
    }

    @Deserializer
    public static GeneratorMap fromJson(JsonObject json) {
        if (json == null) return null;

        GeneratorMap result = new GeneratorMap();
        json.forEach((k, v) -> {
            if (!(v instanceof JsonArray)) return;
            val id = CGIdentifier.of(k);
            List<WeightedBlock> list = listFromJson((JsonArray) v, WeightedBlock::fromJson);
            result.put(id, list);
        });
        return result;
    }
}