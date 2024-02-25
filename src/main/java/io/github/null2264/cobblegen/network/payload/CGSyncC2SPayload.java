package io.github.null2264.cobblegen.network.payload;

import io.github.null2264.cobblegen.data.CGIdentifier;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

import static io.github.null2264.cobblegen.util.Constants.CG_SYNC;

//#if MC<=1.16.5
//$$ public class CGSyncC2SPayload
//#else
public record CGSyncC2SPayload(Boolean sync)
//#endif
        //#if MC<1.20.2
        implements CGPacketPayload
        //#else
        //$$ implements net.minecraft.network.protocol.common.custom.CustomPacketPayload
        //#endif
{
    public static final CGIdentifier ID = CG_SYNC;

    //#if MC<=1.16.5
    //$$ private final Boolean sync;

    //$$ public CGSyncC2SPayload(Boolean sync) {
    //$$     this.sync = sync;
    //$$ }

    //$$ public Boolean sync() {
    //$$     return sync;
    //$$ }
    //#endif

    public CGSyncC2SPayload(FriendlyByteBuf buf) {
        this(buf.readBoolean());
    }

    @Override
    public void write(FriendlyByteBuf buf) {
        buf.writeBoolean(sync);
    }

    @Override
    public ResourceLocation id() {
        return ID.toMC();
    }
}