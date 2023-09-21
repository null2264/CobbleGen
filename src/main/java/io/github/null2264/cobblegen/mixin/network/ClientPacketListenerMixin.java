package io.github.null2264.cobblegen.mixin.network;

import io.github.null2264.cobblegen.network.CGClientPlayNetworkHandler;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.chat.Component;
//#if MC<1.20.2
import net.minecraft.network.protocol.game.ClientboundCustomPayloadPacket;
//#else
//$$ import net.minecraft.network.protocol.common.ClientboundCustomPayloadPacket;
//#endif
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPacketListener.class)
public abstract class ClientPacketListenerMixin
{
    //#if MC<=1.20.1
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
    //#endif
}