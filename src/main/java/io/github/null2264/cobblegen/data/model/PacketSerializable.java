package io.github.null2264.cobblegen.data.model;

import net.minecraft.network.FriendlyByteBuf;

public interface PacketSerializable<T>
{
    void toPacket(FriendlyByteBuf buf);
}