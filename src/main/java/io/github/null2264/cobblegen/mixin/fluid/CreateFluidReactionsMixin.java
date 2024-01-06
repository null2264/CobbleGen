//#if MC>1.16.5
package io.github.null2264.cobblegen.mixin.fluid;

//#if FABRIC>=1
import io.github.fabricators_of_create.porting_lib.util.FluidStack;
//#else
    //#if FORGE>=2 && MC>=1.20.2
    //$$ import net.neoforged.neoforge.fluids.FluidStack;
    //#else
    //$$ import net.minecraftforge.fluids.FluidStack;
    //#endif
//#endif
import io.github.null2264.cobblegen.data.model.Generator;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static io.github.null2264.cobblegen.CobbleGen.FLUID_INTERACTION;

@Pseudo
@Mixin(targets = {"com.simibubi.create.content.contraptions.fluids.FluidReactions", "com.simibubi.create.content.fluids.FluidReactions"})
public abstract class CreateFluidReactionsMixin
{
    private static boolean handleReaction(Level level, BlockPos pos, Fluid fluid1, Fluid fluid2) {
        return FLUID_INTERACTION.interactFromPipe(level, pos, fluid1, fluid2);
    }

    @SuppressWarnings("InvalidInjectorMethodSignature") // False positive
    @Inject(
            method = "handlePipeFlowCollision",
            //#if FABRIC<=0
            //$$ remap = false,
            //#endif
            at = @At(value = "HEAD"), cancellable = true
    )
    private static void generator$handlePipeFlowCollision(
            Level level, BlockPos pos, FluidStack fluid1, FluidStack fluid2, CallbackInfo ci
    ) {
        final boolean success = handleReaction(level, pos, fluid1.getFluid(), fluid2.getFluid());
        if (success)
            ci.cancel();
    }

    @Inject(
            method = "handlePipeSpillCollision",
            //#if FABRIC<=0
            //$$ remap = false,
            //#endif
            at = @At(value = "HEAD"),
            cancellable = true
    )
    private static void generator$handlePipeSpillCollision(
            Level level, BlockPos pos, Fluid pipeFluid, FluidState worldFluid, CallbackInfo ci
    ) {
        final boolean success = handleReaction(level, pos, Generator.getStillFluid(pipeFluid), worldFluid.getType());
        if (success)
            ci.cancel();
    }
}
//#endif