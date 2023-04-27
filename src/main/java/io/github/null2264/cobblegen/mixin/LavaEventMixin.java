package io.github.null2264.cobblegen.mixin;

import io.github.null2264.cobblegen.data.GeneratorData;
import io.github.null2264.cobblegen.util.FluidInteraction;
import lombok.val;
import net.minecraft.block.BlockState;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.LavaFluid;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.WorldAccess;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LavaFluid.class)
public abstract class LavaEventMixin
{
    @Inject(method = "flow", at = @At("HEAD"), cancellable = true)
    public void fluidInteraction$flow(
            WorldAccess world,
            BlockPos pos,
            BlockState state,
            Direction direction,
            FluidState fluidState,
            CallbackInfo ci
    ) {
        if (direction != Direction.DOWN) return;

        val success = FluidInteraction.interact(world, pos, state, new GeneratorData(true));

        if (success)
            ci.cancel();
    }
}