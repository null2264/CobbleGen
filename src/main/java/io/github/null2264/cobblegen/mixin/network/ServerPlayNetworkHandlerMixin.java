package io.github.null2264.cobblegen.mixin.network;

import io.github.null2264.cobblegen.network.CGServerPlayNetworkHandler;
import io.github.null2264.cobblegen.util.Util;
import lombok.val;
import net.fabricmc.api.Environment;
import net.minecraft.network.packet.c2s.play.CustomPayloadC2SPacket;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static net.fabricmc.api.EnvType.SERVER;

@Environment(SERVER)
@Mixin(ServerPlayNetworkHandler.class)
public abstract class ServerPlayNetworkHandlerMixin
{
    @SuppressWarnings("DataFlowIssue")
    private ServerPlayNetworkHandler getHandler() {
        return (ServerPlayNetworkHandler) (Object) this;
    }

    @Inject(method = "<init>", at = @At("RETURN"))
    private void init(CallbackInfo ci) {
        val self = getHandler();
        if (Util.isPortingLibLoaded()) {
            // Just in case
            if (self.connection instanceof io.github.fabricators_of_create.porting_lib.fake_players.FakeConnection) return;
        }
        CGServerPlayNetworkHandler.trySync(self);
    }

    @Inject(method = "onCustomPayload", at = @At("HEAD"), cancellable = true)
    private void handleCustomPayload(CustomPayloadC2SPacket packet, CallbackInfo ci) {
        if (CGServerPlayNetworkHandler.handlePacket(getHandler(), packet)) {
            ci.cancel();
        }
    }
}