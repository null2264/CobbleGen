//#if MC<1.20.2
package io.github.null2264.cobblegen.network.payload;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

public interface CGPacketPayload {
    void write(FriendlyByteBuf buf);

    ResourceLocation id();
}
//#endif