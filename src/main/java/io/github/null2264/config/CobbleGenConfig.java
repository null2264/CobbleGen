package io.github.null2264.config;

import io.github.null2264.util.WeightedBlock;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.serializer.JanksonConfigSerializer;

import java.util.List;

import static io.github.null2264.CobbleGen.MOD_ID;

@Config(name = MOD_ID)
public class CobbleGenConfig implements ConfigData
{
    public List<WeightedBlock> cobbleGen = List.of(
        new WeightedBlock("minecraft:dirt", 5.5),
        new WeightedBlock("minecraft:stone", 10.0),
        new WeightedBlock("minecraft:cobblestone", 50.0)
    );
    public List<WeightedBlock> stoneGen = List.of(
        new WeightedBlock("minecraft:dirt", 100.0)
    );
    public List<WeightedBlock> basaltGen = List.of(
        new WeightedBlock("minecraft:dirt", 100.0)
    );

    public static void init() {
        AutoConfig.register(CobbleGenConfig.class, JanksonConfigSerializer::new);
    }

    public static CobbleGenConfig get() {
        return AutoConfig.getConfigHolder(CobbleGenConfig.class).getConfig();
    }
}