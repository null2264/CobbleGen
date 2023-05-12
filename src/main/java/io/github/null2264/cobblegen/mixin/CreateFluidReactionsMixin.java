package io.github.null2264.cobblegen.mixin;

import com.simibubi.create.content.contraptions.fluids.FluidReactions;
import io.github.fabricators_of_create.porting_lib.util.FluidStack;
import io.github.null2264.cobblegen.data.model.Generator;
import lombok.val;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static io.github.null2264.cobblegen.CobbleGen.FLUID_INTERACTION;

@Mixin(FluidReactions.class)
public abstract class CreateFluidReactionsMixin
{
    private static boolean handleReaction(World world, BlockPos pos, Fluid fluid1, Fluid fluid2) {
        return FLUID_INTERACTION.interactFromPipe(world, pos, fluid1, fluid2);
    }

    @Inject(method = "handlePipeFlowCollision", at = @At(value = "INVOKE", target = "Lcom/simibubi/create/foundation/utility/BlockHelper;destroyBlock(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;F)V", shift = At.Shift.AFTER), cancellable = true)
    private static void generator$handlePipeFlowCollision(
            World world, BlockPos pos, FluidStack fluid1, FluidStack fluid2, CallbackInfo ci
    ) {
        val success = handleReaction(world, pos, fluid1.getFluid(), fluid2.getFluid());
        if (success)
            ci.cancel();
    }

    @Inject(method = "handlePipeSpillCollision", at = @At(value = "HEAD"), cancellable = true)
    private static void generator$handlePipeSpillCollision(
            World world, BlockPos pos, Fluid pipeFluid, FluidState worldFluid, CallbackInfo ci
    ) {
        val success = handleReaction(world, pos, Generator.getStillFluid(pipeFluid), worldFluid.getFluid());
        if (success)
            ci.cancel();
    }
}