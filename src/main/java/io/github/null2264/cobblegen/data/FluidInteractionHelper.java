package io.github.null2264.cobblegen.data;

import blue.endless.jankson.*;
import com.google.common.collect.ImmutableList;
import com.google.gson.Gson;
import io.github.null2264.cobblegen.config.ConfigData;
import io.github.null2264.cobblegen.config.WeightedBlock;
import io.github.null2264.cobblegen.data.model.Generator;
import io.github.null2264.cobblegen.util.CGLog;
import io.github.null2264.cobblegen.util.GeneratorType;
import lombok.val;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.fluid.FlowableFluid;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldEvents;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
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

/**
 * Replacement for BlockGenerator. This will act like Vanilla's registry system
 */
public class FluidInteractionHelper
{
    public static final ImmutableList<Direction> FLOW_DIRECTIONS = ImmutableList.of(
            Direction.DOWN, Direction.SOUTH, Direction.NORTH, Direction.EAST, Direction.WEST
    );
    private static final Path configPath = FabricLoader.getInstance().getConfigDir();
    private static final File configFile = new File(configPath + File.separator + MOD_ID + ".json5");
    private static final Jankson jankson = Jankson.builder().build();
    private static final Gson gson = new Gson();

    private final Map<Fluid, List<Generator>> generatorMap = new HashMap<>();
    private @Nullable Map<Fluid, List<Generator>> serverGeneratorMap = null;
    private final Map<Fluid, List<Generator>> externalMap = new HashMap<>();  // temporary map to hold 3rd party mods' generators

    private boolean firstInit = true;
    private boolean shouldReload;

    public FluidInteractionHelper() {
        shouldReload = true;
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

    private Fluid getFluidFromString(String string) {
        return getCompat().getFluid(new Identifier(string));
    }

    private Block getBlockFromString(String string) {
        return getCompat().getBlock(new Identifier(string));
    }

    @ApiStatus.AvailableSince("4.0")
    public void addGenerator(Fluid fluid, Generator generator) {
        externalMap.computeIfAbsent(fluid, g -> new ArrayList<>()).add(generator);
    }

    private void addToInternalMap(Fluid fluid, Generator generator) {
        generatorMap.computeIfAbsent(fluid, g -> new ArrayList<>()).add(generator);
    }

    private int addAllToInternalMap(Fluid fluid, List<Generator> generators) {
        int count = 0;
        for (Generator generator : generators) {
            Fluid genFluid = generator.getFluid();
            if (genFluid != null && genFluid == Fluids.EMPTY) {
                CGLog.warn("EMPTY fluid is detected! Skipping...");
                continue;
            }
            if (genFluid instanceof FlowableFluid)
                generator.setFluid(((FlowableFluid) genFluid).getStill());
            addToInternalMap(fluid, generator);
            count++;
        }
        return count;
    }

    @NotNull
    public Map<Fluid, List<Generator>> getGenerators() {
        return notNullOr(serverGeneratorMap, generatorMap);
    }

    @ApiStatus.Internal
    public Map<Fluid, List<Generator>> getRawGenerators() {
        return generatorMap;
    }

    @ApiStatus.Internal
    public boolean isSync() {
        return serverGeneratorMap != null;
    }

    @ApiStatus.Internal
    public void writeGeneratorsToPacket(PacketByteBuf buf) {
        buf.writeInt(generatorMap.size());

        for (Map.Entry<Fluid, List<Generator>> entry : generatorMap.entrySet()) {
            buf.writeIdentifier(getCompat().getFluidId(entry.getKey()));

            val gens = entry.getValue();
            buf.writeInt(gens.size());

            for (Generator generator : gens) {
                generator.toPacket(buf);
            }
        }
    }

    @ApiStatus.Internal
    public void readGeneratorsFromPacket(PacketByteBuf buf) {
        val _genSize = buf.readInt();
        val genMap = new HashMap<Fluid, List<Generator>>(_genSize);

        for (int i = 0; i < _genSize; i++) {
            val key = getCompat().getFluid(buf.readIdentifier());

            val _gensSize = buf.readInt();
            val gens = new ArrayList<Generator>(_gensSize);
            for (int j = 0; j < _gensSize; j++) {
                val generator = Generator.fromPacket(buf);
                if (generator == null) {
                    // Shouldn't be possible, but just in case... it's Java Reflection API after all.
                    CGLog.warn("Failed to retrieve a generator, skipping...");
                    continue;
                }
                gens.add(generator);
            }
            genMap.put(key, gens);
        }
        serverGeneratorMap = genMap;
    }

    @ApiStatus.Internal
    public void disconnect() {
        serverGeneratorMap = null;
    }

    @ApiStatus.Internal
    public void apply() {
        if (shouldReload) {
            CGLog.info((firstInit ? "L" : "Rel") + "oading config...");
            generatorMap.clear();

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
                            addToInternalMap(
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

                            addToInternalMap(actualFluid, generator);
                            count.getAndIncrement();
                        }
                    });
                });

            addToInternalMap(Fluids.LAVA, new StoneGenerator(stoneGen, Fluids.WATER, false));
            addToInternalMap(Fluids.LAVA, new CobbleGenerator(cobbleGen, Fluids.WATER, false));
            addToInternalMap(Fluids.LAVA, new BasaltGenerator(basaltGen, Blocks.BLUE_ICE, false));
            count.getAndAdd(3);

            externalMap.forEach((fluid, generators) -> count.getAndAdd(addAllToInternalMap(fluid, generators)));

            if (firstInit) firstInit = false;
            shouldReload = false;
            CGLog.info(count.get() + " generators has been " + (firstInit ? "" : "re") + "loaded");
        }
    }

    @ApiStatus.Internal
    public void reload() {
        shouldReload = true;
        this.apply();
    }

    @ApiStatus.Internal
    public boolean interact(WorldAccess world, BlockPos pos, BlockState state) {
        return interact(world, pos, state, false);
    }

    @ApiStatus.Internal
    public boolean interact(WorldAccess world, BlockPos pos, BlockState state, boolean fromTop) {
        FluidState fluidState = state.getFluidState();
        Fluid fluid = Generator.getStillFluid(fluidState);
        val generators = generatorMap.getOrDefault(fluid, List.of());

        for (Generator generator : generators) {
            if (!generator.check(world, pos, state, fromTop)) continue;
            if (fromTop && generator.getType() != GeneratorType.STONE) continue;

            val result = generator.tryGenerate(world, pos, state);
            if (result.isPresent()) {
                world.setBlockState(pos, result.get(), 3);
                if (!generator.isSilent())
                    world.syncWorldEvent(WorldEvents.LAVA_EXTINGUISHED, pos, 0);
                return true;
            }
        }
        return false;
    }

    @ApiStatus.Internal
    public boolean interactFromPipe(World world, BlockPos pos, Fluid fluid1, Fluid fluid2) {
        Fluid source;
        Fluid neighbour;
        List<Generator> generators = generatorMap.get(fluid1);
        if (generators == null) {
            generators = generatorMap.get(fluid2);
            source = fluid2;
            neighbour = fluid1;
        } else {
            source = fluid1;
            neighbour = fluid2;
        }

        for (Generator generator : generators) {
            boolean fromTop = false;
            if (neighbour instanceof FlowableFluid)
                fromTop = neighbour == ((FlowableFluid) neighbour).getStill();
            if (!generator.check(world, pos, source.getDefaultState().getBlockState(), fromTop)) continue;

            val result = generator.tryGenerate(world, pos, source.getDefaultState(), neighbour.getDefaultState());
            if (result.isPresent()) {
                world.setBlockState(pos, result.get());
                return true;
            }
        }
        return false;
    }
}