package io.github.null2264.cobblegen.data.model;

import io.github.null2264.cobblegen.compat.ByteBufCompat;
import net.minecraft.network.FriendlyByteBuf;

public interface PacketSerializable<T>
{
    default void toPacket(ByteBufCompat buf) {
        toPacket((FriendlyByteBuf) buf);
    }

    default void toPacket(FriendlyByteBuf buf) {
        throw new UnsupportedOperationException();
    }
}