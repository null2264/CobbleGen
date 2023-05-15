package io.github.null2264.cobblegen.data.model;

import net.minecraft.network.PacketByteBuf;

public interface PacketSerializable<T>
{
    void toPacket(PacketByteBuf buf);
}