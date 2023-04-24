package io.github.null2264.cobblegen.mixin;

import io.github.null2264.cobblegen.util.BlockGenerator;
import io.github.null2264.cobblegen.util.GeneratorType;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.block.BlockState;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.LavaFluid;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin(LavaFluid.class)
public class LavaEventMixin
{
    /**
     * Handle fluid interactions (for stone generator).
     * If Porting Lib is installed, fluid interactions will be handled by {@link io.github.null2264.cobblegen.compat.porting_lib.FluidInteractionEvent}
     */
    @ModifyArgs(method = "flow", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/WorldAccess;setBlockState(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;I)Z"))
    private void injected$flow(
            Args args,
            WorldAccess world,
            BlockPos pos,
            BlockState fluidBlockState,
            Direction direction,
            FluidState fluidState
    ) {
        if (FabricLoader.getInstance().isModLoaded("porting_lib")) return;
        BlockGenerator generator = new BlockGenerator((World) world, pos, GeneratorType.STONE);
        generator.tryReplace(args);
    }
}