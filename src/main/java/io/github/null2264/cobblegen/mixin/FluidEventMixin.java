package io.github.null2264.cobblegen.mixin;

import io.github.null2264.cobblegen.util.FluidInteraction;
import lombok.val;
import net.minecraft.block.BlockState;
import net.minecraft.block.FluidBlock;
import net.minecraft.fluid.FlowableFluid;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(FluidBlock.class)
public abstract class FluidEventMixin
{
    @Shadow @Final protected FlowableFluid fluid;

    @Inject(method = "receiveNeighborFluids", at = @At("HEAD"), cancellable = true)
    private void fluidInteraction$receiveNeighborFluids(World world, BlockPos pos, BlockState state, CallbackInfoReturnable<Boolean> cir) {
        val success = FluidInteraction.interact(world, pos, state);

        if (success)
            cir.setReturnValue(false);
    }
}