package io.github.null2264.cobblegen.mixin;

import io.github.null2264.cobblegen.config.WeightedBlock;
import io.github.null2264.cobblegen.util.BlockGenerator;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FluidBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

import java.util.List;
import java.util.Map;

import static io.github.null2264.cobblegen.CobbleGen.CONFIG;

@Mixin(FluidBlock.class)
public class FluidEventMixin {

    @ModifyArgs(
            method = "receiveNeighborFluids",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/World;setBlockState(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;)Z",
                    ordinal = 0
            )
    )
    private void cobble$receiveNeighborFluids(Args args, World world, BlockPos pos, BlockState fluidBlockState) {
        if (((BlockState) args.get(1)).isOf(Blocks.OBSIDIAN))
            return;

        BlockGenerator generator = new BlockGenerator(world, pos, CONFIG.customGen.cobbleGen, CONFIG.cobbleGen);
        BlockState replacement = generator.getReplacement();

        if (replacement != null)
            args.set(1, replacement);
    }

    @Redirect(
            method = "receiveNeighborFluids(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;)Z",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/block/BlockState;isOf(Lnet/minecraft/block/Block;)Z", ordinal = 0)
    )
    private boolean soulSoil$receiveNeighborFluid(BlockState instance, Block block) {
        boolean originalValue = instance.isOf(block);

        Map<String, List<WeightedBlock>> customGen = CONFIG.customGen.basaltGen;
        if (customGen != null) {
            return customGen.get(Registry.BLOCK.getId(instance.getBlock()).toString()) != null || originalValue;
        }
        return originalValue;
    }

    @ModifyArgs(
            method = "receiveNeighborFluids",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/World;setBlockState(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;)Z",
                    ordinal = 1
            )
    )
    private void basalt$receiveNeighborFluids(Args args, World world, BlockPos pos, BlockState fluidBlockState) {
        BlockGenerator generator = new BlockGenerator(world, pos, CONFIG.customGen.basaltGen, CONFIG.basaltGen);
        BlockState replacement = generator.getReplacement();

        if (replacement != null)
            args.set(1, replacement);
    }
}