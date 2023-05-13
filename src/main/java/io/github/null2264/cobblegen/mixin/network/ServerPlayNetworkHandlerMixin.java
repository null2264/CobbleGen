package io.github.null2264.cobblegen.mixin.network;

import io.github.null2264.cobblegen.network.CGServerPlayNetworkHandler;
import io.github.null2264.cobblegen.util.Util;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.packet.c2s.play.CustomPayloadC2SPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayNetworkHandler.class)
public abstract class ServerPlayNetworkHandlerMixin
{
    @Unique
    private CGServerPlayNetworkHandler handlerCG;

    @Inject(method = "<init>", at = @At("RETURN"))
    private void init(MinecraftServer server, ClientConnection connection, ServerPlayerEntity player, CallbackInfo ci) {
        if (Util.isPortingLibLoaded()) {
            // Just in case
            if (connection instanceof io.github.fabricators_of_create.porting_lib.fake_players.FakeConnection) return;
        }
        //noinspection DataFlowIssue
        handlerCG = new CGServerPlayNetworkHandler((ServerPlayNetworkHandler) (Object) this);
        handlerCG.trySync();
    }

    @Inject(method = "onCustomPayload", at = @At("HEAD"), cancellable = true)
    private void handleCustomPayload(CustomPayloadC2SPacket packet, CallbackInfo ci) {
        if (handlerCG.handlePacket(packet)) {
            ci.cancel();
        }
    }
}