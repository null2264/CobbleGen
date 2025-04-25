package io.github.null2264.cobblegen.network.payload;

import io.github.null2264.cobblegen.data.CGIdentifier;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public interface CGPacketPayload
    #if MC>=12002
    extends net.minecraft.network.protocol.common.custom.CustomPacketPayload
    #endif
{
    void write(@NotNull FriendlyByteBuf buf);

    CGIdentifier cgId();

    #if MC>=12002 && MC<12006
    @Override
    #endif
    default @NotNull ResourceLocation id() {
        return cgId().toMC();
    }
}
