package io.github.null2264.cobblegen.data;

import com.google.common.collect.ImmutableList;
import io.github.null2264.cobblegen.data.model.Generator;
import io.github.null2264.cobblegen.util.CGLog;
import io.github.null2264.cobblegen.util.CobbleGenPlugin;
import io.github.null2264.cobblegen.util.GeneratorType;
import io.github.null2264.cobblegen.util.PluginFinder;
import lombok.val;
import net.fabricmc.loader.api.entrypoint.EntrypointContainer;
import net.minecraft.block.BlockState;
import net.minecraft.fluid.FlowableFluid;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldEvents;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import static io.github.null2264.cobblegen.CobbleGen.getCompat;
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
    private boolean shouldReload;
    private final AtomicInteger count = new AtomicInteger();

    public FluidInteractionHelper() {
        shouldReload = true;
    }

    @ApiStatus.AvailableSince("4.0")
    public void addGenerator(Fluid fluid, Generator generator) {
        Fluid genFluid = generator.getFluid();
        if (genFluid != null && genFluid == Fluids.EMPTY) {
            CGLog.warn("EMPTY fluid is detected! Skipping...");
            return;
        }
        if (genFluid instanceof FlowableFluid)
            generator.setFluid(((FlowableFluid) genFluid).getStill());
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
            CGLog.info((firstInit ? "L" : "Rel") + "oading generators...");
            generatorMap.clear();
            count.set(0);
            for (EntrypointContainer<CobbleGenPlugin> plugin : PluginFinder.getModPlugins()) {
                String id = plugin.getProvider().getMetadata().getId();
                CGLog.info("Loading plugin from", id);
                try {
                    plugin.getEntrypoint().registerInteraction();
                } catch (Throwable err) {
                    CGLog.warn("Something went wrong while loading plugin provided by", id);
                    CGLog.error(String.valueOf(err));
                    continue;
                }
                CGLog.info("Loaded plugin from", id);
            }

            shouldReload = false;
            CGLog.info(String.valueOf(count.get()), "generators has been", (firstInit ? "" : "re") + "loaded");
            if (firstInit) firstInit = false;
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