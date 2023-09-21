package io.github.null2264.cobblegen.mixin.network;

import io.github.null2264.cobblegen.network.CGServerPlayNetworkHandler;
import io.github.null2264.cobblegen.util.Util;
import lombok.val;
import net.minecraft.network.Connection;
//#if MC<1.20.2
import net.minecraft.network.protocol.game.ServerboundCustomPayloadPacket;
//#else
//$$ import net.minecraft.network.protocol.common.ServerboundCustomPayloadPacket;
//#endif
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerGamePacketListenerImpl.class)
public abstract class ServerPacketListenerMixin
{
    //#if MC<=1.20.1
    @Shadow
    @Final
    private Connection connection;

    @SuppressWarnings("DataFlowIssue")
    private ServerGamePacketListenerImpl getListener() {
        return (ServerGamePacketListenerImpl) (Object) this;
    }

    @Inject(method = "<init>", at = @At("RETURN"))
    private void init(CallbackInfo ci) {
        val self = getListener();
        //#if FABRIC>=1
        if (Util.isPortingLibLoaded()) {
            // Just in case
            if (this.connection instanceof io.github.fabricators_of_create.porting_lib.fake_players.FakeConnection) return;
        }
        //#endif
        CGServerPlayNetworkHandler.trySync(self);
    }

    @Inject(method = "handleCustomPayload", at = @At("HEAD"), cancellable = true)
    private void handleCustomPayload(ServerboundCustomPayloadPacket packet, CallbackInfo ci) {
        if (CGServerPlayNetworkHandler.handlePacket(getListener(), packet)) {
            ci.cancel();
        }
    }
    //#endif
}