package io.github.null2264.cobblegen.config;

import blue.endless.jankson.Comment;
import io.github.null2264.cobblegen.data.CGIdentifier;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;

public class ConfigData implements Config
{
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
}