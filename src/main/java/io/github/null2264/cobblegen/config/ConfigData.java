package io.github.null2264.cobblegen.config;

import blue.endless.jankson.Comment;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;

import static io.github.null2264.cobblegen.compat.CollectionCompat.listOf;
import static io.github.null2264.cobblegen.compat.CollectionCompat.mapOf;

public class ConfigData implements Config
{
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
    public List<WeightedBlock> cobbleGen;

    @Nullable
    public List<WeightedBlock> stoneGen;

    @Nullable
    public List<WeightedBlock> basaltGen;

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
    public Map<String, Map<String, AdvancedGen>> advanced;

    public static ConfigData defaultConfig() {
        ConfigData config = new ConfigData();
        config.cobbleGen = listOf(
                //#if MC>=1.17
                new WeightedBlock("minecraft:cobblestone", 100.0, null, null, null, 0, null),
                new WeightedBlock("minecraft:cobbled_deepslate", 100.0, null, null, 0, null, null)
                //#else
                //$$ new WeightedBlock("minecraft:cobblestone", 100.0)
                //#endif
        );
        config.stoneGen = listOf(new WeightedBlock("minecraft:stone", 100.0));
        config.basaltGen = listOf(new WeightedBlock("minecraft:basalt", 100.0));
        config.customGen = new CustomGen(
                // Cobble Gen
                mapOf(
                        "minecraft:bedrock",
                        listOf(
                                new WeightedBlock("minecraft:emerald_ore", 2.0),
                                new WeightedBlock("minecraft:diamond_ore", 5.0),
                                new WeightedBlock("minecraft:lapis_ore", 8.0),
                                new WeightedBlock("minecraft:gold_ore", 10.0),
                                new WeightedBlock("minecraft:iron_ore", 15.0),
                                new WeightedBlock("minecraft:coal_ore", 20.0),
                                new WeightedBlock("minecraft:cobblestone", 40.0)
                        )
                ),
                // Stone Gen
                mapOf(
                        "minecraft:bedrock",
                        listOf(
                                new WeightedBlock("minecraft:stone", 40.0),
                                new WeightedBlock("minecraft:diorite", 20.0),
                                new WeightedBlock("minecraft:andesite", 20.0),
                                new WeightedBlock("minecraft:granite", 20.0)
                        )
                ),
                // Basalt Gen
                mapOf(
                        "minecraft:bedrock",
                        listOf(
                                new WeightedBlock("minecraft:end_stone", 100.0, listOf("minecraft:the_end")),
                                new WeightedBlock("minecraft:blackstone", 100.0, null, listOf("minecraft:overworld"))
                        )
                )
        );
        return config;
    }
}