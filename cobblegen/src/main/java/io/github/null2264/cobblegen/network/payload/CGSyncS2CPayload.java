package io.github.null2264.cobblegen.network.payload;

#if MC>=12005
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
#endif

import io.github.null2264.cobblegen.FluidInteraction;
import io.github.null2264.cobblegen.data.CGIdentifier;
import io.github.null2264.cobblegen.data.model.Generator;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.material.Fluid;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;

import static io.github.null2264.cobblegen.util.Constants.CG_SYNC_SERVER;

#if MC<=11605
public class CGSyncS2CPayload
#else
public record CGSyncS2CPayload(Boolean isReload, Map<Fluid, List<Generator>> recipe)
#endif
        implements CGPacketPayload
{

    #if MC<=11605
    private final Boolean isReload;
    private final Map<Fluid, List<Generator>> recipe;

    public CGSyncS2CPayload(Boolean isReload, Map<Fluid, List<Generator>> recipe) {
        this.isReload = isReload;
        this.recipe = recipe;
    }

    public Boolean isReload() {
        return isReload;
    }

    public Map<Fluid, List<Generator>> recipe() {
        return recipe;
    }
    #endif

    public CGSyncS2CPayload(FriendlyByteBuf buf) {
        this(buf.readBoolean(), FluidInteraction.read(buf));
    }

    @Override
    public void write(FriendlyByteBuf buf) {
        buf.writeBoolean(isReload());
        FluidInteraction.write(recipe(), buf);
    }

    @Override
    public CGIdentifier id() {
        return CG_SYNC_SERVER;
    }

    #if MC>=12005
    public static @NotNull StreamCodec<FriendlyByteBuf, CGSyncS2CPayload> codec() {
        return CustomPacketPayload.codec(CGSyncS2CPayload::write, CGSyncS2CPayload::new);
    }

    @Override
    public @NotNull Type<? extends CGPacketPayload> type() {
        return new CustomPacketPayload.Type<>(id().toMC());
    }
    #endif
}
