package io.github.null2264.cobblegen.mixin.fluid;

import lombok.val;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FlowingFluid;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static io.github.null2264.cobblegen.CobbleGen.FLUID_INTERACTION;

@Mixin(LiquidBlock.class)
public abstract class FluidEventMixin
{
    @Shadow
    @Final
    protected FlowingFluid fluid;

    @Inject(method = "shouldSpreadLiquid", at = @At("HEAD"), cancellable = true)
    private void fluidInteraction$shouldSpreadLiquids(
            Level world,
            BlockPos pos,
            BlockState state,
            CallbackInfoReturnable<Boolean> cir
    ) {
        val success = FLUID_INTERACTION.interact(world, pos, state);

        if (success)
            cir.setReturnValue(false);
    }
}