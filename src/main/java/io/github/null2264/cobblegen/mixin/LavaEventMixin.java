package io.github.null2264.cobblegen.mixin;

import io.github.null2264.cobblegen.util.BlockGenerator;
import io.github.null2264.cobblegen.util.FluidInteraction;
import io.github.null2264.cobblegen.util.GeneratorType;
import lombok.val;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.block.BlockState;
import net.minecraft.fluid.FlowableFluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.LavaFluid;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.WorldAccess;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin(LavaFluid.class)
public abstract class LavaEventMixin
{
    @Inject(method = "flow", at = @At("HEAD"), cancellable = true)
    public void head$flow(
            WorldAccess world,
            BlockPos pos,
            BlockState state,
            Direction direction,
            FluidState fluidState,
            CallbackInfo ci
    ) {
        if (direction != Direction.DOWN) return;

        @SuppressWarnings("DataFlowIssue")
        val success = FluidInteraction.doInteraction(world, pos, (FlowableFluid)(Object) this, true);
        if (success)
            ci.cancel();
    }

    /**
     * Handle fluid interactions (for stone generator).
     * If Porting Lib is installed, fluid interactions will be handled by {@link io.github.null2264.cobblegen.compat.porting_lib.FluidInteractionEvent}
     */
    @ModifyArgs(method = "flow", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/WorldAccess;setBlockState(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;I)Z"))
    private void injected$flow(
            Args args,
            WorldAccess world,
            BlockPos pos,
            BlockState fluidBlockState,
            Direction direction,
            FluidState fluidState
    ) {
        if (FabricLoader.getInstance().isModLoaded("porting_lib")) return;
        BlockGenerator generator = new BlockGenerator(world, pos, GeneratorType.STONE);
        generator.tryReplace(args);
    }
}