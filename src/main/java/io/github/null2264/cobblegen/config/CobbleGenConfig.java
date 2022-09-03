package io.github.null2264.cobblegen.config;

import draylar.omegaconfig.OmegaConfig;
import draylar.omegaconfig.api.Comment;
import draylar.omegaconfig.api.Config;

import java.util.List;
import java.util.Map;

import static io.github.null2264.cobblegen.CobbleGen.MOD_ID;

// TODO: Modmenu Integration (move back to cloth config + auto config?)
public class CobbleGenConfig implements Config {

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
    public List<WeightedBlock> cobbleGen = List.of(
            new WeightedBlock("minecraft:cobblestone", 100.0, null, null, null, 0),
            new WeightedBlock("minecraft:cobbled_deepslate", 100.0, null, null, 0, null)
    );

    public List<WeightedBlock> stoneGen = List.of(
            new WeightedBlock("minecraft:stone", 100.0)
    );

    public List<WeightedBlock> basaltGen = List.of(
            new WeightedBlock("minecraft:basalt", 100.0)
    );

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
    public CustomGen customGen = new CustomGen(
            // Cobble Gen
            Map.of(
                    "minecraft:bedrock",
                    List.of(
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
            Map.of(
                    "minecraft:bedrock",
                    List.of(
                            new WeightedBlock("minecraft:stone", 40.0),
                            new WeightedBlock("minecraft:diorite", 20.0),
                            new WeightedBlock("minecraft:andesite", 20.0),
                            new WeightedBlock("minecraft:granite", 20.0)
                    )
            ),
            // Basalt Gen
            Map.of(
                    "minecraft:bedrock",
                    List.of(
                            new WeightedBlock("minecraft:end_stone", 100.0, List.of("minecraft:the_end")),
                            new WeightedBlock("minecraft:blackstone", 100.0, null, List.of("minecraft:the_end"))
                    )
            )
    );

    public static CobbleGenConfig init() {
        return OmegaConfig.register(CobbleGenConfig.class);
    }

    @Override
    public String getName() {
        return MOD_ID;
    }

    @Override
    public String getExtension() {
        return "json5";
    }
}