package io.github.null2264.cobblegen.network.payload;

//#if MC>=1.20.5
//$$ import net.minecraft.network.codec.ByteBufCodecs;
//$$ import net.minecraft.network.codec.StreamCodec;
//$$ import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
//#endif

import io.github.null2264.cobblegen.data.CGIdentifier;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

import static io.github.null2264.cobblegen.util.Constants.CG_SYNC;

//#if MC<=1.16.5
//$$ public class CGSyncC2SPayload
//#else
public record CGSyncC2SPayload(Boolean sync)
//#endif
        implements CGPacketPayload
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

    //#if MC>=1.20.5
    //$$ public static final StreamCodec<FriendlyByteBuf, CGSyncC2SPayload> STREAM_CODEC =
    //$$     StreamCodec.composite(ByteBufCodecs.BOOL, CGSyncC2SPayload::sync, CGSyncC2SPayload::new);
    //$$ public static final CustomPacketPayload.Type<CGSyncC2SPayload> TYPE = new CustomPacketPayload.Type<>(ID.toMC());
    //$$
    //$$ @Override
    //$$ public Type<? extends CGPacketPayload> type() {
    //$$     return TYPE;
    //$$ }
    //#endif
}