package io.github.null2264.cobblegen.data.model;

import io.github.null2264.cobblegen.config.WeightedBlock;
import io.github.null2264.cobblegen.util.GeneratorType;
import lombok.val;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface Generator extends PacketSerializable<Generator>
{
    static Fluid getStillFluid(FluidState fluidState) {
        try {
            return ((FlowingFluid) fluidState.getType()).getSource();
        } catch (ClassCastException ignore) {
            return getStillFluid(fluidState.getType());
        }
    }

    static Fluid getStillFluid(Fluid fluid) {
        if (fluid == Fluids.FLOWING_LAVA)
            return Fluids.LAVA;
        else if (fluid == Fluids.FLOWING_WATER)
            return Fluids.WATER;
        else if (fluid instanceof FlowingFluid)
            return ((FlowingFluid) fluid).getSource();
        return fluid;
    }

    ;

    Optional<BlockState> tryGenerate(LevelAccessor level, BlockPos pos, BlockState state);

    /**
     * Only override if you want to support Create mod's pipe
     */
    default Optional<BlockState> tryGenerate(LevelAccessor level, BlockPos pos, FluidState source, FluidState neighbour) {
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
    default boolean check(LevelAccessor level, BlockPos pos, BlockState state, boolean fromTop) {
        return true;
    }

    static Generator fromPacket(FriendlyByteBuf buf) {
        val className = buf.readUtf();
        try {
            Method method = Class.forName(className).getMethod("fromPacket", FriendlyByteBuf.class);
            return (Generator) method.invoke(null, buf);
        } catch (Exception ignored) {
        }
        return null;
    }
}