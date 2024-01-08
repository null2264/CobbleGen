package io.github.null2264.cobblegen.mixin.fluid;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FlowingFluid;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static io.github.null2264.cobblegen.CobbleGen.FLUID_INTERACTION;

@Mixin(LiquidBlock.class)
public abstract class FluidEventMixin
{
    @Shadow
    @Final
    protected FlowingFluid fluid;

    @SuppressWarnings("unused")
    private void doInteraction(Level level, BlockPos pos, BlockState state, CallbackInfo ci) {
        final boolean success = FLUID_INTERACTION.interact(level, pos, state);

        if (success)
            ci.cancel();
    }

    private void doInteraction(Level level, BlockPos pos, BlockState state, CallbackInfoReturnable<Boolean> cir) {
        final boolean success = FLUID_INTERACTION.interact(level, pos, state);

        if (success)
            cir.setReturnValue(false);
    }

    //#if MC>=11900 && FABRIC<=0
    //$$ @Inject(method = "onPlace", at = @At("HEAD"), cancellable = true)
    //$$ private void fluidInteraction$onPlace(
    //$$         BlockState state,
    //$$         Level level,
    //$$         BlockPos pos,
    //$$         BlockState blockState2,
    //$$         boolean bl,
    //$$         CallbackInfo ci
    //$$ ) {
    //$$     // Forge 1.19.x completely changed Vanilla fluid behaviour
    //$$     // REF: https://github.com/MinecraftForge/MinecraftForge/pull/8695
    //$$     doInteraction(level, pos, state, ci);
    //$$ }
    //$$
    //$$ @Inject(method = "neighborChanged", at = @At("HEAD"), cancellable = true)
    //$$ private void fluidInteraction$neighborChangede(
    //$$         BlockState state,
    //$$         Level level,
    //$$         BlockPos pos,
    //$$         net.minecraft.world.level.block.Block block,
    //$$         BlockPos blockPos2,
    //$$         boolean bl,
    //$$         CallbackInfo ci
    //$$ ) {
    //$$     // Forge 1.19.x completely changed Vanilla fluid behaviour
    //$$     // REF: https://github.com/MinecraftForge/MinecraftForge/pull/8695
    //$$     doInteraction(level, pos, state, ci);
    //$$ }
    //#endif

    @Inject(method = "shouldSpreadLiquid", at = @At("HEAD"), cancellable = true)
    private void fluidInteraction$shouldSpreadLiquids(
            Level level,
            BlockPos pos,
            BlockState state,
            CallbackInfoReturnable<Boolean> cir
    ) {
        doInteraction(level, pos, state, cir);
    }
}