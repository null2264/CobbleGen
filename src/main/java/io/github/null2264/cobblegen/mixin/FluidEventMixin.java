package io.github.null2264.cobblegen.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import io.github.null2264.cobblegen.util.BlockGenerator;
import io.github.null2264.cobblegen.util.GeneratorType;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FluidBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin(FluidBlock.class)
public abstract class FluidEventMixin
{
    private BlockState basaltReplacement;

    private boolean shouldBasaltGenerate(World world, BlockPos pos) {
        BlockGenerator basaltGenerator = new BlockGenerator(world, pos, GeneratorType.BASALT);
        basaltReplacement = basaltGenerator.getReplacement();
        return basaltReplacement != null;
    }

    @ModifyArgs(method = "receiveNeighborFluids", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;setBlockState(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;)Z", ordinal = 0))
    private void cobble$receiveNeighborFluids(Args args, World world, BlockPos pos, BlockState fluidBlockState) {
        if (((BlockState) args.get(1)).isOf(Blocks.OBSIDIAN)) return;

        BlockGenerator generator = new BlockGenerator(world, pos, GeneratorType.COBBLE);
        generator.tryReplace(args);
    }

    @ModifyExpressionValue(method = "receiveNeighborFluids(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;)Z", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/BlockState;isOf(Lnet/minecraft/block/Block;)Z", ordinal = 0))
    private boolean soulSoil$receiveNeighborFluid(boolean original, World world, BlockPos pos, BlockState state) {
        return shouldBasaltGenerate(world, pos) || original;
    }

    @ModifyArgs(method = "receiveNeighborFluids", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;setBlockState(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;)Z", ordinal = 1))
    private void basalt$receiveNeighborFluids(Args args, World world, BlockPos pos, BlockState fluidBlockState) {
        BlockState replacement = basaltReplacement;

        if (replacement != null) args.set(1, replacement);
    }
}