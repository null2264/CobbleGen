package io.github.null2264.cobblegen.mixin.network;

import io.github.null2264.cobblegen.network.CGServerPlayNetworkHandler;
import io.github.null2264.cobblegen.util.Util;
import lombok.val;
import net.fabricmc.api.Environment;
import net.minecraft.network.protocol.game.ServerboundCustomPayloadPacket;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static net.fabricmc.api.EnvType.SERVER;

@Environment(SERVER)
@Mixin(ServerGamePacketListenerImpl.class)
public abstract class ServerPacketListenerMixin
{
    @SuppressWarnings("DataFlowIssue")
    private ServerGamePacketListenerImpl getListener() {
        return (ServerGamePacketListenerImpl) (Object) this;
    }

    @Inject(method = "<init>", at = @At("RETURN"))
    private void init(CallbackInfo ci) {
        val self = getListener();
        if (Util.isPortingLibLoaded()) {
            // Just in case
            if (self.connection instanceof io.github.fabricators_of_create.porting_lib.fake_players.FakeConnection) return;
        }
        CGServerPlayNetworkHandler.trySync(self);
    }

    @Inject(method = "handleCustomPayload", at = @At("HEAD"), cancellable = true)
    private void handleCustomPayload(ServerboundCustomPayloadPacket packet, CallbackInfo ci) {
        if (CGServerPlayNetworkHandler.handlePacket(getListener(), packet)) {
            ci.cancel();
        }
    }
}