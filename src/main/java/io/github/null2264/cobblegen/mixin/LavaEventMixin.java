package io.github.null2264.cobblegen.mixin;

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

import static io.github.null2264.cobblegen.CobbleGen.FLUID_INTERACTION;

@Mixin(LavaFluid.class)
public abstract class LavaEventMixin
{
    @Inject(method = "flow", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/WorldAccess;getFluidState(Lnet/minecraft/util/math/BlockPos;)Lnet/minecraft/fluid/FluidState;"), cancellable = true)
    public void fluidInteraction$flow(
            WorldAccess world,
            BlockPos pos,
            BlockState state,
            Direction direction,
            FluidState fluidState,
            CallbackInfo ci
    ) {
        val success = FLUID_INTERACTION.interact(world, pos, world.getBlockState(pos.up()), true);

        if (success)
            ci.cancel();
    }
}