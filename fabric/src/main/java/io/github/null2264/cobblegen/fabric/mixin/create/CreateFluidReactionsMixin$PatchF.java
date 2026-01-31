#if FABRIC && MC>11605
package io.github.null2264.cobblegen.mixin.create;

import io.github.fabricators_of_create.porting_lib.fluids.FluidStack;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static io.github.null2264.cobblegen.CobbleGen.FLUID_INTERACTION;

/**
 * Mixin for Create Fabric Patch F
 */
@Pseudo
@Mixin(targets = {"com.simibubi.create.content.contraptions.fluids.FluidReactions", "com.simibubi.create.content.fluids.FluidReactions"})
public abstract class CreateFluidReactionsMixin$PatchF
{
    @SuppressWarnings("InvalidInjectorMethodSignature")
    @Inject(
        method = "handlePipeFlowCollision",
        at = @At(value = "HEAD"), cancellable = true
    )
    private static void generator$handlePipeFlowCollision(
        Level level, BlockPos pos, FluidStack fluid1, FluidStack fluid2, CallbackInfo ci
    ) {
        final boolean success = FLUID_INTERACTION.interactFromPipe(level, pos, fluid1.getFluid(), fluid2.getFluid());
        if (success)
            ci.cancel();
    }
}
#endif
