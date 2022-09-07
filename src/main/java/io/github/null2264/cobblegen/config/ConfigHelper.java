package io.github.null2264.cobblegen.config;

import blue.endless.jankson.Jankson;
import blue.endless.jankson.JsonGrammar;
import blue.endless.jankson.JsonObject;
import blue.endless.jankson.api.SyntaxError;
import com.google.gson.Gson;
import net.fabricmc.loader.api.FabricLoader;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;

import static io.github.null2264.cobblegen.CobbleGen.LOGGER;
import static io.github.null2264.cobblegen.CobbleGen.MOD_ID;

public class ConfigHelper {
    private static final Path configPath = FabricLoader.getInstance().getConfigDir();
    private static final File configFile = new File(configPath + File.separator + MOD_ID + ".json5");
    private static final Jankson jankson = Jankson.builder().build();
    private static final Gson gson = new Gson();
    public static ConfigData CONFIG;

    public static void loadAndSaveDefault() {
        try {
            if (!ConfigHelper.load(true)) {
                ConfigHelper.save();
            }
        } catch (Exception e) {
            LOGGER.error("Error: ", e);
            CONFIG = new ConfigData();
        }
    }

    public static boolean load(Boolean fallback) throws SyntaxError, IOException {
        try {
            JsonObject json = jankson.load(configFile);
            CONFIG = gson.fromJson(json.toJson(JsonGrammar.COMPACT), ConfigData.class);
            return true;
        } catch (Exception e) {
            if (fallback) {
                LOGGER.error("Error: ", e);
                CONFIG = new ConfigData();
                return false;
            }
            throw e;
        }
    }

    public static void save() {
        try {
            FileWriter fw = new FileWriter(configFile);
            fw.write(Jankson.builder().build().toJson(CONFIG).toJson(JsonGrammar.JSON5));
            fw.close();
        } catch (Exception e) {
            LOGGER.error("Error: ", e);
        }
    }
}