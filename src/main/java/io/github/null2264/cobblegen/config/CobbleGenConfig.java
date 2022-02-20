package io.github.null2264.cobblegen.config;

import draylar.omegaconfig.OmegaConfig;
import draylar.omegaconfig.api.Comment;
import draylar.omegaconfig.api.Config;
import io.github.null2264.cobblegen.util.WeightedBlock;

import java.util.List;

import static io.github.null2264.cobblegen.CobbleGen.MOD_ID;

public class CobbleGenConfig implements Config
{
    @Comment(value = "Cobblestone Generator\n" +
        "{ \"id\": \"mod_id:block_id\", \"weight\": 95.5 }")
    public List<WeightedBlock> cobbleGen = List.of(
        new WeightedBlock("minecraft:diamond_ore", 5.0),
        new WeightedBlock("minecraft:iron_ore", 15.0),
        new WeightedBlock("minecraft:coal_ore", 20.0),
        new WeightedBlock("minecraft:cobblestone", 60.0)
    );

    @Comment(value = "Stone Generator\n" +
        "{ \"id\": \"mod_id:block_id\", \"weight\": 95.5 }")
    public List<WeightedBlock> stoneGen = List.of(
        new WeightedBlock("minecraft:stone", 100.0)
    );

    @Comment(value = "Basalt Generator\n" +
        "{ \"id\": \"mod_id:block_id\", \"weight\": 95.5 }")
    public List<WeightedBlock> basaltGen = List.of(
        new WeightedBlock("minecraft:basalt", 100.0)
    );

    @Override
    public String getName() {
        return MOD_ID;
    }

    public static CobbleGenConfig init() {
        return OmegaConfig.register(CobbleGenConfig.class);
    }
}