package io.github.null2264.cobblegen.mixin;

import io.github.null2264.cobblegen.config.CobbleGenConfig;
import io.github.null2264.cobblegen.util.Util;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FluidBlock;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

import static io.github.null2264.cobblegen.CobbleGen.CONFIG;

@Mixin(FluidBlock.class)
public class FluidEventMixin
{

    @ModifyArgs(
        method = "receiveNeighborFluids",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/World;setBlockState(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;)Z"
        )
    )
    private void injected$receiveNeighborFluids(Args args, World world, BlockPos pos, BlockState fluidBlockState) {
        BlockState state = args.get(1);
        String replacement = null;

        Block block = state.getBlock();
        if (block.equals(Blocks.COBBLESTONE)) {
            if (world.getBlockState(pos.down()).isOf(Blocks.BEDROCK))
                // TODO: Config for this
                replacement = "minecraft:stone";
            else
                replacement = Util.randomizeBlockId(CONFIG.cobbleGen);
        } else if (block.equals(Blocks.BASALT))
            replacement = Util.randomizeBlockId(CONFIG.basaltGen);

        args.set(1, replacement != null ? Registry.BLOCK.get(new Identifier(replacement)).getDefaultState() : state);
    }
}