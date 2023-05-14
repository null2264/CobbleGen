package io.github.null2264.cobblegen.integration;

import blue.endless.jankson.*;
import com.google.gson.Gson;
import io.github.null2264.cobblegen.config.ConfigData;
import io.github.null2264.cobblegen.config.WeightedBlock;
import io.github.null2264.cobblegen.data.generator.BasaltGenerator;
import io.github.null2264.cobblegen.data.generator.CobbleGenerator;
import io.github.null2264.cobblegen.data.generator.StoneGenerator;
import io.github.null2264.cobblegen.data.model.Generator;
import io.github.null2264.cobblegen.util.CGLog;
import io.github.null2264.cobblegen.util.CobbleGenPlugin;
import lombok.val;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import static io.github.null2264.cobblegen.CobbleGen.*;
import static io.github.null2264.cobblegen.util.Util.notNullOr;

public class BuiltInPlugin implements CobbleGenPlugin
{
    private static final Path configPath = FabricLoader.getInstance().getConfigDir();
    private static final File configFile = new File(configPath + File.separator + MOD_ID + ".json5");
    private static final Jankson jankson = Jankson.builder().build();
    private static final Gson gson = new Gson();

    private boolean firstInit = true;

    private Fluid getFluidFromString(String string) {
        return getCompat().getFluid(new Identifier(string));
    }

    private Block getBlockFromString(String string) {
        return getCompat().getBlock(new Identifier(string));
    }

    @Override
    public void registerInteraction() {
        CGLog.info((firstInit ? "L" : "Rel") + "oading config...");

        AtomicInteger count = new AtomicInteger();
        val config = loadConfig(!firstInit);

        Map<String, List<WeightedBlock>> stoneGen = new HashMap<>();
        if (config.customGen != null && config.customGen.stoneGen != null)
            stoneGen = config.customGen.stoneGen;
        Map<String, List<WeightedBlock>> cobbleGen = new HashMap<>();
        if (config.customGen != null && config.customGen.cobbleGen != null)
            cobbleGen = config.customGen.cobbleGen;
        Map<String, List<WeightedBlock>> basaltGen = new HashMap<>();
        if (config.customGen != null && config.customGen.basaltGen != null)
            basaltGen = config.customGen.basaltGen;

        stoneGen.put("*", notNullOr(config.stoneGen, new ArrayList<>()));
        cobbleGen.put("*", notNullOr(config.cobbleGen, new ArrayList<>()));
        basaltGen.put("minecraft:soul_soil", notNullOr(config.basaltGen, new ArrayList<>()));

        if (config.advanced != null)
            config.advanced.forEach((fluid, value) -> {
                Fluid actualFluid = getFluidFromString(fluid);
                value.forEach((neighbour, gen) -> {
                    val results = gen.results;

                    boolean isNeighbourBlock = neighbour.startsWith("b:");
                    if (isNeighbourBlock) neighbour = neighbour.substring(2);

                    if (gen.resultsFromTop != null && !gen.resultsFromTop.isEmpty()) {
                        FLUID_INTERACTION.addGenerator(
                                actualFluid,
                                new StoneGenerator(
                                        gen.resultsFromTop,
                                        getFluidFromString(neighbour),
                                        gen.silent
                                )
                        );
                        count.getAndIncrement();
                    }

                    if (!results.isEmpty()) {
                        Generator generator;
                        if (isNeighbourBlock)
                            generator = new BasaltGenerator(results, getBlockFromString(neighbour), gen.silent);
                        else
                            generator = new CobbleGenerator(results, getFluidFromString(neighbour), gen.silent);

                        FLUID_INTERACTION.addGenerator(actualFluid, generator);
                        count.getAndIncrement();
                    }
                });
            });

        FLUID_INTERACTION.addGenerator(Fluids.LAVA, new StoneGenerator(stoneGen, Fluids.WATER, false));
        FLUID_INTERACTION.addGenerator(Fluids.LAVA, new CobbleGenerator(cobbleGen, Fluids.WATER, false));
        FLUID_INTERACTION.addGenerator(Fluids.LAVA, new BasaltGenerator(basaltGen, Blocks.BLUE_ICE, false));

        CGLog.info(String.valueOf(count.get()), "generators has been added from config");
        if (firstInit) firstInit = false;
    }

    /**
     * @deprecated Removed when Jankson released their proper null filter
     */
    @Deprecated
    @Nullable
    private static JsonElement filter(JsonElement json) {
        JsonElement result = null;
        if (json instanceof JsonObject finalResult) {
            finalResult.keySet().forEach(key -> {
                JsonElement element = finalResult.get(key);
                if (!(element instanceof JsonNull) && element != null) filter(element);
                else finalResult.remove(key);
            });
            result = finalResult;
        } else if (json instanceof JsonArray finalResult) {
            finalResult.forEach(element -> {
                if (element instanceof JsonObject) filter(element);
            });
            result = finalResult;
        }
        return result;
    }

    private static ConfigData loadConfig(boolean reload) {
        String string = reload ? "reload" : "load";
        try {
            CGLog.info("Trying to " + string + " config file...");
            JsonObject json = jankson.load(configFile);
            return gson.fromJson(json.toJson(JsonGrammar.COMPACT), ConfigData.class);
        } catch (Exception e) {
            CGLog.error("There was an error while " + string + "ing the config file!\n" + e);
            val config = new ConfigData();
            if (!configFile.exists()) {
                saveConfig(config);
            }
            CGLog.warn("Falling back to default config...");
            return config;
        }
    }

    private static void saveConfig(ConfigData config) {
        try {
            CGLog.info("Trying to create config file...");
            FileWriter fw = new FileWriter(configFile);
            JsonElement jsonElement = Jankson.builder().build().toJson(config);
            JsonElement filteredElement = filter(jsonElement);
            fw.write((filteredElement != null ? filteredElement : jsonElement).toJson(JsonGrammar.JSON5));
            fw.close();
        } catch (IOException e) {
            CGLog.error("There was an error while creating the config file!\n" + e);
        }
    }
}