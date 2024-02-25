package io.github.null2264.cobblegen.network.payload;

import io.github.null2264.cobblegen.data.CGIdentifier;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

import static io.github.null2264.cobblegen.util.Constants.CG_PING;

//#if MC<=1.16.5
//$$ public class CGPingS2CPayload
//#else
public record CGPingS2CPayload(Boolean reload)
//#endif
        //#if MC<1.20.2
        implements CGPacketPayload
        //#else
        //$$ implements net.minecraft.network.protocol.common.custom.CustomPacketPayload
        //#endif
{
    public static final CGIdentifier ID = CG_PING;

    //#if MC<=1.16.5
    //$$ private final Boolean reload;

    //$$ public CGPingS2CPayload(Boolean reload) {
    //$$     this.reload = reload;
    //$$ }

    //$$ public Boolean reload() {
    //$$     return reload;
    //$$ }
    //#endif

    public CGPingS2CPayload(FriendlyByteBuf buf) {
        this(buf.readBoolean());
    }

    @Override
    public void write(FriendlyByteBuf buf) {
        buf.writeBoolean(reload);
    }

    @Override
    public ResourceLocation id() {
        return ID.toMC();
    }
}