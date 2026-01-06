#if FORGE && FORGE>1 && MC>=12004
package io.github.null2264.cobblegen.mixin.network.loader.neoforge;

import io.github.null2264.cobblegen.network.payload.CGPacketPayload;
import net.minecraft.network.protocol.Packet;
import net.neoforged.neoforge.network.registration.NetworkRegistry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;

@Mixin(NetworkRegistry.class)
public abstract class NetworkRegistryMixin {
    @Inject(
        #if MC<12005
        method = "Lnet/neoforged/neoforge/network/registration/NetworkRegistry;canSendPacket(Lnet/minecraft/network/protocol/Packet;Lnet/minecraft/network/protocol/common/ServerCommonPacketListener;)Z",
        #else
        method = "Lnet/neoforged/neoforge/network/registration/NetworkRegistry;checkPacket(Lnet/minecraft/network/protocol/Packet;Lnet/minecraft/network/protocol/common/ServerCommonPacketListener;)V",
        #endif
        at = @At("HEAD"),
        cancellable = true
    )
    public static void validateCobbleGenC2S(
        net.minecraft.network.protocol.Packet<?> packet,
        net.minecraft.network.protocol.common.ServerCommonPacketListener listener,
        #if MC<12005
        org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable cir
        #else
        org.spongepowered.asm.mixin.injection.callback.CallbackInfo ci
        #endif
    ) {
        if (packet instanceof net.minecraft.network.protocol.common.ClientboundCustomPayloadPacket customPayloadPacket) {
            if (customPayloadPacket.payload() instanceof CGPacketPayload) {
                #if MC<12005
                cir.setReturnValue(true)
                #else
                ci.cancel();
                #endif
            }
        }
    }

    @Inject(
        #if MC<12005
        method = "Lnet/neoforged/neoforge/network/registration/NetworkRegistry;canSendPacket(Lnet/minecraft/network/protocol/Packet;Lnet/minecraft/network/protocol/common/ClientCommonPacketListener;)Z",
        #else
        method = "Lnet/neoforged/neoforge/network/registration/NetworkRegistry;checkPacket(Lnet/minecraft/network/protocol/Packet;Lnet/minecraft/network/protocol/common/ClientCommonPacketListener;)V",
        #endif
        at = @At("HEAD"),
        cancellable = true
    )
    public static void validateCobbleGenS2C(
        net.minecraft.network.protocol.Packet<?> packet,
        net.minecraft.network.protocol.common.ClientCommonPacketListener listener,
        #if MC<12005
        org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable cir
        #else
        org.spongepowered.asm.mixin.injection.callback.CallbackInfo ci
        #endif
    ) {
        if (packet instanceof net.minecraft.network.protocol.common.ServerboundCustomPayloadPacket customPayloadPacket) {
            if (customPayloadPacket.payload() instanceof CGPacketPayload) {
                #if MC<12005
                cir.setReturnValue(true);
                #else
                ci.cancel();
                #endif
            }
        }
    }
}
#endif
