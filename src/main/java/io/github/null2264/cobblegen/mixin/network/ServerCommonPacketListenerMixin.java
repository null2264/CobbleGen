package io.github.null2264.cobblegen.mixin.network;

//#if MC<1.20.2
import net.minecraft.network.protocol.game.ServerboundCustomPayloadPacket;
//#else
//$$ import net.minecraft.network.protocol.common.ServerboundCustomPayloadPacket;
//#endif
import io.github.null2264.cobblegen.network.CGServerPlayNetworkHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(
        //#if MC<1.20.2
        net.minecraft.server.network.ServerGamePacketListenerImpl.class
        //#else
        //$$ net.minecraft.server.network.ServerCommonPacketListenerImpl.class
        //#endif
)
public abstract class ServerCommonPacketListenerMixin
{
    @Unique
    @SuppressWarnings("DataFlowIssue")
    //#if MC<1.20.2
    private net.minecraft.server.network.ServerGamePacketListenerImpl getListener() {
        return (net.minecraft.server.network.ServerGamePacketListenerImpl) (Object) this;
    //#else
    //$$ private net.minecraft.server.network.ServerCommonPacketListenerImpl getListener() {
    //$$     return (net.minecraft.server.network.ServerCommonPacketListenerImpl) (Object) this;
    //#endif
    }

    //#if MC<1.20.2
    @Inject(method = "<init>", at = @At("RETURN"))
    private void init(CallbackInfo ci) {
        //#if MC<1.20.2
        net.minecraft.server.network.ServerGamePacketListenerImpl self =
        //#else
        //$$ net.minecraft.server.network.ServerCommonPacketListenerImpl self =
        //#endif
                getListener();
        CGServerPlayNetworkHandler.trySync(self);
    }
    //#endif

    @Inject(method = "handleCustomPayload", at = @At("HEAD"), cancellable = true)
    private void handleCustomPayload(ServerboundCustomPayloadPacket packet, CallbackInfo ci) {
        if (CGServerPlayNetworkHandler.handlePacket(
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
}