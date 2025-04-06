package io.github.null2264.cobblegen.data;

import blue.endless.jankson.JsonElement;
import blue.endless.jankson.annotation.Serializer;

public interface JanksonSerializable {
    JsonElement toJson();
}