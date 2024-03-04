package io.github.null2264.cobblegen.network.payload;

//#if MC>=1.20.5
//$$ import com.google.common.collect.Lists;
//$$ import com.google.common.collect.Maps;
//$$ import io.github.null2264.cobblegen.util.Constants;
//$$ import net.minecraft.network.codec.ByteBufCodecs;
//$$ import net.minecraft.network.codec.StreamCodec;
//$$ import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
//#endif

import io.github.null2264.cobblegen.FluidInteraction;
import io.github.null2264.cobblegen.data.CGIdentifier;
import io.github.null2264.cobblegen.data.model.Generator;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.material.Fluid;

import java.util.List;
import java.util.Map;

import static io.github.null2264.cobblegen.util.Constants.CG_SYNC_SERVER;

//#if MC<=1.16.5
//$$ public class CGSyncS2CPayload
//#else
public record CGSyncS2CPayload(Boolean isReload, Map<Fluid, List<Generator>> recipe)
//#endif
        implements CGPacketPayload
{
    public static final CGIdentifier ID = CG_SYNC_SERVER;

    //#if MC<=1.16.5
    //$$ private final Boolean isReload;
    //$$ private final Map<Fluid, List<Generator>> recipe;

    //$$ public CGSyncS2CPayload(Boolean isReload, Map<Fluid, List<Generator>> recipe) {
    //$$     this.isReload = isReload;
    //$$     this.recipe = recipe;
    //$$ }

    //$$ public Boolean isReload() {
    //$$     return isReload;
    //$$ }

    //$$ public Map<Fluid, List<Generator>> recipe() {
    //$$     return recipe;
    //$$ }
    //#endif

    public CGSyncS2CPayload(FriendlyByteBuf buf) {
        this(buf.readBoolean(), FluidInteraction.read(buf));
    }

    @Override
    public void write(FriendlyByteBuf buf) {
        buf.writeBoolean(isReload());
        FluidInteraction.write(recipe(), buf);
    }

    @Override
    public ResourceLocation id() {
        return ID.toMC();
    }

    //#if MC>=1.20.5
    //$$ public static final StreamCodec<FriendlyByteBuf, CGSyncS2CPayload> STREAM_CODEC =
    //$$     CustomPacketPayload.codec(CGSyncS2CPayload::write, CGSyncS2CPayload::new);
    //$$ public static final CustomPacketPayload.Type<CGSyncS2CPayload> TYPE = new CustomPacketPayload.Type<>(ID.toMC());
    //$$
    //$$ @Override
    //$$ public Type<? extends CGPacketPayload> type() {
    //$$     return TYPE;
    //$$ }
    //#endif
}