package io.github.null2264.cobblegen.mixin.network;

import com.mojang.authlib.GameProfile;
import io.github.null2264.cobblegen.network.CGClientPlayNetworkHandler;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.util.telemetry.TelemetrySender;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.packet.s2c.play.CustomPayloadS2CPacket;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayNetworkHandler.class)
public abstract class ClientPlayNetworkHandlerMixin
{
    @Unique
    private CGClientPlayNetworkHandler handlerCG;

    @Inject(method = "<init>", at = @At("RETURN"))
    private void init(
            MinecraftClient client,
            Screen screen,
            ClientConnection connection,
            GameProfile profile,
            TelemetrySender telemetrySender,
            CallbackInfo ci
    ) {
        //noinspection DataFlowIssue
        this.handlerCG = new CGClientPlayNetworkHandler((ClientPlayNetworkHandler) (Object) this);
    }

    @Inject(method = "onCustomPayload", at = @At("HEAD"), cancellable = true)
    private void handleCustomPayload(CustomPayloadS2CPacket packet, CallbackInfo ci) {
        if (handlerCG.handlePacket(packet)) {
            ci.cancel();
        }
    }

    @Inject(method = "onDisconnected", at = @At("HEAD"))
    private void handleDisconnect(Text reason, CallbackInfo ci) {
        handlerCG.onDisconnect();
    }
}