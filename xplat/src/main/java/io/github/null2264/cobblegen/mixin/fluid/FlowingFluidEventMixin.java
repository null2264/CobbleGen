package io.github.null2264.cobblegen.mixin.fluid;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.FluidState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static io.github.null2264.cobblegen.CobbleGen.FLUID_INTERACTION;

@Mixin(FlowingFluid.class)
public abstract class FlowingFluidEventMixin
{
    @Inject(method = "spreadTo", at = @At(value = "HEAD"), cancellable = true)
    public void fluidInteraction$flow(
            LevelAccessor level,
            BlockPos pos,
            BlockState state,
            Direction direction,
            FluidState fluidState,
            CallbackInfo ci
    ) {
        if (direction != Direction.DOWN) return;

        final boolean success = FLUID_INTERACTION.interact(level, pos, level.getBlockState(pos.above()), true);

        if (success)
            ci.cancel();
    }
}
