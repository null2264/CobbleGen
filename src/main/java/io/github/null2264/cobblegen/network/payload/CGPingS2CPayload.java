package io.github.null2264.cobblegen.network.payload;

//#if MC>=1.20.5
//$$ import net.minecraft.network.codec.ByteBufCodecs;
//$$ import net.minecraft.network.codec.StreamCodec;
//$$ import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
//#endif

import io.github.null2264.cobblegen.data.CGIdentifier;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

import static io.github.null2264.cobblegen.util.Constants.CG_PING;

//#if MC<=1.16.5
//$$ public class CGPingS2CPayload
//#else
public record CGPingS2CPayload(Boolean reload)
//#endif
        implements CGPacketPayload
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

    //#if MC>=1.20.5
    //$$ public static final StreamCodec<FriendlyByteBuf, CGPingS2CPayload> STREAM_CODEC =
    //$$     StreamCodec.composite(ByteBufCodecs.BOOL, CGPingS2CPayload::reload, CGPingS2CPayload::new);
    //$$ public static final CustomPacketPayload.Type<CGPingS2CPayload> TYPE = new CustomPacketPayload.Type<>(ID.toMC());
    //$$
    //$$ @Override
    //$$ public Type<? extends CGPacketPayload> type() {
    //$$     return TYPE;
    //$$ }
    //#endif
}