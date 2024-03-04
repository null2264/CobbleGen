//#if MC>=1.20.5
//$$ package io.github.null2264.cobblegen.mixin.network.packet;
//$$
//$$ import io.github.null2264.cobblegen.data.CGIdentifier;
//$$ import net.minecraft.network.FriendlyByteBuf;
//$$ import net.minecraft.network.codec.StreamCodec;
//$$ import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
//$$ import net.minecraft.resources.ResourceLocation;
//$$ import org.spongepowered.asm.mixin.Mixin;
//$$ import org.spongepowered.asm.mixin.injection.At;
//$$ import org.spongepowered.asm.mixin.injection.Inject;
//$$ import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
//$$
//$$ import static io.github.null2264.cobblegen.CobbleGen.MOD_ID;
//$$ import static io.github.null2264.cobblegen.util.Constants.KNOWN_PAYLOADS;
//$$
//$$ @Mixin(targets = "net.minecraft.network.protocol.common.custom.CustomPacketPayload$1")
//$$ public abstract class CustomPacketPayloadMixin
//$$ {
//$$     @Inject(method = "findCodec", at = @At("HEAD"), cancellable = true)
//$$     private void getCodec(
//$$         ResourceLocation id,
//$$         CallbackInfoReturnable<StreamCodec<? super FriendlyByteBuf, ? extends CustomPacketPayload>> cir
//$$     ) {
//$$         if (!(id.getNamespace().equals(MOD_ID))) return;
//$$
//$$         StreamCodec<? super FriendlyByteBuf, ? extends CustomPacketPayload> codec =
//$$             KNOWN_PAYLOADS.get(CGIdentifier.fromMC(id));
//$$         if (codec == null) return;
//$$
//$$         cir.setReturnValue(codec);
//$$     }
//$$ }
//#endif