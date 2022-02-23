package io.github.null2264.cobblegen.mixin;

import io.github.null2264.cobblegen.util.Util;
import io.github.null2264.cobblegen.config.WeightedBlock;
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

import java.util.List;
import java.util.Map;

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
        List<WeightedBlock> replacements = null;

        Block block = state.getBlock();
        if (block.equals(Blocks.COBBLESTONE)) {
            Map<String, List<WeightedBlock>> customGen = CONFIG.customGen.cobbleGen;
            if (customGen != null)
                replacements = customGen.get(
                    Registry.BLOCK.getId(
                        world.getBlockState(pos.down()).getBlock()
                    ).toString()
                );

            if (replacements == null)
                replacements = CONFIG.cobbleGen;
        } else if (block.equals(Blocks.BASALT))
            replacements = CONFIG.basaltGen;

        if (replacements != null && replacements.size() >= 1)
            args.set(1, Registry.BLOCK.get(
                new Identifier(Util.randomizeBlockId(replacements))).getDefaultState());
    }
}