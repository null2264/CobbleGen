package io.github.null2264.cobblegen.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

/**
 * Replacement for FabricAPI's PacketByteBufPayload
 */
public record PacketByteBufPayload(ResourceLocation id, FriendlyByteBuf data)
    //#if MC>=1.20.2
    //$$ implements net.minecraft.network.protocol.common.custom.CustomPacketPayload
    //#endif
{
    //#if MC>=1.20.2
    //$$ @Override
    //#endif
    public void write(FriendlyByteBuf buf) {
        buf.writeBytes(data.copy());
    }
}