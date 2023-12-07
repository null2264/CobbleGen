package io.github.null2264.cobblegen.network.payload;

import io.github.null2264.cobblegen.data.CGIdentifier;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

import static io.github.null2264.cobblegen.util.Constants.CG_PING;

public record CGPingS2CPayload(Boolean reload)
        //#if MC<1.20.2
        implements CGPacketPayload
        //#else
        //$$ implements net.minecraft.network.protocol.common.custom.CustomPacketPayload
        //#endif
{
    public static final CGIdentifier ID = CG_PING;

    public CGPingS2CPayload(FriendlyByteBuf buf) {
        this(buf.readBoolean());
    }

    public Boolean isReload() {
        return reload;
    }

    @Override
    public void write(FriendlyByteBuf buf) {
        buf.writeBoolean(isReload());
    }

    @Override
    public ResourceLocation id() {
        return ID.toMC();
    }
}