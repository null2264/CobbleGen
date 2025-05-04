package io.github.null2264.cobblegen.data.config;

import blue.endless.jankson.*;
import blue.endless.jankson.annotation.Deserializer;
import blue.endless.jankson.annotation.Serializer;
import io.github.null2264.cobblegen.data.CGIdentifier;
import io.github.null2264.cobblegen.data.JanksonSerializable;
import io.github.null2264.cobblegen.data.Pair;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;

import static io.github.null2264.cobblegen.compat.CollectionCompat.listOf;
import static io.github.null2264.cobblegen.gametest.Constants.IS_GAMETEST_ENABLED;

@SuppressWarnings("TextBlockMigration")
public class ConfigData implements Config, JanksonSerializable {

    public static class Factory implements Config.Factory<ConfigData> {

        private static final String NAME = "generator";
        private static final File PATH = new File(Config.path + File.separator + "cobblegen.json5");

        @Override
        public ConfigData load() {
            return ConfigHelper.loadConfig(
                NAME,
                false,
                PATH,
                null,
                ConfigData::defaultConfig,
                ConfigData.class
            );
        }

        @Override
        public ConfigData reload(ConfigData workingConfig) {
            return ConfigHelper.loadConfig(
                NAME,
                true,
                PATH,
                workingConfig,
                ConfigData::defaultConfig,
                ConfigData.class
            );
        }
    }

    @Comment(value = "CobbleGen Format Version, you can leave this alone for now. v2.0 will be released in CobbleGen v6.0")
    @NotNull
    public String formatVersion = "1.0";

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
                     "  \"maxY\": 69\n" +
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

    public static ConfigData defaultConfig() {
        return IS_GAMETEST_ENABLED ? ConfigData.testConfig() : ConfigData.productionConfig();
    }

    public static ConfigData testConfig() {
        ConfigData config = new ConfigData();
        config.cobbleGen = ResultList.of(new WeightedBlock.Builder().setId("minecraft:bedrock").setWeight(100.0).build());
        config.stoneGen = ResultList.of(new WeightedBlock.Builder().setId("minecraft:bedrock").setWeight(100.0).build());
        config.basaltGen = ResultList.of(new WeightedBlock.Builder().setId("minecraft:bedrock").setWeight(100.0).build());
        config.customGen = new CustomGen(
            // Cobble Gen
            GeneratorMap.of(
                Pair.of(
                    CGIdentifier.of("minecraft:bedrock"),
                    ResultList.of(
                        new WeightedBlock.Builder().setId("minecraft:bedrock").setWeight(100.0).build()
                    )
                )
            ),
            // Stone Gen
            GeneratorMap.of(
                Pair.of(
                    CGIdentifier.of("minecraft:bedrock"),
                    ResultList.of(
                        new WeightedBlock.Builder().setId("minecraft:bedrock").setWeight(100.0).build()
                    )
                )
            ),
            // Basalt Gen
            GeneratorMap.of(
                Pair.of(
                    CGIdentifier.of("minecraft:bedrock"),
                    ResultList.of(
                        new WeightedBlock.Builder().setId("minecraft:bedrock").setWeight(100.0).build()
                    )
                )
            )
        );

        return config;
    }

    public static ConfigData productionConfig() {
        ConfigData config = new ConfigData();
        config.cobbleGen = ResultList.of(
            new WeightedBlock.Builder().setId("minecraft:cobblestone").setWeight(100.0).setMinY(0).build(),
            new WeightedBlock.Builder().setId("minecraft:cobbled_deepslate").setWeight(100.0).setMaxY(0).build()
        );
        config.stoneGen = ResultList.of(new WeightedBlock.Builder().setId("minecraft:stone").setWeight(100.0).build());
        config.basaltGen = ResultList.of(new WeightedBlock.Builder().setId("minecraft:basalt").setWeight(100.0).build());
        config.customGen = new CustomGen(
                // Cobble Gen
                GeneratorMap.of(
                        Pair.of(
                                CGIdentifier.of("minecraft:bedrock"),
                                ResultList.of(
                                        new WeightedBlock.Builder().setId("minecraft:emerald_ore").setWeight(2.0).build(),
                                        new WeightedBlock.Builder().setId("minecraft:diamond_ore").setWeight(5.0).build(),
                                        new WeightedBlock.Builder().setId("minecraft:lapis_ore").setWeight(8.0).build(),
                                        new WeightedBlock.Builder().setId("minecraft:gold_ore").setWeight(10.0).build(),
                                        new WeightedBlock.Builder().setId("minecraft:iron_ore").setWeight(15.0).build(),
                                        new WeightedBlock.Builder().setId("minecraft:coal_ore").setWeight(20.0).build(),
                                        new WeightedBlock.Builder().setId("minecraft:cobblestone").setWeight(80.0).build()
                                )
                        )
                ),
                // Stone Gen
                GeneratorMap.of(
                        Pair.of(
                                CGIdentifier.of("minecraft:bedrock"),
                                ResultList.of(
                                        new WeightedBlock.Builder().setId("minecraft:stone").setWeight(40.0).build(),
                                        new WeightedBlock.Builder().setId("minecraft:diorite").setWeight(20.0).build(),
                                        new WeightedBlock.Builder().setId("minecraft:andesite").setWeight(20.0).build(),
                                        new WeightedBlock.Builder().setId("minecraft:granite").setWeight(20.0).build()
                                )
                        )
                ),
                // Basalt Gen
                GeneratorMap.of(
                        Pair.of(
                                CGIdentifier.of("minecraft:bedrock"),
                                ResultList.of(
                                        new WeightedBlock.Builder().setId("minecraft:end_stone").setWeight(100.0).setDimensions(listOf("minecraft:the_end")).build(),
                                        new WeightedBlock.Builder().setId("minecraft:blackstone").setWeight(100.0).setExcludedDimensions(listOf("minecraft:overworld")).build()
                                )
                        )
                )
        );
        return config;
    }

    @Override
    @Serializer
    public JsonObject toJson() {
        JsonObject json = new JsonObject();
        json.put("formatVersion", JsonPrimitive.of(formatVersion));
        if (cobbleGen != null) json.put("cobbleGen", cobbleGen.toJson());
        if (stoneGen != null) json.put("stoneGen", stoneGen.toJson());
        if (basaltGen != null) json.put("basaltGen", basaltGen.toJson());
        if (customGen != null) json.put("customGen", customGen.toJson());
        if (advanced != null) json.put("advanced", advanced.toJson());
        return json;
    }

    @Deserializer
    public static ConfigData fromJson(JsonObject json) {
        ConfigData config = new ConfigData();
        JsonElement formatVersion = json.get("formatVersion");
        config.formatVersion = (formatVersion instanceof JsonPrimitive) ? ((JsonPrimitive) formatVersion).asString() : "1.0";
        /* TODO
        if (config.formatVersion.equals("1.0")) {
            // TODO: Migrate to 2.0
        }
         */
        config.cobbleGen = ResultList.fromJson(json.get("cobbleGen"));
        config.stoneGen = ResultList.fromJson(json.get("stoneGen"));
        config.basaltGen = ResultList.fromJson(json.get("basaltGen"));
        config.customGen = CustomGen.fromJson(json.getObject("customGen"));
        config.advanced = FluidInteractionMap.fromJson(json.getObject("advanced"));
        return config;
    }
}
