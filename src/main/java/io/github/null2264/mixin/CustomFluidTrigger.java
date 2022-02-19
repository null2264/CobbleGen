package io.github.null2264.mixin;

import io.github.null2264.config.CobbleGenConfig;
import io.github.null2264.util.Util;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FluidBlock;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;

@Mixin(FluidBlock.class)
public class CustomFluidTrigger
{

    @ModifyArg(
        method = "receiveNeighborFluids(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;)Z",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/World;setBlockState(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;)Z"
        ),
        index = 1
    )
    private BlockState injected(BlockState state) {
        Block block = state.getBlock();

        if (block.equals(Blocks.COBBLESTONE)) {
            // TODO: Make it actually randomized
            String replacement = Util.randomizeBlockId(CobbleGenConfig.get().cobbleGen);
            return Registry.BLOCK.get(new Identifier(replacement)).getDefaultState();
        } else if (block.equals(Blocks.BASALT)) {
            String replacement = Util.randomizeBlockId(CobbleGenConfig.get().basaltGen);
            return Registry.BLOCK.get(new Identifier(replacement)).getDefaultState();
        }

        // Obsidian maybe?
        return state;
    }
}