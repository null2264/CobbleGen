package io.github.null2264.cobblegen.mixin.fluid;

import com.simibubi.create.content.contraptions.fluids.FluidReactions;
//#if FABRIC>=1
import io.github.fabricators_of_create.porting_lib.util.FluidStack;
//#else
//$$ import net.minecraftforge.fluids.FluidStack;
//#endif
import io.github.null2264.cobblegen.data.model.Generator;
import lombok.val;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static io.github.null2264.cobblegen.CobbleGen.FLUID_INTERACTION;

@Mixin(FluidReactions.class)
public abstract class CreateFluidReactionsMixin
{
    private static boolean handleReaction(Level level, BlockPos pos, Fluid fluid1, Fluid fluid2) {
        return FLUID_INTERACTION.interactFromPipe(level, pos, fluid1, fluid2);
    }

    @Inject(
            //#if FABRIC
            method = "handlePipeFlowCollision(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lio/github/fabricators_of_create/porting_lib/util/FluidStack;Lio/github/fabricators_of_create/porting_lib/util/FluidStack;)V",
            //#else
            //$$ method = "handlePipeFlowCollision(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraftforge/fluids/FluidStack;Lnet/minecraftforge/fluids/FluidStack;)V",
            //$$ remap = false,
            //#endif
            at = @At(value = "HEAD"), cancellable = true
    )
    private static void generator$handlePipeFlowCollision(
            Level level, BlockPos pos, FluidStack fluid1, FluidStack fluid2, CallbackInfo ci
    ) {
        val success = handleReaction(level, pos, fluid1.getFluid(), fluid2.getFluid());
        if (success)
            ci.cancel();
    }

    @Inject(
            method = "handlePipeSpillCollision(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/material/Fluid;Lnet/minecraft/world/level/material/FluidState;)V",
            //#if FABRIC<=0
            //$$ remap = false,
            //#endif
            at = @At(value = "HEAD"),
            cancellable = true
    )
    private static void generator$handlePipeSpillCollision(
            Level level, BlockPos pos, Fluid pipeFluid, FluidState worldFluid, CallbackInfo ci
    ) {
        val success = handleReaction(level, pos, Generator.getStillFluid(pipeFluid), worldFluid.getType());
        if (success)
            ci.cancel();
    }
}