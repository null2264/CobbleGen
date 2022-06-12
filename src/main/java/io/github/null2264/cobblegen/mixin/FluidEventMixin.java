package io.github.null2264.cobblegen.mixin;

import io.github.null2264.cobblegen.config.WeightedBlock;
import io.github.null2264.cobblegen.util.Util;
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
import org.spongepowered.asm.mixin.injection.ModifyVariable;
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
            target = "Lnet/minecraft/world/World;setBlockState(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;)Z",
            ordinal = 0
        )
    )
    private void cobble$receiveNeighborFluids(Args args, World world, BlockPos pos, BlockState fluidBlockState) {
        if (((BlockState) args.get(1)).isOf(Blocks.OBSIDIAN))
            return;

        List<WeightedBlock> replacements = Util.getCustomReplacement(
            world, pos, CONFIG.customGen.cobbleGen, CONFIG.cobbleGen);

        if (replacements != null && replacements.size() >= 1)
            args.set(1, Registry.BLOCK.get(
                new Identifier(Util.randomizeBlockId(
                        replacements,
                        world.getDimensionKey().getValue().toString(),
                        pos.getY()
                ))).getDefaultState());
    }

    @SuppressWarnings("InvalidInjectorMethodSignature")
    @ModifyVariable(
        method = "receiveNeighborFluids(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;)Z",
        at = @At("STORE"),
        ordinal = 0
    )
    private boolean soulSoil$receiveNeighborFluid(boolean isExists, World world, BlockPos pos, BlockState fluidBlockState) {
        Map<String, List<WeightedBlock>> customGen = CONFIG.customGen.basaltGen;
        if (customGen != null && customGen.size() >= 1) {
            return customGen.get(
                Registry.BLOCK.getId(
                    world.getBlockState(pos.down()).getBlock()).toString()
            ) != null || isExists;
        }
        return isExists;
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
        List<WeightedBlock> replacements = Util.getCustomReplacement(
            world, pos, CONFIG.customGen.basaltGen, CONFIG.basaltGen);

        if (replacements != null && replacements.size() >= 1)
            args.set(1, Registry.BLOCK.get(
                new Identifier(Util.randomizeBlockId(
                        replacements,
                        world.getDimensionKey().getValue().toString(),
                        pos.getY()
                ))).getDefaultState());
    }
}