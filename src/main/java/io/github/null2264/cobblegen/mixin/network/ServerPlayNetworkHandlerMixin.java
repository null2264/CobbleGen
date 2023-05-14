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
    @Unique
    private CGServerPlayNetworkHandler handlerCG;

    @Inject(method = "<init>", at = @At("RETURN"))
    private void init(CallbackInfo ci) {
        //noinspection DataFlowIssue
        val self = (ServerPlayNetworkHandler) (Object) this;
        if (Util.isPortingLibLoaded()) {
            // Just in case
            if (self.connection instanceof io.github.fabricators_of_create.porting_lib.fake_players.FakeConnection) return;
        }
        handlerCG = new CGServerPlayNetworkHandler(self);
        handlerCG.trySync();
    }

    @Inject(method = "onCustomPayload", at = @At("HEAD"), cancellable = true)
    private void handleCustomPayload(CustomPayloadC2SPacket packet, CallbackInfo ci) {
        if (handlerCG.handlePacket(packet)) {
            ci.cancel();
        }
    }
}