package io.github.null2264.cobblegen.data.config;

import blue.endless.jankson.*;
import blue.endless.jankson.annotation.Deserializer;
import blue.endless.jankson.annotation.Serializer;
import io.github.null2264.cobblegen.data.CGIdentifier;
import io.github.null2264.cobblegen.data.JanksonSerializable;
import io.github.null2264.cobblegen.data.Pair;
import io.github.null2264.cobblegen.gametest.CobbleGenTestConfig;
import io.github.null2264.cobblegen.util.CGLog;
import io.github.null2264.cobblegen.util.Util;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;

import static io.github.null2264.cobblegen.compat.CollectionCompat.listOf;

@SuppressWarnings("TextBlockMigration")
public class ConfigData implements Config, JanksonSerializable {

    private static String NAME = "generator";
    public static String LATEST_FORMAT_VERSION = "1.1";

    public static class Factory implements Config.Factory<ConfigData> {
        @Override
        public ConfigData load(File file) {
            return ConfigHelper.loadConfig(
                NAME,
                false,
                file,
                null,
                CobbleGenTestConfig.ENABLED ? ConfigData.testConfig() : ConfigData.defaultConfig(),
                ConfigData.class
            );
        }

        @Override
        public ConfigData reload(File file) {
            return ConfigHelper.loadConfig(
                NAME,
                true,
                file,
                null,
                CobbleGenTestConfig.ENABLED ? ConfigData.testConfig() : ConfigData.defaultConfig(),
                ConfigData.class
            );
        }
    }

    // FIXME: Make this SemVer object
    @Comment(value = "CobbleGen Format Version, you can leave this alone for now. v2.0 will be released in CobbleGen v6.0")
    @NotNull
    public String formatVersion = LATEST_FORMAT_VERSION;

    @Nullable
    @Comment(value = "Default Generators\n" +
                     "{\n" +
                     "  \"id\": \"mod_id:block_id\",\n" +
                     "  \"weight\": 95.5,\n" +
                     "  \"dimensions\": [\n" +
                     "    \"mod_id:dimension_id\",\n" +
                     "    \"mod_id:dimension_id\"\n" +
                     "  ],\n" +
                     "  \"excludedDimensions\": [\n" +
                     "    \"mod_id:dimension_id\",\n" +
                     "    \"mod_id:dimension_id\"\n" +
                     "  ],\n" +
                     "  \"minY\": 0,\n" +
                     "  \"maxY\": 69,\n" +
                     "  \"modifier\": \"mod_id:modifier_block_id\"\n" +
                     "}")
    public ResultList cobbleGen;

    @Nullable
    public ResultList stoneGen;

    @Nullable
    public ResultList basaltGen;

    @Nullable
    @Comment(value = "Custom Generators\n" +
                     "<stoneGen|cobbleGen|basaltGen>: {\n" +
                     "  \"mod_id:modifier_block_id\": [\n" +
                     "    {\n" +
                     "      \"id\": \"mod_id:block_id\",\n" +
                     "      \"weight\": 95.5,\n" +
                     "      \"dimensions\": [\n" +
                     "        \"mod_id:dimension_id\",\n" +
                     "        \"mod_id:dimension_id\"\n" +
                     "      ],\n" +
                     "      \"excludedDimensions\": [\n" +
                     "        \"mod_id:dimension_id\",\n" +
                     "        \"mod_id:dimension_id\"\n" +
                     "      ],\n" +
                     "      \"minY\": 0,\n" +
                     "      \"maxY\": 69\n" +
                     "    },\n" +
                     "    ...\n" +
                     "  ]\n" +
                     "}")
    public CustomGen customGen;

    @Nullable
    public FluidInteractionMap advanced;

    public static ConfigData testConfig() {
        ConfigData config = new ConfigData();
        config.cobbleGen = ResultList.of(
            new WeightedBlock.Builder().setId("minecraft:cobbled_deepslate").setWeight(100.0).build(),
            new WeightedBlock.Builder().setId("minecraft:deepslate").setWeight(100.0).setModifier("minecraft:bedrock").build()
        );
        config.stoneGen = ResultList.of(
            new WeightedBlock.Builder().setId("minecraft:deepslate").setWeight(100.0).build(),
            new WeightedBlock.Builder().setId("minecraft:cobbled_deepslate").setWeight(100.0).setModifier("minecraft:bedrock").build()
        );
        config.basaltGen = ResultList.of(
            new WeightedBlock.Builder().setId("minecraft:blackstone").setWeight(100.0).build(),
            new WeightedBlock.Builder().setId("minecraft:end_stone").setWeight(100.0).setModifier("minecraft:bedrock").build()
        );
        return config;
    }

    public static ConfigData defaultConfig() {
        ConfigData config = new ConfigData();
        config.cobbleGen = ResultList.of(
            new WeightedBlock.Builder().setId("minecraft:cobblestone").setWeight(100.0).setMinY(0).build(),
            new WeightedBlock.Builder().setId("minecraft:cobbled_deepslate").setWeight(100.0).setMaxY(0).build(),
            new WeightedBlock.Builder().setId("minecraft:emerald_ore").setWeight(2.0).setModifier("minecraft:bedrock").build(),
            new WeightedBlock.Builder().setId("minecraft:diamond_ore").setWeight(5.0).setModifier("minecraft:bedrock").build(),
            new WeightedBlock.Builder().setId("minecraft:lapis_ore").setWeight(8.0).setModifier("minecraft:bedrock").build(),
            new WeightedBlock.Builder().setId("minecraft:gold_ore").setWeight(10.0).setModifier("minecraft:bedrock").build(),
            new WeightedBlock.Builder().setId("minecraft:iron_ore").setWeight(15.0).setModifier("minecraft:bedrock").build(),
            new WeightedBlock.Builder().setId("minecraft:coal_ore").setWeight(20.0).setModifier("minecraft:bedrock").build(),
            new WeightedBlock.Builder().setId("minecraft:cobblestone").setWeight(80.0).setModifier("minecraft:bedrock").build()
        );
        config.stoneGen = ResultList.of(
            new WeightedBlock.Builder().setId("minecraft:stone").setWeight(100.0).build(),
            new WeightedBlock.Builder().setId("minecraft:stone").setWeight(40.0).setModifier("minecraft:bedrock").build(),
            new WeightedBlock.Builder().setId("minecraft:diorite").setWeight(20.0).setModifier("minecraft:bedrock").build(),
            new WeightedBlock.Builder().setId("minecraft:andesite").setWeight(20.0).setModifier("minecraft:bedrock").build(),
            new WeightedBlock.Builder().setId("minecraft:granite").setWeight(20.0).setModifier("minecraft:bedrock").build()
        );
        config.basaltGen = ResultList.of(
            new WeightedBlock.Builder().setId("minecraft:basalt").setWeight(100.0).build(),
            new WeightedBlock.Builder().setId("minecraft:end_stone").setWeight(100.0).setDimensions(listOf("minecraft:the_end")).setModifier("minecraft:bedrock").build(),
            new WeightedBlock.Builder().setId("minecraft:blackstone").setWeight(100.0).setExcludedDimensions(listOf("minecraft:overworld")).setModifier("minecraft:bedrock").build()
        );
        return config;
    }

    @Override
    @Serializer
    public JsonObject toJson() {
        JsonObject json = new JsonObject();
        json.put("formatVersion", JsonPrimitive.of(LATEST_FORMAT_VERSION));
        if (cobbleGen != null) json.put("cobbleGen", cobbleGen.toJson());
        if (stoneGen != null) json.put("stoneGen", stoneGen.toJson());
        if (basaltGen != null) json.put("basaltGen", basaltGen.toJson());
        if (advanced != null) json.put("advanced", advanced.toJson());
        return json;
    }

    @Deserializer
    public static ConfigData fromJson(JsonObject json) {
        ConfigData config = new ConfigData();
        JsonElement formatVersion = json.get("formatVersion");
        config.formatVersion = (formatVersion instanceof JsonPrimitive) ? ((JsonPrimitive) formatVersion).asString() : LATEST_FORMAT_VERSION;
        config.cobbleGen = ResultList.fromJson(json.get("cobbleGen"), config.formatVersion);
        config.stoneGen = ResultList.fromJson(json.get("stoneGen"), config.formatVersion);
        config.basaltGen = ResultList.fromJson(json.get("basaltGen"), config.formatVersion);
        // TODO: Delete later
        CustomGen customGen = CustomGen.fromJson(json.getObject("customGen"));
        if (config.formatVersion.equals("1.0")) {
            CGLog.warn(() -> "CobbleGen config format v1.0 is deprecated, please consider migrating to v" + LATEST_FORMAT_VERSION);
            if (customGen != null) {
                Util.optional(customGen.cobbleGen).ifPresent(gen -> gen.forEach((modifier, value) -> {
                    if (config.cobbleGen != null) {
                        config.cobbleGen.addAll(value.stream().peek(result -> result.neighbours = listOf(modifier.toString())).toList());
                    }
                }));
                Util.optional(customGen.stoneGen).ifPresent(gen -> gen.forEach((modifier, value) -> {
                    if (config.stoneGen != null) {
                        config.stoneGen.addAll(value.stream().peek(result -> result.neighbours = listOf(modifier.toString())).toList());
                    }
                }));
                Util.optional(customGen.basaltGen).ifPresent(gen -> gen.forEach((modifier, value) -> {
                    if (config.basaltGen != null) {
                        config.basaltGen.addAll(value.stream().peek(result -> result.neighbours = listOf(modifier.toString())).toList());
                    }
                }));
            }
        } else if (customGen != null) {
            // TODO: This probably should be sent to the chat when player joined a world.
            CGLog.warn(() -> "You're using \"customGen\" on config format v" + config.formatVersion + "! Please migrate to format v" + LATEST_FORMAT_VERSION + " or specify \"formatVersion\" on the config file, otherwise the custom generator(s) won't be used by the mod.");
        }
        config.advanced = FluidInteractionMap.fromJson(json.getObject("advanced"));
        return config;
    }
}
