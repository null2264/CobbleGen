package io.github.null2264.cobblegen.data.model;

import io.github.null2264.cobblegen.compat.ByteBufCompat;

public interface PacketSerializable<T>
{
    void toPacket(ByteBufCompat buf);
}