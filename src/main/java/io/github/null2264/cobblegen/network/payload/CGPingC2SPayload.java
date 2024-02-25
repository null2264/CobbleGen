package io.github.null2264.cobblegen.network.payload;

import io.github.null2264.cobblegen.data.CGIdentifier;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

import static io.github.null2264.cobblegen.util.Constants.CG_PING;

//#if MC<=1.16.5
//$$ public class CGPingC2SPayload
//#else
public record CGPingC2SPayload(Boolean reload, Boolean recipeViewer)
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
    //$$ private final Boolean recipeViewer;

    //$$ public CGPingC2SPayload(Boolean reload, Boolean recipeViewer) {
    //$$     this.reload = reload;
    //$$     this.recipeViewer = recipeViewer;
    //$$ }

    //$$ public Boolean reload() {
    //$$     return reload;
    //$$ }

    //$$ public Boolean recipeViewer() {
    //$$     return recipeViewer;
    //$$ }
    //#endif

    public CGPingC2SPayload(FriendlyByteBuf buf) {
        this(buf.readBoolean(), buf.readBoolean());
    }

    public Boolean hasRecipeViewer() {
        return recipeViewer;
    }

    @Override
    public void write(FriendlyByteBuf buf) {
        buf.writeBoolean(reload);
        buf.writeBoolean(hasRecipeViewer());
    }

    @Override
    public ResourceLocation id() {
        return ID.toMC();
    }
}