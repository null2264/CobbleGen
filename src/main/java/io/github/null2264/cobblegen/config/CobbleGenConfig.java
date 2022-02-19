package io.github.null2264.cobblegen.config;

import io.github.null2264.cobblegen.util.WeightedBlock;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.serializer.JanksonConfigSerializer;

import java.util.List;

import static io.github.null2264.cobblegen.CobbleGen.MOD_ID;

@Config(name = MOD_ID)
public class CobbleGenConfig implements ConfigData
{
    public List<WeightedBlock> cobbleGen = List.of(
        new WeightedBlock("minecraft:diamond_ore", 5.0),
        new WeightedBlock("minecraft:iron_ore", 15.0),
        new WeightedBlock("minecraft:coal_ore", 20.0),
        new WeightedBlock("minecraft:cobblestone", 60.0)
    );
    public List<WeightedBlock> stoneGen = List.of(
        new WeightedBlock("minecraft:stone", 100.0)
    );
    public List<WeightedBlock> basaltGen = List.of(
        new WeightedBlock("minecraft:basalt", 100.0)
    );

    public static void init() {
        AutoConfig.register(CobbleGenConfig.class, JanksonConfigSerializer::new);
    }

    public static CobbleGenConfig get() {
        return AutoConfig.getConfigHolder(CobbleGenConfig.class).getConfig();
    }
}