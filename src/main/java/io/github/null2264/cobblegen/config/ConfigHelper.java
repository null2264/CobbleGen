package io.github.null2264.cobblegen.config;

import blue.endless.jankson.*;
import blue.endless.jankson.api.SyntaxError;
import com.google.gson.Gson;
import net.fabricmc.loader.api.FabricLoader;

import javax.annotation.Nullable;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;

import static io.github.null2264.cobblegen.CobbleGen.LOGGER;
import static io.github.null2264.cobblegen.CobbleGen.MOD_ID;

public class ConfigHelper
{
    private static final Path configPath = FabricLoader.getInstance().getConfigDir();
    private static final File configFile = new File(configPath + File.separator + MOD_ID + ".json5");
    private static final Jankson jankson = Jankson.builder().build();
    private static final Gson gson = new Gson();
    public static ConfigData CONFIG;

    public static void loadAndSaveDefault() {
        ConfigHelper.loadOrDefault();
        if (!configFile.exists()) {
            ConfigHelper.save();
        }
    }

    public static void loadOrDefault() {
        try {
            ConfigHelper.load();
            LOGGER.info("Config file successfully loaded");
        } catch (Exception e) {
            LOGGER.error("There was an error while (re)loading the config file!", e);
            CONFIG = new ConfigData();
            LOGGER.warn("Falling back to default config...");
        }
    }

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

    public static void load() throws SyntaxError, IOException {
        try {
            LOGGER.info("Trying to (re)load config file...");
            JsonObject json = jankson.load(configFile);
            CONFIG = gson.fromJson(json.toJson(JsonGrammar.COMPACT), ConfigData.class);
        } catch (Exception e) {
            LOGGER.error("There was an error while (re)loading the config file!", e);
            throw e;
        }
    }

    public static void save() {
        try {
            LOGGER.info("Trying to create config file...");
            FileWriter fw = new FileWriter(configFile);
            JsonElement jsonElement = Jankson.builder().build().toJson(CONFIG);
            JsonElement filteredElement = filter(jsonElement);
            fw.write((filteredElement != null ? filteredElement : jsonElement).toJson(JsonGrammar.JSON5));
            fw.close();
        } catch (IOException e) {
            LOGGER.error("There was an error while creating the config file!", e);
        }
    }
}