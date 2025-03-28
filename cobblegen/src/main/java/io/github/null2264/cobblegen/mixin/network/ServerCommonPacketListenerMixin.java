package io.github.null2264.cobblegen.mixin.network;

#if MC<12002
import net.minecraft.network.protocol.game.ServerboundCustomPayloadPacket;
#else
#if MC>=12004
import io.github.null2264.cobblegen.network.payload.CGPacketPayload;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.common.ClientboundCustomPayloadPacket;
#if MC<12005
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
#else
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.network.protocol.common.ServerCommonPacketListener;
#endif
#endif
import net.minecraft.network.protocol.common.ServerboundCustomPayloadPacket;
#endif

import io.github.null2264.cobblegen.network.CGServerPlayNetworkHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(
        #if MC<12002
        net.minecraft.server.network.ServerGamePacketListenerImpl.class
        #else
        net.minecraft.server.network.ServerCommonPacketListenerImpl.class
        #endif
)
public abstract class ServerCommonPacketListenerMixin
{
    @Unique
    @SuppressWarnings("DataFlowIssue")
    #if MC<12002
    private net.minecraft.server.network.ServerGamePacketListenerImpl getListener() {
        return (net.minecraft.server.network.ServerGamePacketListenerImpl) (Object) this;
    #else
    private net.minecraft.server.network.ServerCommonPacketListenerImpl getListener() {
        return (net.minecraft.server.network.ServerCommonPacketListenerImpl) (Object) this;
    #endif
    }

    #if MC<12002
    @Inject(method = "<init>", at = @At("RETURN"))
    private void init(CallbackInfo ci) {
        #if MC<12002
        net.minecraft.server.network.ServerGamePacketListenerImpl self =
        #else
        net.minecraft.server.network.ServerCommonPacketListenerImpl self =
        #endif
                getListener();
        CGServerPlayNetworkHandler.trySync(self);
    }
    #endif

    @Inject(method = "handleCustomPayload", at = @At("HEAD"), cancellable = true)
    private void handleCustomPayload(ServerboundCustomPayloadPacket packet, CallbackInfo ci) {
        if (CGServerPlayNetworkHandler.handlePacket(
                getListener(),
                #if MC<1.20.2
                packet
                #else
                packet.payload()
                #endif
        )) {
            ci.cancel();
        }
    }

    #if MC>=12004 && FORGE && FORGE>1
    #if MC<12005
    @ModifyExpressionValue(
        method = "send(Lnet/minecraft/network/protocol/Packet;Lnet/minecraft/network/PacketSendListener;)V",
        at = @At(value = "INVOKE", target = "Lnet/neoforged/neoforge/network/registration/NetworkRegistry;canSendPacket(Lnet/minecraft/network/protocol/Packet;Lnet/minecraft/network/protocol/common/ServerCommonPacketListener;)Z")
    )
    private boolean validateCobbleGen(boolean original, Packet<?> packet) {
        if (packet instanceof ClientboundCustomPayloadPacket customPayloadPacket) {
            if (customPayloadPacket.payload() instanceof CGPacketPayload) {
                return true;
            }
        }
        return original;
    }
    #else
    @WrapOperation(
        method = "send(Lnet/minecraft/network/protocol/Packet;Lnet/minecraft/network/PacketSendListener;)V",
        at = @At(value = "INVOKE", target = "Lnet/neoforged/neoforge/network/registration/NetworkRegistry;checkPacket(Lnet/minecraft/network/protocol/Packet;Lnet/minecraft/network/protocol/common/ServerCommonPacketListener;)V")
    )
    private void validateCobbleGen(Packet<?> packet, ServerCommonPacketListener listener, Operation<Void> original) {
        try {
            original.call(packet, listener);
        } catch (UnsupportedOperationException e) {
            if (!(packet instanceof ClientboundCustomPayloadPacket customPayloadPacket)) {
                throw e;
            }

            if (!(customPayloadPacket.payload() instanceof CGPacketPayload)) {
                throw e;
            }
        }
    }
    #endif
    #endif
}
