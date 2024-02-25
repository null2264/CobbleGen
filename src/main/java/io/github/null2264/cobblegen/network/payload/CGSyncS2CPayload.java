package io.github.null2264.cobblegen.network.payload;

import io.github.null2264.cobblegen.FluidInteraction;
import io.github.null2264.cobblegen.data.CGIdentifier;
import io.github.null2264.cobblegen.data.model.Generator;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.material.Fluid;

import java.util.List;
import java.util.Map;

import static io.github.null2264.cobblegen.util.Constants.CG_SYNC;

//#if MC<=1.16.5
//$$ public class CGSyncS2CPayload
//#else
public record CGSyncS2CPayload(Boolean isReload, Map<Fluid, List<Generator>> recipe)
//#endif
        //#if MC<1.20.2
        implements CGPacketPayload
        //#else
        //$$ implements net.minecraft.network.protocol.common.custom.CustomPacketPayload
        //#endif
{
    public static final CGIdentifier ID = CG_SYNC;

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
}