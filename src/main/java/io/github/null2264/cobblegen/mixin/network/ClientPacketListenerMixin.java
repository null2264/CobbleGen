package io.github.null2264.cobblegen.mixin.network;

import io.github.null2264.cobblegen.network.CGClientPlayNetworkHandler;
import net.fabricmc.api.Environment;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundCustomPayloadPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static net.fabricmc.api.EnvType.CLIENT;

@Environment(CLIENT)
@Mixin(ClientPacketListener.class)
public abstract class ClientPacketListenerMixin
{
    @SuppressWarnings("DataFlowIssue")
    private ClientPacketListener getListener() {
        return (ClientPacketListener) (Object) this;
    }

    @Inject(method = "handleCustomPayload", at = @At("HEAD"), cancellable = true)
    private void handleCustomPayload(ClientboundCustomPayloadPacket packet, CallbackInfo ci) {
        if (CGClientPlayNetworkHandler.handlePacket(getListener(), packet)) {
            ci.cancel();
        }
    }

    @Inject(method = "onDisconnect", at = @At("HEAD"))
    private void handleDisconnect(Component reason, CallbackInfo ci) {
        CGClientPlayNetworkHandler.onDisconnect();
    }
}