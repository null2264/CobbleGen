package io.github.null2264.cobblegen.data.config;

import blue.endless.jankson.*;
import blue.endless.jankson.annotation.Deserializer;
import blue.endless.jankson.annotation.Serializer;
import io.github.null2264.cobblegen.data.CGIdentifier;
import io.github.null2264.cobblegen.data.CGSemVer;
import io.github.null2264.cobblegen.data.JanksonSerializable;
import io.github.null2264.cobblegen.data.Pair;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static io.github.null2264.cobblegen.CobbleGen.META_CONFIG;
import static io.github.null2264.cobblegen.compat.CollectionCompat.listOf;

@SuppressWarnings("TextBlockMigration")
public class ConfigData implements Config, JanksonSerializable
{
    /**
     * Deprecated, moved to {@link io.github.null2264.cobblegen.data.config.ConfigMetaData}
     */
    @Comment(value = "CobbleGen Format Version, you can leave this alone for now. v2.0 will be released in CobbleGen v6.0")
    @NotNull
    @Deprecated
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
        ConfigData config = new ConfigData();
        config.cobbleGen = ResultList.of(new WeightedBlock(
                "minecraft:cobblestone",
                100.0,
                null,
                null,
                null,
                0,
                null
        ), new WeightedBlock("minecraft:cobbled_deepslate", 100.0, null, null, 0, null, null));
        config.stoneGen = ResultList.of(new WeightedBlock("minecraft:stone", 100.0));
        config.basaltGen = ResultList.of(new WeightedBlock("minecraft:basalt", 100.0));
        config.customGen = new CustomGen(
                // Cobble Gen
                GeneratorMap.of(
                        Pair.of(
                                CGIdentifier.of("minecraft:bedrock"),
                                ResultList.of(
                                        new WeightedBlock("minecraft:emerald_ore", 2.0),
                                        new WeightedBlock("minecraft:diamond_ore", 5.0),
                                        new WeightedBlock("minecraft:lapis_ore", 8.0),
                                        new WeightedBlock("minecraft:gold_ore", 10.0),
                                        new WeightedBlock("minecraft:iron_ore", 15.0),
                                        new WeightedBlock("minecraft:coal_ore", 20.0),
                                        new WeightedBlock("minecraft:cobblestone", 80.0)
                                )
                        )
                ),
                // Stone Gen
                GeneratorMap.of(
                        Pair.of(
                                CGIdentifier.of("minecraft:bedrock"),
                                ResultList.of(
                                        new WeightedBlock("minecraft:stone", 40.0),
                                        new WeightedBlock("minecraft:diorite", 20.0),
                                        new WeightedBlock("minecraft:andesite", 20.0),
                                        new WeightedBlock("minecraft:granite", 20.0)
                                )
                        )
                ),
                // Basalt Gen
                GeneratorMap.of(
                        Pair.of(
                                CGIdentifier.of("minecraft:bedrock"),
                                ResultList.of(
                                        new WeightedBlock("minecraft:end_stone", 100.0, listOf("minecraft:the_end")),
                                        new WeightedBlock("minecraft:blackstone", 100.0, null, listOf("minecraft:overworld"))
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
        config.formatVersion = META_CONFIG.formatVersion.toString();
        config.cobbleGen = ResultList.fromJson(json.get("cobbleGen"));
        config.stoneGen = ResultList.fromJson(json.get("stoneGen"));
        config.stoneGen = ResultList.fromJson(json.get("stoneGen"));
        config.customGen = CustomGen.fromJson(json.getObject("customGen"));
        config.advanced = FluidInteractionMap.fromJson(json.getObject("advanced"));
        return config;
    }
}