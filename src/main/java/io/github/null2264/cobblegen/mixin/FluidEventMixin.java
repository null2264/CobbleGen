package io.github.null2264.cobblegen.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import io.github.null2264.cobblegen.compat.porting_lib.FluidInteractionEvent;
import io.github.null2264.cobblegen.util.BlockGenerator;
import io.github.null2264.cobblegen.util.FluidInteraction;
import io.github.null2264.cobblegen.util.GeneratorType;
import lombok.val;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FluidBlock;
import net.minecraft.fluid.FlowableFluid;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin(FluidBlock.class)
public abstract class FluidEventMixin
{
    @Shadow @Final protected FlowableFluid fluid;
    private BlockState basaltReplacement;

    private boolean shouldBasaltGenerate(World world, BlockPos pos, BlockState state, Boolean canGenerate) {
        if (FabricLoader.getInstance().isModLoaded("porting_lib"))
            basaltReplacement = FluidInteractionEvent.invoke(world, pos, state);
        else {
            BlockGenerator basaltGenerator = new BlockGenerator(world, pos, GeneratorType.BASALT);
            basaltReplacement = basaltGenerator.getReplacement();
        }
        return basaltReplacement != null || canGenerate;
    }

    @Inject(method = "receiveNeighborFluids", at = @At("HEAD"), cancellable = true)
    private void customInteraction(World world, BlockPos pos, BlockState state, CallbackInfoReturnable<Boolean> cir) {
        val success = FluidInteraction.doInteraction(world, pos, state, fluid);
        if (success)
            cir.setReturnValue(false);
    }

    /**
     * Handle fluid interactions (for cobblestone generator).
     * If Porting Lib is installed, fluid interactions will be handled by {@link io.github.null2264.cobblegen.compat.porting_lib.FluidInteractionEvent}
     */
    @ModifyArgs(method = "receiveNeighborFluids", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;setBlockState(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;)Z", ordinal = 0))
    private void cobble$receiveNeighborFluids(
            @NotNull Args args,
            World world,
            BlockPos pos,
            BlockState fluidBlockState
    ) {
        if (FabricLoader.getInstance().isModLoaded("porting_lib")) return;
        if (((BlockState) args.get(1)).isOf(Blocks.OBSIDIAN)) return;

        BlockGenerator generator = new BlockGenerator(world, pos, GeneratorType.COBBLE);
        generator.tryReplace(args);
    }

    @ModifyExpressionValue(method = "receiveNeighborFluids(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;)Z", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/BlockState;isOf(Lnet/minecraft/block/Block;)Z", ordinal = 0))
    private boolean soulSoil$receiveNeighborFluid(
            boolean original,
            World world,
            BlockPos pos,
            BlockState state
    ) {
        return shouldBasaltGenerate(world, pos, state, original);
    }

    @ModifyArgs(method = "receiveNeighborFluids", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;setBlockState(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;)Z", ordinal = 1))
    private void basalt$receiveNeighborFluids(Args args, World world, BlockPos pos, BlockState fluidBlockState) {
        if (basaltReplacement != null) {
            args.set(1, basaltReplacement);
            basaltReplacement = null;
        }
    }
}