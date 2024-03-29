package io.github.null2264.cobblegen.mixin.network;

import io.github.null2264.cobblegen.network.CGClientPlayNetworkHandler;
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

@Mixin(
        //#if MC<1.20.2
        net.minecraft.client.multiplayer.ClientPacketListener.class
        //#else
        //$$ net.minecraft.client.multiplayer.ClientCommonPacketListenerImpl.class
        //#endif
)
public abstract class ClientCommonPacketListenerMixin
{
    @SuppressWarnings("DataFlowIssue")
    //#if MC<1.20.2
    private net.minecraft.client.multiplayer.ClientPacketListener getListener() {
        return (net.minecraft.client.multiplayer.ClientPacketListener) (Object) this;
    //#else
    //$$     private net.minecraft.client.multiplayer.ClientCommonPacketListenerImpl getListener() {
    //$$         return (net.minecraft.client.multiplayer.ClientCommonPacketListenerImpl) (Object) this;
    //#endif
    }

    //#if MC<1.20.2
    @Inject(method = "handleCustomPayload", at = @At("HEAD"), cancellable = true)
    //#else
    //$$ @SuppressWarnings("AmbiguousMixinReference")
    //$$ @Inject(method = "handleCustomPayload(Lnet/minecraft/network/protocol/common/ClientboundCustomPayloadPacket;)V", at = @At("HEAD"), cancellable = true)
    //#endif
    private void handleCustomPayload(ClientboundCustomPayloadPacket packet, CallbackInfo ci) {
        if (CGClientPlayNetworkHandler.handlePacket(
                getListener(),
                //#if MC<1.20.2
                packet
                //#else
                //$$ packet.payload()
                //#endif
        )) {
            ci.cancel();
        }
    }

    //#if MC<1.20.2
    @Inject(method = "onDisconnect", at = @At("HEAD"))
    private void handleDisconnect(Component reason, CallbackInfo ci) {
        CGClientPlayNetworkHandler.onDisconnect();
    }
    //#endif
}