//#if MC>=1.20.2
//$$ package io.github.null2264.cobblegen.mixin.network;
//$$
//$$ import io.github.null2264.cobblegen.network.payload.CGPingS2CPayload;
//$$ import io.netty.buffer.Unpooled;
//$$ import lombok.val;
//$$ import net.minecraft.network.FriendlyByteBuf;
//$$ import net.minecraft.network.protocol.common.ClientboundCustomPayloadPacket;
//$$ import net.minecraft.server.network.ServerConfigurationPacketListenerImpl;
//$$ import org.spongepowered.asm.mixin.Mixin;
//$$ import org.spongepowered.asm.mixin.injection.At;
//$$ import org.spongepowered.asm.mixin.injection.Inject;
//$$ import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
//$$
//$$ import static io.github.null2264.cobblegen.util.Constants.CG_PING;
//$$
//$$ @Mixin(ServerConfigurationPacketListenerImpl.class)
//$$ public abstract class ServerConfigurationPacketListenerMixin {
//$$     @Inject(method = "startConfiguration", at = @At("HEAD"))
//$$     private void syncCG(CallbackInfo ci) {
//$$         val buf = new FriendlyByteBuf(Unpooled.buffer());
//$$         buf.writeResourceLocation(CG_PING.toMC());
//$$         ((ServerConfigurationPacketListenerImpl) (Object) this).send(
//$$             new ClientboundCustomPayloadPacket(new CGPingS2CPayload(false))
//$$         );
//$$     }
//$$ }
//#endif