#if MC>11605
package io.github.null2264.cobblegen.forge.mixin.create;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static io.github.null2264.cobblegen.CobbleGen.FLUID_INTERACTION;

#if MC>=12002
import net.neoforged.neoforge.fluids.FluidStack;
#else
import net.minecraftforge.fluids.FluidStack;
#endif

/**
 * Mixin for Create Fabric (pre) Patch F
 */
@Pseudo
@Mixin(targets = {"com.simibubi.create.content.contraptions.fluids.FluidReactions", "com.simibubi.create.content.fluids.FluidReactions"})
public abstract class CreateFluidReactionsMixin$PatchE
{
    @Inject(
        method = "handlePipeFlowCollision",
        #if FORGE
        remap = false,
        #endif
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
