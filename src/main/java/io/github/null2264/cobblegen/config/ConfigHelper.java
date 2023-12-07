package io.github.null2264.cobblegen.config;

import blue.endless.jankson.*;
import io.github.null2264.cobblegen.data.CGIdentifier;
import io.github.null2264.cobblegen.util.CGLog;
import lombok.val;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class ConfigHelper {
    private static final Jankson jankson = Jankson.builder()
            .registerSerializer(CGIdentifier.class, (it, m) -> new JsonPrimitive(it.toString()))
            .registerDeserializer(String.class, CGIdentifier.class, (str, m) -> CGIdentifier.of(str))
            .registerDeserializer(JsonPrimitive.class, CGIdentifier.class, (primitive, m) -> {
                val str = primitive.asString();
                if (str.equals("null")) return null;
                return CGIdentifier.of(str);
            })
            .build();

    /**
     * @deprecated Removed when Jankson released their proper null filter
     */
    @Deprecated
    @Nullable
    private static JsonElement filter(JsonElement json) {
        JsonElement result = null;
        if (json instanceof JsonObject finalResult) {
            finalResult.keySet().forEach(key -> {
                JsonElement element = finalResult.get(key);
                if (!(element instanceof JsonNull) && element != null) filter(element);
                else finalResult.remove(key);
            });
            result = finalResult;
        } else if (json instanceof JsonArray finalResult) {
            finalResult.forEach(element -> {
                if (element instanceof JsonObject) filter(element);
            });
            result = finalResult;
        }
        return result;
    }

    @ApiStatus.Internal
    public static <T extends Config> T loadConfig(boolean reload, File configFile, T workingConfig, T defaultConfig, Class<T> clazz) {
        String string = reload ? "reload" : "load";
        try {
            CGLog.info("Trying to " + string + " config file...");
            JsonObject json = jankson.load(configFile);
            return jankson.fromJson(json, clazz);
        } catch (Exception e) {
            CGLog.error("There was an error while " + string + "ing the config file!\n" + e);

            if (reload && workingConfig != null) {
                CGLog.warn("Falling back to previously working config...");
                return workingConfig;
            }

            if (!configFile.exists()) {
                saveConfig(defaultConfig, configFile);
            }
            CGLog.warn("Falling back to default config...");
            return defaultConfig;
        }
    }

    private static void saveConfig(Config config, File configFile) {
        try {
            CGLog.info("Trying to create config file...");
            FileWriter fw = new FileWriter(configFile);
            JsonElement jsonElement = Jankson.builder().build().toJson(config);
            JsonElement filteredElement = filter(jsonElement);
            fw.write((filteredElement != null ? filteredElement : jsonElement).toJson(JsonGrammar.JSON5));
            fw.close();
        } catch (IOException e) {
            CGLog.error("There was an error while creating the config file!\n" + e);
        }
    }
}