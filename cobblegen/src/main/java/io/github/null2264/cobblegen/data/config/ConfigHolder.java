package io.github.null2264.cobblegen.data.config;

import blue.endless.jankson.Jankson;
import blue.endless.jankson.JsonPrimitive;
import io.github.null2264.cobblegen.data.CGIdentifier;
import org.jetbrains.annotations.ApiStatus;

import java.io.File;

import static io.github.null2264.cobblegen.data.config.ConfigHelper.loadConfig;

public class ConfigHolder {

    public static final Jankson JANKSON = Jankson.builder()
        .registerSerializer(CGIdentifier.class, (it, m) -> it.toJson())
        .registerDeserializer(JsonPrimitive.class, CGIdentifier.class, (json, m) -> CGIdentifier.fromJson(json))
        .registerDeserializer(String.class, CGIdentifier.class, (str, m) -> CGIdentifier.of(str))
        .build();

    @ApiStatus.Internal
    public static final File configFile = new File(Config.path + File.separator + "cobblegen.json5");
    @ApiStatus.Internal
    public static final File metaConfigFile = new File(Config.path + File.separator + "cobblegen-meta.json5");
    @ApiStatus.Internal
    public static ConfigMetaData META = loadConfig(false, metaConfigFile, null, new ConfigMetaData(), ConfigMetaData.class);
}
