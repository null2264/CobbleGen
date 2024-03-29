package io.github.null2264.cobblegen.mixin.network.packet;

import org.spongepowered.asm.mixin.Mixin;
//#if MC<1.20.2
import net.minecraft.network.protocol.game.ServerboundCustomPayloadPacket;
//#else
//$$ import net.minecraft.network.protocol.common.ServerboundCustomPayloadPacket;
//#if MC<1.20.5
//$$ import io.github.null2264.cobblegen.data.CGIdentifier;
//$$ import io.github.null2264.cobblegen.network.payload.CGPacketPayload;
//$$ import io.github.null2264.cobblegen.network.payload.CGPayloadReader;
//$$ import io.netty.buffer.Unpooled;
//$$ import net.minecraft.network.FriendlyByteBuf;
//$$ import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
//$$ import net.minecraft.resources.ResourceLocation;
//$$ import org.spongepowered.asm.mixin.injection.At;
//$$ import org.spongepowered.asm.mixin.injection.Inject;
//$$ import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
//$$
//$$ import static io.github.null2264.cobblegen.util.Constants.KNOWN_SERVER_PAYLOADS;
//$$ import static io.github.null2264.cobblegen.CobbleGen.MOD_ID;
//#endif
//#endif

@Mixin(value = ServerboundCustomPayloadPacket.class, priority = 999)
public abstract class ServerboundCustomPayloadPacketMixin {
    //#if MC>=1.20.2 && MC<1.20.5
    //$$ @Inject(method = "readPayload", at = @At("HEAD"), cancellable = true)
    //$$ private static void read(ResourceLocation id, FriendlyByteBuf buf, CallbackInfoReturnable<CustomPacketPayload> cir) {
    //$$     if (!id.getNamespace().equals(MOD_ID)) return;
    //$$
    //$$     CGPayloadReader<? extends CGPacketPayload> reader = KNOWN_SERVER_PAYLOADS.get(CGIdentifier.fromMC(id));
    //$$     if (reader == null) return;
    //$$
    //$$     cir.setReturnValue(reader.apply(buf));
    //$$ }
    //#endif
}