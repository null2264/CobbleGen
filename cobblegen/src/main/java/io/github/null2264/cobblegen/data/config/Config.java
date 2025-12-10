package io.github.null2264.cobblegen.data.config;

import blue.endless.jankson.Jankson;
import blue.endless.jankson.JsonPrimitive;
import io.github.null2264.cobblegen.compat.LoaderCompat;
import io.github.null2264.cobblegen.data.CGIdentifier;

import java.nio.file.Path;

public interface Config {

    Jankson JANKSON = Jankson.builder()
        .registerSerializer(CGIdentifier.class, (it, m) -> it.toJson())
        .registerDeserializer(JsonPrimitive.class, CGIdentifier.class, (json, m) -> CGIdentifier.fromJson(json))
        .registerDeserializer(String.class, CGIdentifier.class, (str, m) -> CGIdentifier.of(str))
        .build();

    Path path = LoaderCompat.getConfigDir();

    interface Factory<T extends Config> {
        T load();
        T reload(T workingConfig);
    }
}
