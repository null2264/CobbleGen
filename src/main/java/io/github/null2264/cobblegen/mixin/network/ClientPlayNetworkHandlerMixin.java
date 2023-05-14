package io.github.null2264.cobblegen.mixin.network;

import io.github.null2264.cobblegen.network.CGClientPlayNetworkHandler;
import net.fabricmc.api.Environment;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.packet.s2c.play.CustomPayloadS2CPacket;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static net.fabricmc.api.EnvType.CLIENT;

@Environment(CLIENT)
@Mixin(ClientPlayNetworkHandler.class)
public abstract class ClientPlayNetworkHandlerMixin
{
    @SuppressWarnings("DataFlowIssue")
    private ClientPlayNetworkHandler getHandler() {
        return (ClientPlayNetworkHandler) (Object) this;
    }

    @Inject(method = "onCustomPayload", at = @At("HEAD"), cancellable = true)
    private void handleCustomPayload(CustomPayloadS2CPacket packet, CallbackInfo ci) {
        if (CGClientPlayNetworkHandler.handlePacket(getHandler(), packet)) {
            ci.cancel();
        }
    }

    @Inject(method = "onDisconnected", at = @At("HEAD"))
    private void handleDisconnect(Text reason, CallbackInfo ci) {
        CGClientPlayNetworkHandler.onDisconnect();
    }
}