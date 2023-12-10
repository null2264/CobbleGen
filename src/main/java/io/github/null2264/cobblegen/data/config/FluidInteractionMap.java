package io.github.null2264.cobblegen.data.config;

import blue.endless.jankson.JsonObject;
import blue.endless.jankson.annotation.Deserializer;
import blue.endless.jankson.annotation.Serializer;
import io.github.null2264.cobblegen.data.JanksonSerializable;

import java.util.HashMap;

public class FluidInteractionMap extends HashMap<String, HashMap<String, AdvancedGen>> implements JanksonSerializable {
    @Override
    @Serializer
    public JsonObject toJson() {
        JsonObject root = new JsonObject();
        this.forEach((fluid1, fluidMap) -> {
            JsonObject subroot = new JsonObject();
            fluidMap.forEach((fluid2, advancedGen) -> subroot.put(fluid2, advancedGen.toJson()));
            root.put(fluid1, subroot);
        });
        return root;
    }

    @Deserializer
    public static FluidInteractionMap fromJson(JsonObject json) {
        if (json == null) return null;

        FluidInteractionMap result = new FluidInteractionMap();
        json.forEach((fluid1, fluidMap) -> {
            if (!(fluidMap instanceof JsonObject subroot)) return;

            HashMap<String, AdvancedGen> root = new HashMap<>();
            subroot.forEach((fluid2, jsonElement) -> root.put(fluid2, AdvancedGen.fromJson((JsonObject) jsonElement)));
            result.put(fluid1, root);
        });
        return result;
    }
}