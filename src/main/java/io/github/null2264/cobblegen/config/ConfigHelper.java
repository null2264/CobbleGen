package io.github.null2264.cobblegen.config;

import blue.endless.jankson.*;
import com.google.gson.Gson;
import io.github.null2264.cobblegen.util.CGLog;
import lombok.val;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class ConfigHelper {
    private static final Jankson jankson = Jankson.builder().build();
    private static final Gson gson = new Gson();

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
    public static ConfigData loadConfig(boolean reload, File configFile, ConfigData workingConfig) {
        String string = reload ? "reload" : "load";
        try {
            CGLog.info("Trying to " + string + " config file...");
            JsonObject json = jankson.load(configFile);
            return gson.fromJson(json.toJson(JsonGrammar.COMPACT), ConfigData.class);
        } catch (Exception e) {
            CGLog.error("There was an error while " + string + "ing the config file!\n" + e);

            if (reload && workingConfig != null) {
                CGLog.warn("Falling back to previously working config...");
                return workingConfig;
            }

            val newConfig = ConfigData.defaultConfig();
            if (!configFile.exists()) {
                saveConfig(newConfig, configFile);
            }
            CGLog.warn("Falling back to default config...");
            return newConfig;
        }
    }

    private static void saveConfig(ConfigData config, File configFile) {
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