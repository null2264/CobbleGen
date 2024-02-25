package io.github.null2264.cobblegen.integration;

import io.github.null2264.cobblegen.CGPlugin;
import io.github.null2264.cobblegen.CobbleGenPlugin;
import io.github.null2264.cobblegen.compat.LoaderCompat;
import io.github.null2264.cobblegen.data.CGIdentifier;
import io.github.null2264.cobblegen.data.config.ConfigData;
import io.github.null2264.cobblegen.data.config.GeneratorMap;
import io.github.null2264.cobblegen.data.config.ResultList;
import io.github.null2264.cobblegen.data.generator.BasaltGenerator;
import io.github.null2264.cobblegen.data.generator.CobbleGenerator;
import io.github.null2264.cobblegen.data.generator.StoneGenerator;
import io.github.null2264.cobblegen.data.model.CGRegistry;
import io.github.null2264.cobblegen.data.model.Generator;
import io.github.null2264.cobblegen.util.CGLog;
import io.github.null2264.cobblegen.util.Util;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.nio.file.Path;
import java.util.concurrent.atomic.AtomicInteger;

import static io.github.null2264.cobblegen.CobbleGen.MOD_ID;
import static io.github.null2264.cobblegen.data.config.ConfigHelper.loadConfig;
import static io.github.null2264.cobblegen.util.Util.notNullOr;

@CGPlugin
public class BuiltInPlugin implements CobbleGenPlugin
{
    private static final Path configPath = LoaderCompat.getConfigDir();
    private static final File configFile = new File(configPath + File.separator + MOD_ID + ".json5");
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
        if (config == null || isReload) config = loadConfig(isReload, configFile, config, ConfigData.defaultConfig(), ConfigData.class);
        if (config == null) throw new RuntimeException("How?");

        AtomicInteger count = new AtomicInteger();

        GeneratorMap stoneGen = new GeneratorMap();
        if (config.customGen != null && config.customGen.stoneGen != null)
            stoneGen = config.customGen.stoneGen;
        GeneratorMap cobbleGen = new GeneratorMap();
        if (config.customGen != null && config.customGen.cobbleGen != null)
            cobbleGen = config.customGen.cobbleGen;
        GeneratorMap basaltGen = new GeneratorMap();
        if (config.customGen != null && config.customGen.basaltGen != null)
            basaltGen = config.customGen.basaltGen;

        stoneGen.put(CGIdentifier.wildcard(), notNullOr(config.stoneGen, new ResultList()));
        cobbleGen.put(CGIdentifier.wildcard(), notNullOr(config.cobbleGen, new ResultList()));
        basaltGen.put(CGIdentifier.fromBlock(Blocks.SOUL_SOIL), notNullOr(config.basaltGen, new ResultList()));

        if (config.advanced != null)
            config.advanced.forEach((fluid, value) -> {
                Fluid actualFluid = getFluidFromString(fluid);
                value.forEach((neighbour, gen) -> {
                    final GeneratorMap results = gen.results;
                    final GeneratorMap obi = gen.obsidian;

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
                            generator = new CobbleGenerator(results, getFluidFromString(neighbour), gen.silent, obi);

                        registry.addGenerator(actualFluid, generator);
                        count.getAndIncrement();
                    }
                });
            });

        registry.addGenerator(Fluids.LAVA, new StoneGenerator(stoneGen, Fluids.WATER, false));
        registry.addGenerator(Fluids.LAVA, new CobbleGenerator(cobbleGen, Fluids.WATER, false, GeneratorMap.of()));
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
}