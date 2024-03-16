//#if MC>1.16.5
package io.github.null2264.cobblegen.mixin.create;

import io.github.null2264.cobblegen.data.model.Generator;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static io.github.null2264.cobblegen.CobbleGen.FLUID_INTERACTION;

@Mixin(com.simibubi.create.content.fluids.FluidReactions.class)
public abstract class CreateFluidReactionsMixin
{
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
        final boolean success =
            FLUID_INTERACTION.interactFromPipe(level, pos, Generator.getStillFluid(pipeFluid), worldFluid.getType());
        if (success)
            ci.cancel();
    }
}
//#endif