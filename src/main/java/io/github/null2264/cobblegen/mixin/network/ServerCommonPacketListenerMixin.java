package io.github.null2264.cobblegen.mixin.network;

//#if MC<1.20.2
import net.minecraft.network.protocol.game.ServerboundCustomPayloadPacket;
//#else
//#endif
import io.github.null2264.cobblegen.network.CGServerPlayNetworkHandler;
import io.github.null2264.cobblegen.util.Util;
import lombok.val;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ServerboundCustomPayloadPacket;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

//#if MC<1.20.2
@Mixin(net.minecraft.server.network.ServerGamePacketListenerImpl.class)
//#else
//$$ @Mixin(net.minecraft.server.network.ServerCommonPacketListenerImpl.class)
//#endif
public abstract class ServerCommonPacketListenerMixin
{
    @Shadow
    @Final
    private Connection connection;

    @SuppressWarnings("DataFlowIssue")
    //#if MC<1.20.2
    private net.minecraft.server.network.ServerGamePacketListenerImpl getListener() {
        return (net.minecraft.server.network.ServerGamePacketListenerImpl) (Object) this;
    //#else
    //$$ private net.minecraft.server.network.ServerCommonPacketListenerImpl getListener() {
    //$$     return (net.minecraft.server.network.ServerCommonPacketListenerImpl) (Object) this;
    //#endif
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
        //#if MC<1.20.2
        if (CGServerPlayNetworkHandler.handlePacket(getListener(), packet)) {
        //#else
        //$$ if (CGServerPlayNetworkHandler.handlePacket(getListener(), packet.payload())) {
        //#endif
            ci.cancel();
        }
    }
}
