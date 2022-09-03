package io.github.null2264.cobblegen.mixin;

import io.github.null2264.cobblegen.util.BlockGenerator;
import io.github.null2264.cobblegen.util.Util;
import io.github.null2264.cobblegen.config.WeightedBlock;
import net.minecraft.block.BlockState;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.LavaFluid;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

import java.util.List;

import static io.github.null2264.cobblegen.CobbleGen.CONFIG;

@Mixin(LavaFluid.class)
public class LavaEventMixin
{
    @ModifyArgs(
        method = "flow",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/WorldAccess;setBlockState(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;I)Z"
        )
    )
    private void injected$flow(Args args, WorldAccess world, BlockPos pos, BlockState fluidBlockState, Direction direction, FluidState fluidState) {
        BlockGenerator generator = new BlockGenerator((World) world, pos, CONFIG.customGen.stoneGen, CONFIG.stoneGen);
        BlockState replacement = generator.getReplacement();

        if (replacement != null)
            args.set(1, replacement);
    }
}