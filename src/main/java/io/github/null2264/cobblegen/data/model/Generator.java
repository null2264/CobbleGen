package io.github.null2264.cobblegen.data.model;

import io.github.null2264.cobblegen.config.WeightedBlock;
import io.github.null2264.cobblegen.util.GeneratorType;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.fluid.FlowableFluid;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldAccess;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface Generator extends PacketSerializable<Generator>
{
    static Fluid getStillFluid(FluidState fluidState) {
        try {
            return ((FlowableFluid) fluidState.getFluid()).getStill();
        } catch (ClassCastException ignore) {
            return getStillFluid(fluidState.getFluid());
        }
    }

    static Fluid getStillFluid(Fluid fluid) {
        if (fluid == Fluids.FLOWING_LAVA)
            return Fluids.LAVA;
        else if (fluid == Fluids.FLOWING_WATER)
            return Fluids.WATER;
        else if (fluid instanceof FlowableFluid)
            return ((FlowableFluid) fluid).getStill();
        return fluid;
    }

    ;

    Optional<BlockState> tryGenerate(WorldAccess world, BlockPos pos, BlockState state);

    /**
     * Only override if you want to support Create mod's pipe
     */
    default Optional<BlockState> tryGenerate(WorldAccess world, BlockPos pos, FluidState source, FluidState neighbour) {
        return Optional.empty();
    }

    @NotNull
    Map<String, List<WeightedBlock>> getOutput();

    @NotNull
    GeneratorType getType();

    /**
     * The neighbour {@link Fluid}, for Stone/Cobble-like generators.
     */
    @Nullable
    Fluid getFluid();

    @ApiStatus.Internal
    default void setFluid(Fluid fluid) {};

    /**
     * The neighbour {@link Block}, for Basalt-like generators.
     */
    @Nullable
    Block getBlock();

    default boolean isSilent() {
        return false;
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    default boolean check(WorldAccess world, BlockPos pos, BlockState state, boolean fromTop) {
        return true;
    }

    class Factory {
        public static Generator fromPacket(PacketByteBuf buf) {
            return null;
        }
    }
}