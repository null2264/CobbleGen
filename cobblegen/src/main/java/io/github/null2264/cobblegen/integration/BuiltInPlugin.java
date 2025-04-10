package io.github.null2264.cobblegen.integration;

import io.github.null2264.cobblegen.CGPlugin;
import io.github.null2264.cobblegen.CobbleGenPlugin;
import io.github.null2264.cobblegen.compat.LoaderCompat;
import io.github.null2264.cobblegen.data.CGIdentifier;
import io.github.null2264.cobblegen.data.config.Config;
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
import static io.github.null2264.cobblegen.util.Util.elvis;

@CGPlugin
public class BuiltInPlugin implements CobbleGenPlugin
{
    private static final Config.Factory<ConfigData> factory = new ConfigData.Factory();
    private static final Path configPath = LoaderCompat.getConfigDir();
    private static final File configFile = new File(configPath + File.separator + MOD_ID + ".json5");
    @Nullable
    private static ConfigData config = null;

    private boolean isReload = false;

    @Nullable
    private Fluid getFluidFromString(String string) {
        try {
            return Util.getFluid(ResourceLocation.tryParse(string));
        } catch (Exception e) {
            return null;
        }
    }

    @Nullable
    private Block getBlockFromString(String string) {
        try {
            return Util.getBlock(ResourceLocation.tryParse(string));
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public void registerInteraction(CGRegistry registry) {
        CGLog.info((!isReload ? "L" : "Rel") + "oading config...");
        if (config == null || isReload) config = isReload ? factory.reload(configFile) : factory.load(configFile);
        if (config == null) throw new RuntimeException("How?");

        AtomicInteger count = new AtomicInteger();

        GeneratorMap stoneGen = new GeneratorMap();
        GeneratorMap cobbleGen = new GeneratorMap();
        GeneratorMap basaltGen = new GeneratorMap();

        elvis(config.stoneGen, new ResultList())
            .forEach(result ->
                stoneGen.put(CGIdentifier.of(result.getModifier().orElse("*")), elvis(config.stoneGen, new ResultList()))
            );
        elvis(config.cobbleGen, new ResultList())
            .forEach(result ->
                cobbleGen.put(CGIdentifier.of(result.getModifier().orElse("*")), elvis(config.cobbleGen, new ResultList()))
            );
        elvis(config.basaltGen, new ResultList())
            .forEach(result -> {
                CGIdentifier id = result.getModifier().map(CGIdentifier::of).orElseGet(() -> CGIdentifier.fromBlock(Blocks.SOUL_SOIL));
                basaltGen.put(id, elvis(config.basaltGen, new ResultList()));
            });

        if (config.advanced != null)
            config.advanced.forEach((fluid, value) -> {
                Fluid actualFluid = getFluidFromString(fluid);
                if (actualFluid == null) return;

                value.forEach((neighbour, gen) -> {
                    final GeneratorMap results = gen.results;
                    final GeneratorMap obi = gen.obsidian;

                    boolean isNeighbourBlock = neighbour.startsWith("b:");
                    if (isNeighbourBlock) neighbour = neighbour.substring(2);

                    if (gen.resultsFromTop != null && !gen.resultsFromTop.isEmpty()) {
                        Fluid neighbourFluid = getFluidFromString(neighbour);
                        if (neighbourFluid != null) {
                            registry.addGenerator(
                                    actualFluid,
                                    new StoneGenerator(
                                            gen.resultsFromTop,
                                            neighbourFluid,
                                            gen.silent
                                    )
                            );
                            count.getAndIncrement();
                        }
                    }

                    if (!results.isEmpty()) {
                        Generator generator = null;
                        if (isNeighbourBlock) {
                            Block neighbourBlock = getBlockFromString(neighbour);
                            if (neighbourBlock != null)
                                generator = new BasaltGenerator(results, neighbourBlock, gen.silent);
                        } else {
                            Fluid neighbourFluid = getFluidFromString(neighbour);
                            if (neighbourFluid != null)
                                generator = new CobbleGenerator(results, neighbourFluid, gen.silent, obi);
                        }

                        if (generator != null) {
                            registry.addGenerator(actualFluid, generator);
                            count.getAndIncrement();
                        }
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
