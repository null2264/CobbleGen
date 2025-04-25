package io.github.null2264.cobblegen.network.payload;

#if MC>=12005
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
#endif

import io.github.null2264.cobblegen.data.CGIdentifier;
import net.minecraft.network.FriendlyByteBuf;
import org.jetbrains.annotations.NotNull;

import static io.github.null2264.cobblegen.util.Constants.CG_PING;

#if MC<=11605
public class CGPingC2SPayload
#else
public record CGPingC2SPayload(Boolean reload, Boolean recipeViewer)
#endif
        implements CGPacketPayload
{

    #if MC<=11605
    private final Boolean reload;
    private final Boolean recipeViewer;

    public CGPingC2SPayload(Boolean reload, Boolean recipeViewer) {
        this.reload = reload;
        this.recipeViewer = recipeViewer;
    }

    public Boolean reload() {
        return reload;
    }

    public Boolean recipeViewer() {
        return recipeViewer;
    }
    #endif

    public CGPingC2SPayload(FriendlyByteBuf buf) {
        this(buf.readBoolean(), buf.readBoolean());
    }

    public Boolean hasRecipeViewer() {
        return recipeViewer;
    }

    @Override
    public void write(@NotNull FriendlyByteBuf buf) {
        buf.writeBoolean(reload);
        buf.writeBoolean(hasRecipeViewer());
    }

    @Override
    public @NotNull CGIdentifier cgId() {
        return CG_PING;
    }

    #if MC>=12005
    public static @NotNull StreamCodec<FriendlyByteBuf, CGPingC2SPayload> codec() {
        return CustomPacketPayload.codec(CGPingC2SPayload::write, CGPingC2SPayload::new);
    }

    @Override
    public @NotNull Type<? extends CGPacketPayload> type() {
        return new CustomPacketPayload.Type<>(id());
    }
    #endif
}
