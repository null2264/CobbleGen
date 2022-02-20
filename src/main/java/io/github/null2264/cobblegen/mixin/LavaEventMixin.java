package io.github.null2264.cobblegen.mixin;

import io.github.null2264.cobblegen.config.CobbleGenConfig;
import io.github.null2264.cobblegen.util.Util;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.LavaFluid;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.WorldAccess;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

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
        BlockState state = args.get(1);
        String replacement = null;

        if (world.getBlockState(pos.down()).isOf(Blocks.BEDROCK))
            replacement = "minecraft:dirt";
        else
            replacement = Util.randomizeBlockId(CONFIG.stoneGen);

        args.set(1, replacement != null ? Registry.BLOCK.get(new Identifier(replacement)).getDefaultState() : state);
    }
}