//#if FABRIC && MC>1.16.5
package io.github.null2264.cobblegen.mixin.create;

import com.simibubi.create.api.event.PipeCollisionEvent;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Optional;

import static io.github.null2264.cobblegen.CobbleGen.FLUID_INTERACTION;

/**
 * Mixin for Create Fabric Patch F
 */
@Pseudo
@Mixin(targets = {"com.simibubi.create.content.contraptions.fluids.FluidReactions", "com.simibubi.create.content.fluids.FluidReactions"})
public abstract class CreateFluidReactionsMixinPatchF
{
    @Inject(
        method = "handlePipeFlowCollisionFallback",
        at = @At(value = "HEAD"), cancellable = true
    )
    private static void generator$handlePipeFlowCollision(PipeCollisionEvent.Flow event, CallbackInfo ci) {
        final Optional<BlockState> state =
            FLUID_INTERACTION.interactFromPipeState(event.getLevel(), event.getPos(), event.getFirstFluid(), event.getSecondFluid());
        if (state.isPresent()) {
            event.setState(state.get());
            ci.cancel();
        }
    }
}
//#endif