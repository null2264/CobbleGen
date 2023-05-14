package io.github.null2264.cobblegen.mixin;

import net.minecraft.server.MinecraftServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.concurrent.CompletableFuture;

import static io.github.null2264.cobblegen.CobbleGen.FLUID_INTERACTION;

@Mixin(MinecraftServer.class)
public abstract class MinecraftServerMixin
{
    @Inject(method = "runServer", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/MinecraftServer;setupServer()Z"))
    public void applyConfig(CallbackInfo ci) {
        FLUID_INTERACTION.apply();
    }

    @Inject(method = "reloadResources", at = @At("HEAD"))
    public void reloadConfig(CallbackInfoReturnable<CompletableFuture<Void>> cir) {
        FLUID_INTERACTION.apply();
    }
}