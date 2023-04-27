package io.github.null2264.cobblegen.mixin;

import com.simibubi.create.content.contraptions.fluids.FluidReactions;
import io.github.fabricators_of_create.porting_lib.util.FluidStack;
import io.github.null2264.cobblegen.data.BlockGenerator;
import io.github.null2264.cobblegen.util.GeneratorType;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin(FluidReactions.class)
public abstract class CreateFluidReactionsMixin
{
    private static void handleReaction(@NotNull Args args, World world, BlockPos pos) {
        BlockState state = args.get(1);
        if (state.isOf(Blocks.STONE) || state.isOf(Blocks.COBBLESTONE)) {
            BlockGenerator generator = new BlockGenerator(world,
                                                          pos,
                                                          state.isOf(Blocks.STONE) ? GeneratorType.STONE : GeneratorType.COBBLE
            );
            generator.tryReplace(args);
        }
    }

    @ModifyArgs(method = "handlePipeFlowCollision(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lio/github/fabricators_of_create/porting_lib/util/FluidStack;Lio/github/fabricators_of_create/porting_lib/util/FluidStack;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;setBlockState(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;)Z"))
    private static void generator$handlePipeFlowCollision(
            Args args, World world, BlockPos pos, FluidStack fluid, FluidStack fluid1
    ) {
        handleReaction(args, world, pos);
    }

    @ModifyArgs(method = "handlePipeSpillCollision(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/fluid/Fluid;Lnet/minecraft/fluid/FluidState;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;setBlockState(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;)Z"))
    private static void generator$handlePipeSpillCollision(
            Args args, World world, BlockPos pos, Fluid fluid, FluidState state
    ) {
        handleReaction(args, world, pos);
    }
}