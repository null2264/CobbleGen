package io.github.null2264.cobblegen.network.payload;

#if MC>=12005
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
#endif

import io.github.null2264.cobblegen.data.CGIdentifier;
import net.minecraft.network.FriendlyByteBuf;
import org.jetbrains.annotations.NotNull;

import static io.github.null2264.cobblegen.util.Constants.CG_SYNC;

#if MC<=11605
public class CGSyncC2SPayload
#else
public record CGSyncC2SPayload(Boolean sync)
#endif
        implements CGPacketPayload
{
    public static final CGIdentifier ID = CG_SYNC;

    #if MC<=11605
    private final Boolean sync;

    public CGSyncC2SPayload(Boolean sync) {
        this.sync = sync;
    }

    public Boolean sync() {
        return sync;
    }
    #endif

    public CGSyncC2SPayload(FriendlyByteBuf buf) {
        this(buf.readBoolean());
    }

    @Override
    public void write(FriendlyByteBuf buf) {
        buf.writeBoolean(sync);
    }

    @Override
    public CGIdentifier id() {
        return CG_SYNC;
    }

    #if MC>=12005
    public static @NotNull StreamCodec<FriendlyByteBuf, CGSyncC2SPayload> codec() {
        return CustomPacketPayload.codec(CGSyncC2SPayload::write, CGSyncC2SPayload::new);
    }

    @Override
    public @NotNull Type<? extends CGPacketPayload> type() {
        return new CustomPacketPayload.Type<>(id().toMC());
    }
    #endif
}
