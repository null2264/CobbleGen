package io.github.null2264.cobblegen.data.config;

import blue.endless.jankson.*;
import blue.endless.jankson.annotation.Deserializer;
import blue.endless.jankson.annotation.Serializer;
import io.github.null2264.cobblegen.data.CGIdentifier;
import io.github.null2264.cobblegen.data.JanksonSerializable;
import lombok.val;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static io.github.null2264.cobblegen.data.config.ConfigHelper.listFromJson;
import static io.github.null2264.cobblegen.util.Constants.JANKSON;

public class ConfigData implements Config, JanksonSerializable
{
    @Comment(value = "CobbleGen Format Version, you can leave this alone for now. v2.0 will be released in CobbleGen v6.0")
    public String formatVersion = "1.0";

    @Nullable
    @Comment(value = """
            Default Generators
            {
              "id": "mod_id:block_id",
              "weight": 95.5,
              "dimensions": [
                "mod_id:dimension_id",
                "mod_id:dimension_id"
              ],
              "excludedDimensions": [
                "mod_id:dimension_id",
                "mod_id:dimension_id"
              ],
              "minY": 0,
              "maxY": 69
            }""")
    public List<WeightedBlock> cobbleGen;

    @Nullable
    public List<WeightedBlock> stoneGen;

    @Nullable
    public List<WeightedBlock> basaltGen;

    @Nullable
    @Comment(value = """
            Custom Generators
            <stoneGen|cobbleGen|basaltGen>: {
              "mod_id:modifier_block_id": [
                {
                  "id": "mod_id:block_id",
                  "weight": 95.5,
                  "dimensions": [
                    "mod_id:dimension_id",
                    "mod_id:dimension_id"
                  ],
                  "excludedDimensions": [
                    "mod_id:dimension_id",
                    "mod_id:dimension_id"
                  ],
                  "minY": 0,
                  "maxY": 69
                },
                ...
              ]
            }""")
    public CustomGen customGen;

    @Nullable
    public Map<String, Map<String, AdvancedGen>> advanced;

    public static ConfigData defaultConfig() {
        ConfigData config = new ConfigData();
        config.cobbleGen = List.of(new WeightedBlock(
                "minecraft:cobblestone",
                100.0,
                null,
                null,
                null,
                0,
                null
        ), new WeightedBlock("minecraft:cobbled_deepslate", 100.0, null, null, 0, null, null));
        config.stoneGen = List.of(new WeightedBlock("minecraft:stone", 100.0));
        config.basaltGen = List.of(new WeightedBlock("minecraft:basalt", 100.0));
        config.customGen = new CustomGen(
                // Cobble Gen
                Map.of(
                        CGIdentifier.of("minecraft:bedrock"),
                        List.of(
                                new WeightedBlock("minecraft:emerald_ore", 2.0),
                                new WeightedBlock("minecraft:diamond_ore", 5.0),
                                new WeightedBlock("minecraft:lapis_ore", 8.0),
                                new WeightedBlock("minecraft:gold_ore", 10.0),
                                new WeightedBlock("minecraft:iron_ore", 15.0),
                                new WeightedBlock("minecraft:coal_ore", 20.0),
                                new WeightedBlock("minecraft:cobblestone", 80.0)
                        )
                ),
                // Stone Gen
                Map.of(
                        CGIdentifier.of("minecraft:bedrock"),
                        List.of(
                                new WeightedBlock("minecraft:stone", 40.0),
                                new WeightedBlock("minecraft:diorite", 20.0),
                                new WeightedBlock("minecraft:andesite", 20.0),
                                new WeightedBlock("minecraft:granite", 20.0)
                        )
                ),
                // Basalt Gen
                Map.of(
                        CGIdentifier.of("minecraft:bedrock"),
                        List.of(
                                new WeightedBlock("minecraft:end_stone", 100.0, List.of("minecraft:the_end")),
                                new WeightedBlock("minecraft:blackstone", 100.0, null, List.of("minecraft:overworld"))
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
        json.put("cobbleGen", JANKSON.toJson(cobbleGen));
        json.put("stoneGen", JANKSON.toJson(stoneGen));
        json.put("basaltGen", JANKSON.toJson(basaltGen));
        json.put("customGen", JANKSON.toJson(customGen));
        if (advanced != null) {
            JsonObject advancedRoot = new JsonObject();
            advanced.forEach((fluid1, fluidMap) -> {
                JsonObject advancedSubroot = new JsonObject();
                fluidMap.forEach((fluid2, advancedGen) -> advancedSubroot.put(fluid2, advancedGen.toJson()));
                advancedRoot.put(fluid1, advancedSubroot);
            });
            json.put("advanced", advancedRoot);
        }
        return json;
    }

    @Deserializer
    public static ConfigData fromJson(JsonObject json) {
        ConfigData config = new ConfigData();
        val formatVersion = json.get("formatVersion");
        config.formatVersion = (formatVersion instanceof JsonPrimitive) ? ((JsonPrimitive) formatVersion).asString() : "1.0";
        config.cobbleGen = listFromJson((JsonArray) json.get("cobbleGen"), WeightedBlock::fromJson);
        config.stoneGen = listFromJson((JsonArray) json.get("stoneGen"), WeightedBlock::fromJson);
        config.stoneGen = listFromJson((JsonArray) json.get("stoneGen"), WeightedBlock::fromJson);
        config.customGen = CustomGen.fromJson(json.getObject("customGen"));
        JsonObject e = json.getObject("advanced");
        if (e == null) return config;

        config.advanced = new HashMap<>();
        e.forEach((fluid1, fluidMap) -> {
            if (!(fluidMap instanceof JsonObject advancedSubroot)) return;

            Map<String, AdvancedGen> advanced = new HashMap<>();
            advancedSubroot.forEach((fluid2, jsonElement) -> advanced.put(fluid2, AdvancedGen.fromJson((JsonObject) jsonElement)));
            config.advanced.put(fluid1, advanced);
        });
        return config;
    }
}