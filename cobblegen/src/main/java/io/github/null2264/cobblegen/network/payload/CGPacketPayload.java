package io.github.null2264.cobblegen.network.payload;

import io.github.null2264.cobblegen.data.CGIdentifier;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

public interface CGPacketPayload
    #if MC>=12002
    extends net.minecraft.network.protocol.common.custom.CustomPacketPayload
    #endif
{
    void write(FriendlyByteBuf buf);

    CGIdentifier id();
}
