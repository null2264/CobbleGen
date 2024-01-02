package io.github.null2264.cobblegen.mixin.network;

import org.spongepowered.asm.mixin.Mixin;
//#if MC<1.20.2
import net.minecraft.network.protocol.game.ServerboundCustomPayloadPacket;
//#else
//$$ import net.minecraft.network.FriendlyByteBuf;
//$$ import io.netty.buffer.Unpooled;
//$$ import io.github.null2264.cobblegen.network.PacketByteBufPayload;
//$$ import net.minecraft.network.protocol.common.ServerboundCustomPayloadPacket;
//$$ import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
//$$ import net.minecraft.resources.ResourceLocation;
//$$ import org.spongepowered.asm.mixin.injection.At;
//$$ import org.spongepowered.asm.mixin.injection.Inject;
//$$ import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
//$$
//$$ import static io.github.null2264.cobblegen.CobbleGen.MOD_ID;
//#endif

@Mixin(value = ServerboundCustomPayloadPacket.class, priority = 999)
public abstract class ServerboundCustomPayloadPacketMixin {
    //#if MC>=1.20.2
    //$$ @Inject(method = "readPayload", at = @At("HEAD"), cancellable = true)
    //$$ private static void read(ResourceLocation id, FriendlyByteBuf buf, CallbackInfoReturnable<CustomPacketPayload> cir) {
    //$$     if (!id.getNamespace().equals(MOD_ID))
    //$$         return;
    //$$
    //$$     FriendlyByteBuf newBuf = new FriendlyByteBuf(Unpooled.buffer());
    //$$     newBuf.writeBytes(buf.copy());
    //$$     buf.skipBytes(buf.readableBytes());
    //$$     cir.setReturnValue(new PacketByteBufPayload(id, newBuf));
    //$$ }
    //#endif
}