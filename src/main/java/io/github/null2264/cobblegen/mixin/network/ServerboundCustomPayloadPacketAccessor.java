package io.github.null2264.cobblegen.mixin.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
//#if MC<1.20.2
import net.minecraft.network.protocol.game.ServerboundCustomPayloadPacket;
import org.spongepowered.asm.mixin.gen.Accessor;
//#else
//$$ import net.minecraft.network.protocol.common.ServerboundCustomPayloadPacket;
//#endif

@Mixin(ServerboundCustomPayloadPacket.class)
public interface ServerboundCustomPayloadPacketAccessor {
    @Accessor("identifier")
    ResourceLocation getResourceLocation();

    @Accessor("data")
    FriendlyByteBuf getByte();
}