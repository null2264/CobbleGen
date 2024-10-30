package io.github.null2264.cobblegen.mixin.network;

//#if MC<1.20.2
import net.minecraft.network.protocol.game.ClientboundCustomPayloadPacket;
//#else
//#if MC>=1.20.4 && MC<1.20.5
//$$ import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
//#else if MC>=1.20.5
//$$ import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
//$$ import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
//$$ import io.github.null2264.cobblegen.network.payload.CGPacketPayload;
//$$ import net.minecraft.network.protocol.Packet;
//$$ import net.minecraft.network.protocol.common.ClientCommonPacketListener;
//$$ import net.minecraft.network.protocol.common.ServerboundCustomPayloadPacket;
//#endif
//$$ import net.minecraft.network.protocol.common.ClientboundCustomPayloadPacket;
//#endif

import io.github.null2264.cobblegen.network.CGClientPlayNetworkHandler;
import net.minecraft.network.chat.Component;
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

    //#if FORGE>1
    //#if MC>=1.20.4 && MC<1.20.5
    //$$ @ModifyExpressionValue(
    //$$     method = "send",
    //$$     at = @At(value = "INVOKE", target = "Lnet/neoforged/neoforge/network/registration/NetworkRegistry;canSendPacket(Lnet/minecraft/network/protocol/Packet;Lnet/minecraft/network/protocol/common/ClientCommonPacketListener;)Z")
    //$$ )
    //$$ private boolean validateCobbleGen(boolean original, Packet<?> packet) {
    //$$     if (packet instanceof ServerboundCustomPayloadPacket customPayloadPacket) {
    //$$         if (customPayloadPacket.payload() instanceof CGPacketPayload) {
    //$$             return true;
    //$$         }
    //$$     }
    //$$     return original;
    //$$ }
    //#else if MC>=1.20.5
    //$$ @WrapOperation(
    //$$     method = "send",
    //$$     at = @At(value = "INVOKE", target = "Lnet/neoforged/neoforge/network/registration/NetworkRegistry;checkPacket(Lnet/minecraft/network/protocol/Packet;Lnet/minecraft/network/protocol/common/ClientCommonPacketListener;)V")
    //$$ )
    //$$ private void validateCobbleGen(Packet<?> packet, ClientCommonPacketListener listener, Operation<Void> original) {
    //$$     try {
    //$$         original.call(packet, listener);
    //$$     } catch (UnsupportedOperationException e) {
    //$$         if (!(packet instanceof ServerboundCustomPayloadPacket customPayloadPacket)) {
    //$$             throw e;
    //$$         }

    //$$         if (!(customPayloadPacket.payload() instanceof CGPacketPayload)) {
    //$$             throw e;
    //$$         }
    //$$     }
    //$$ }
    //#endif
    //#endif
}
