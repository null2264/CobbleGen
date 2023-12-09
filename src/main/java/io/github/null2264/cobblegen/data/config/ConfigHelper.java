package io.github.null2264.cobblegen.data.config;

import blue.endless.jankson.*;
import io.github.null2264.cobblegen.data.CGIdentifier;
import io.github.null2264.cobblegen.util.CGLog;
import lombok.val;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

import static io.github.null2264.cobblegen.util.Constants.JANKSON;

public class ConfigHelper {
    @ApiStatus.Internal
    public static <T extends Config> T loadConfig(boolean reload, File configFile, T workingConfig, T defaultConfig, Class<T> clazz) {
        String string = reload ? "reload" : "load";
        try {
            CGLog.info("Trying to " + string + " config file...");
            JsonObject json = JANKSON.load(configFile);
            return JANKSON.fromJson(json, clazz);
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

    /**
     * @deprecated Will be removed once Jankson released their proper omit null feature
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

    public static <T> List<T> listFromJson(JsonArray json, Function<JsonObject, T> mapper) {
        return json.stream().map((o) -> mapper.apply((JsonObject) o)).filter(Objects::nonNull).toList();
    }

    public static Map<CGIdentifier, List<WeightedBlock>> generatorFromJson(JsonObject json, String key) {
        Map<CGIdentifier, List<WeightedBlock>> result = new HashMap<>();
        JsonObject obj = json.getObject(key);

        if (obj == null) return null;

        obj.forEach((k, v) -> {
            if (!(v instanceof JsonArray)) return;
            val id = CGIdentifier.of(k);
            List<WeightedBlock> list = listFromJson((JsonArray) v, WeightedBlock::fromJson);
            result.put(id, list);
        });
        return result;
    }
}