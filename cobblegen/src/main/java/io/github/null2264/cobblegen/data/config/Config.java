package io.github.null2264.cobblegen.data.config;

import blue.endless.jankson.Jankson;
import blue.endless.jankson.JsonPrimitive;
import io.github.null2264.cobblegen.compat.LoaderCompat;
import io.github.null2264.cobblegen.data.CGIdentifier;
import io.github.null2264.cobblegen.data.SemVer;

import java.nio.file.Path;

public interface Config {

    Jankson JANKSON = Jankson.builder()
        .registerSerializer(CGIdentifier.class, (it, m) -> it.toJson())
        .registerDeserializer(JsonPrimitive.class, CGIdentifier.class, (json, m) -> CGIdentifier.fromJson(json))
        .registerDeserializer(String.class, CGIdentifier.class, (str, m) -> CGIdentifier.of(str))
        .build();

    Path path = LoaderCompat.getConfigDir();

    // TODO: Bump to v1.1 once it's ready
    SemVer DEFAULT_FORMAT_VERSION = new SemVer("1.0");

    interface Factory<T extends Config> {
        T load();
        T reload(T workingConfig);
    }
}
