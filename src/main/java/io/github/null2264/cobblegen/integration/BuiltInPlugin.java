package io.github.null2264.cobblegen.integration;

import blue.endless.jankson.*;
import com.google.gson.Gson;
import io.github.null2264.cobblegen.CobbleGenPlugin;
import io.github.null2264.cobblegen.config.ConfigData;
import io.github.null2264.cobblegen.config.WeightedBlock;
import io.github.null2264.cobblegen.data.generator.BasaltGenerator;
import io.github.null2264.cobblegen.data.generator.CobbleGenerator;
import io.github.null2264.cobblegen.data.generator.StoneGenerator;
import io.github.null2264.cobblegen.data.model.CGRegistry;
import io.github.null2264.cobblegen.data.model.Generator;
import io.github.null2264.cobblegen.util.CGLog;
import io.github.null2264.cobblegen.util.Constants.CGBlocks;
import io.github.null2264.cobblegen.util.Util;
import lombok.val;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
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

import static io.github.null2264.cobblegen.CobbleGen.MOD_ID;
import static io.github.null2264.cobblegen.util.Util.notNullOr;

public class BuiltInPlugin implements CobbleGenPlugin
{
    private static final Path configPath = FabricLoader.getInstance().getConfigDir();
    private static final File configFile = new File(configPath + File.separator + MOD_ID + ".json5");
    private static final Jankson jankson = Jankson.builder().build();
    private static final Gson gson = new Gson();
    @Nullable
    private static ConfigData config = null;

    private boolean isReload = false;

    private Fluid getFluidFromString(String string) {
        return Util.getFluid(new ResourceLocation(string));
    }

    private Block getBlockFromString(String string) {
        return Util.getBlock(new ResourceLocation(string));
    }

    @Override
    public void registerInteraction(CGRegistry registry) {
        CGLog.info((!isReload ? "L" : "Rel") + "oading config...");
        if (config == null) config = loadConfig(isReload);
        if (config == null) throw new RuntimeException("How?");

        AtomicInteger count = new AtomicInteger();

        Map<String, List<WeightedBlock>> stoneGen = new HashMap<>();
        if (config.customGen != null && config.customGen.stoneGen != null)
            stoneGen = new HashMap<>(config.customGen.stoneGen);
        Map<String, List<WeightedBlock>> cobbleGen = new HashMap<>();
        if (config.customGen != null && config.customGen.cobbleGen != null)
            cobbleGen = new HashMap<>(config.customGen.cobbleGen);
        Map<String, List<WeightedBlock>> basaltGen = new HashMap<>();
        if (config.customGen != null && config.customGen.basaltGen != null)
            basaltGen = new HashMap<>(config.customGen.basaltGen);

        stoneGen.put(CGBlocks.WILDCARD.toString(), notNullOr(config.stoneGen, new ArrayList<>()));
        cobbleGen.put(CGBlocks.WILDCARD.toString(), notNullOr(config.cobbleGen, new ArrayList<>()));
        basaltGen.put(CGBlocks.fromBlock(Blocks.SOUL_SOIL), notNullOr(config.basaltGen, new ArrayList<>()));

        if (config.advanced != null)
            config.advanced.forEach((fluid, value) -> {
                Fluid actualFluid = getFluidFromString(fluid);
                value.forEach((neighbour, gen) -> {
                    val results = gen.results;

                    boolean isNeighbourBlock = neighbour.startsWith("b:");
                    if (isNeighbourBlock) neighbour = neighbour.substring(2);

                    if (gen.resultsFromTop != null && !gen.resultsFromTop.isEmpty()) {
                        registry.addGenerator(
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

                        registry.addGenerator(actualFluid, generator);
                        count.getAndIncrement();
                    }
                });
            });

        registry.addGenerator(Fluids.LAVA, new StoneGenerator(stoneGen, Fluids.WATER, false));
        registry.addGenerator(Fluids.LAVA, new CobbleGenerator(cobbleGen, Fluids.WATER, false));
        registry.addGenerator(Fluids.LAVA, new BasaltGenerator(basaltGen, Blocks.BLUE_ICE, false));
        count.addAndGet(3);

        CGLog.info(String.valueOf(count.get()), "generators has been added from config");
        if (isReload) isReload = false;
    }

    @Override
    public void onReload() {
        CGLog.info("Reloading built-in plugin...");
        isReload = true;
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

            if (reload) {
                CGLog.warn("Falling back to previously working config...");
                return config;
            }

            val newConfig = new ConfigData();
            if (!configFile.exists()) {
                saveConfig(newConfig);
            }
            CGLog.warn("Falling back to default config...");
            return newConfig;
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