package io.github.null2264.cobblegen.network.payload;

#if MC>=12005
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
#endif

import io.github.null2264.cobblegen.data.CGIdentifier;
import net.minecraft.network.FriendlyByteBuf;
import org.jetbrains.annotations.NotNull;

import static io.github.null2264.cobblegen.util.Constants.CG_PING_SERVER;

#if MC<=11605
public class CGPingS2CPayload
#else
public record CGPingS2CPayload(Boolean reload)
#endif
        implements CGPacketPayload
{
    public static final CGIdentifier ID = CG_PING_SERVER;

    #if MC<=11605
    private final Boolean reload;

    public CGPingS2CPayload(Boolean reload) {
        this.reload = reload;
    }

    public Boolean reload() {
        return reload;
    }
    #endif

    public CGPingS2CPayload(FriendlyByteBuf buf) {
        this(buf.readBoolean());
    }

    @Override
    public void write(@NotNull FriendlyByteBuf buf) {
        buf.writeBoolean(reload);
    }

    @Override
    public @NotNull CGIdentifier cgId() {
        return CG_PING_SERVER;
    }

    #if MC>=12005
    public static @NotNull StreamCodec<FriendlyByteBuf, CGPingS2CPayload> codec() {
        return CustomPacketPayload.codec(CGPingS2CPayload::write, CGPingS2CPayload::new);
    }

    @Override
    public @NotNull Type<? extends CGPacketPayload> type() {
        return new CustomPacketPayload.Type<>(id());
    }
    #endif
}
