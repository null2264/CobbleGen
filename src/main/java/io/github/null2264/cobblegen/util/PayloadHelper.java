package io.github.null2264.cobblegen.util;

import io.netty.buffer.Unpooled;
import net.minecraft.network.FriendlyByteBuf;

public class PayloadHelper {
    public static void write(FriendlyByteBuf byteBuf, FriendlyByteBuf data) {
        byteBuf.writeBytes(data.copy());
    }

    public static FriendlyByteBuf read(FriendlyByteBuf byteBuf) {
        FriendlyByteBuf newBuf = new FriendlyByteBuf(Unpooled.buffer());
        newBuf.writeBytes(byteBuf.copy());
        byteBuf.skipBytes(byteBuf.readableBytes());
        return newBuf;
    }
}