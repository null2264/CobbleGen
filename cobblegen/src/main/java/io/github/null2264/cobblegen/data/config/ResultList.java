package io.github.null2264.cobblegen.data.config;

import blue.endless.jankson.JsonArray;
import blue.endless.jankson.JsonElement;
import blue.endless.jankson.JsonObject;
import blue.endless.jankson.annotation.Deserializer;
import blue.endless.jankson.annotation.Serializer;
import io.github.null2264.cobblegen.data.JanksonSerializable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ResultList extends ArrayList<WeightedBlock> implements JanksonSerializable {
    public ResultList() {}

    public ResultList(List<WeightedBlock> weightedBlocks) {
        this.addAll(weightedBlocks);
    }

    public static ResultList of(WeightedBlock... weightedBlocks) {
        return new ResultList(Arrays.asList(weightedBlocks));
    }

    @Override
    @Serializer
    public JsonElement toJson() {
        JsonArray array = new JsonArray();
        this.forEach((block) -> array.add(block.toJson()));
        return array;
    }

    @Deserializer
    public static ResultList fromJson(JsonElement json) {
        if (json == null) return null;

        ResultList result = new ResultList();
        ((JsonArray) json).stream()
                .filter((e) -> e instanceof JsonObject)
                .forEach((e) -> {
                    WeightedBlock block = WeightedBlock.fromJson((JsonObject) e);
                    if (block != null) result.add(block);
                });
        return result;
    }
}