package io.github.null2264.cobblegen.data;

import com.google.common.collect.ImmutableList;
import io.github.null2264.cobblegen.CobbleGenPlugin;
import io.github.null2264.cobblegen.compat.ByteBufCompat;
import io.github.null2264.cobblegen.data.model.CGRegistry;
import io.github.null2264.cobblegen.data.model.Generator;
import io.github.null2264.cobblegen.util.CGLog;
import io.github.null2264.cobblegen.util.GeneratorType;
import io.github.null2264.cobblegen.util.PluginFinder;
import io.github.null2264.cobblegen.util.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

import static io.github.null2264.cobblegen.compat.CollectionCompat.listOf;
import static io.github.null2264.cobblegen.util.Constants.LAVA_FIZZ;
import static io.github.null2264.cobblegen.util.Util.notNullOr;

/**
 * Replacement for BlockGenerator. This will act like Vanilla's registry system
 */
public class FluidInteractionHelper
{
    public static final ImmutableList<Direction> FLOW_DIRECTIONS = ImmutableList.of(
            Direction.DOWN, Direction.SOUTH, Direction.NORTH, Direction.EAST, Direction.WEST
    );

    private final Map<Fluid, List<Generator>> generatorMap = new HashMap<>();
    private @Nullable Map<Fluid, List<Generator>> serverGeneratorMap = null;

    private boolean firstInit = true;
    private boolean shouldReload = true;
    private final AtomicInteger count = new AtomicInteger();

    @ApiStatus.AvailableSince("4.0")
    public void addGenerator(Fluid fluid, Generator generator) {
        Fluid genFluid = generator.getFluid();
        if (genFluid != null && genFluid == Fluids.EMPTY) {
            CGLog.warn("EMPTY fluid is detected! Skipping...");
            return;
        }
        if (genFluid instanceof FlowingFluid)
            generator.setFluid(((FlowingFluid) genFluid).getSource());
        generatorMap.computeIfAbsent(fluid, g -> new ArrayList<>()).add(generator);
        count.incrementAndGet();
    }

    @NotNull
    public Map<Fluid, List<Generator>> getGenerators() {
        return notNullOr(serverGeneratorMap, generatorMap);
    }

    @ApiStatus.Internal
    public boolean isSync() {
        return serverGeneratorMap != null;
    }

    @ApiStatus.Internal
    @Deprecated
    //#if MC>1.16.5
    (since = "5.1", forRemoval = true)
    //#endif
    public void writeGeneratorsToPacket(ByteBufCompat buf) {
        write(buf);
    }

    @ApiStatus.Internal
    public void readGeneratorsFromPacket(ByteBufCompat buf) {
        final int _genSize = buf.readInt();
        final HashMap<Fluid, List<Generator>> genMap = new HashMap<>(_genSize);

        for (int i = 0; i < _genSize; i++) {
            final Fluid key = Util.getFluid(buf.readResourceLocation());

            final int _gensSize = buf.readInt();
            final ArrayList<Generator> gens = new ArrayList<>(_gensSize);
            for (int j = 0; j < _gensSize; j++) {
                final Generator generator = Generator.fromPacket(buf);
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
            CGLog.info(firstInit ? "Loading" : "Reloading", "generators...");
            generatorMap.clear();
            count.set(0);

            CGRegistry registry = new CGRegistryImpl();
            for (PluginFinder.PlugInContainer container : PluginFinder.getModPlugins()) {
                String id = container.getModId();
                CobbleGenPlugin plugin = container.getPlugin();
                CGLog.info(firstInit ? "Loading" : "Reloading", "plugin from", id);
                try {
                    if (!firstInit) plugin.onReload();
                    plugin.registerInteraction(registry);
                } catch (Throwable err) {
                    CGLog.warn("Something went wrong while", firstInit ? "loading" : "reloading", "plugin provided by", id);
                    CGLog.error(err);
                    continue;
                }
                CGLog.info(firstInit ? "Loaded" : "Reloaded", "plugin from", id);
            }

            CGLog.info(String.valueOf(count.get()), "generators has been", firstInit ? "loaded" : "reloaded");
            if (firstInit) firstInit = false;
            shouldReload = false;
        }
    }

    @ApiStatus.Internal
    public void reload() {
        shouldReload = true;
        this.apply();
    }

    @ApiStatus.Internal
    public boolean interact(LevelAccessor level, BlockPos pos, BlockState state) {
        return interact(level, pos, state, false);
    }

    @ApiStatus.Internal
    public boolean interact(LevelAccessor level, BlockPos pos, BlockState state, boolean fromTop) {
        FluidState fluidState = state.getFluidState();
        Fluid fluid = Generator.getStillFluid(fluidState);
        final List<Generator> generators = generatorMap.getOrDefault(fluid, listOf());

        for (Generator generator : generators) {
            if (!generator.check(level, pos, state, fromTop)) continue;
            if (fromTop && generator.getType() != GeneratorType.STONE) continue;

            final Optional<BlockState> result = generator.tryGenerate(level, pos, state);
            if (result.isPresent()) {
                level.setBlock(pos, result.get(), 3);
                if (!generator.isSilent())
                    level.levelEvent(LAVA_FIZZ, pos, 0);
                return true;
            }
        }
        return false;
    }

    @ApiStatus.Internal
    public boolean interactFromPipe(Level level, BlockPos pos, Fluid fluid1, Fluid fluid2) {
        Fluid source;
        Fluid neighbour;
        List<Generator> generators = generatorMap.get(fluid1);
        if (generators == null) {
            generators = generatorMap.get(fluid2);
            if (generators == null)
                return false;
            source = fluid2;
            neighbour = fluid1;
        } else {
            source = fluid1;
            neighbour = fluid2;
        }

        for (Generator generator : generators) {
            boolean fromTop = false;
            if (neighbour instanceof FlowingFluid)
                fromTop = neighbour == ((FlowingFluid) neighbour).getSource();
            if (!generator.check(level, pos, source.defaultFluidState().createLegacyBlock(), fromTop)) continue;

            if (source == Fluids.LAVA)  // prevent obsidian from generating
                source = ((FlowingFluid) source).getFlowing();

            final Optional<BlockState> result = generator.tryGenerate(level, pos, source.defaultFluidState(), neighbour.defaultFluidState());
            if (result.isPresent()) {
                level.setBlockAndUpdate(pos, result.get());
                return true;
            }
        }
        return false;
    }

    public void write(ByteBufCompat buf) {
        buf.writeInt(generatorMap.size());

        for (Map.Entry<Fluid, List<Generator>> entry : generatorMap.entrySet()) {
            buf.writeResourceLocation(Util.getFluidId(entry.getKey()));

            final List<Generator> gens = entry.getValue();
            buf.writeInt(gens.size());

            for (Generator generator : gens) {
                generator.toPacket(buf);
            }
        }
    }
}