package io.github.null2264.cobblegen.network;

import io.github.null2264.cobblegen.compat.ByteBufCompat;
import io.github.null2264.cobblegen.util.CGLog;
import io.github.null2264.cobblegen.util.Util;
import io.netty.buffer.Unpooled;
import net.minecraft.network.FriendlyByteBuf;
//#if MC<1.20.2
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.protocol.game.ClientboundCustomPayloadPacket;
import net.minecraft.network.protocol.game.ServerboundCustomPayloadPacket;
//#else
//$$ import net.minecraft.client.multiplayer.ClientCommonPacketListenerImpl;
//$$ import net.minecraft.network.protocol.common.ServerboundCustomPayloadPacket;
//$$ import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
//#endif
import net.minecraft.resources.ResourceLocation;

import java.util.Objects;

import static io.github.null2264.cobblegen.CobbleGen.*;

public class CGClientPlayNetworkHandler
{
    //#if MC<1.20.2
    public static boolean handlePacket(ClientPacketListener listener, ClientboundCustomPayloadPacket packet) {
    //#else
    //$$ @SuppressWarnings("UnstableApiUsage")
    //$$ public static boolean handlePacket(ClientCommonPacketListenerImpl listener, CustomPacketPayload packet) {
    //#endif
        // FIXME: Enable REI integration
        //#if MC<=1.16.5
        //$$ return false;
        //#else

        //#if MC>=1.20.2
        //$$ if (!(packet instanceof PacketByteBufPayload)) return false;
        //$$ ResourceLocation id = ((PacketByteBufPayload) packet).id();
        //#else
        ResourceLocation id = packet.getIdentifier();
        //#endif

        if (id.equals(SYNC_CHANNEL)) {
            final FriendlyByteBuf packetData =
                    //#if MC<1.20.2
                    packet.getData();
                    //#else
                    //$$ ((PacketByteBufPayload) packet).data();
                    //#endif

            boolean isReload = packetData.readBoolean();
            FLUID_INTERACTION.readGeneratorsFromPacket((ByteBufCompat) packetData);

            boolean isSync = FLUID_INTERACTION.isSync();
            if (isSync)
                CGLog.info("CobbleGen config has been", isReload ? "re-synced" : "retrieved from the server");
            final FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
            buf.writeResourceLocation(keyFromChannel(Channel.SYNC));
            buf.writeBoolean(isSync);
            listener.send(createC2SPacket(buf));
            return true;
        } if (id.equals(SYNC_PING_CHANNEL)) {
            final FriendlyByteBuf packetData =
                    //#if MC<1.20.2
                    packet.getData();
                    //#else
                    //$$ ((PacketByteBufPayload) packet).data();
                    //#endif

            final FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
            buf.writeResourceLocation(keyFromChannel(Channel.PING));
            buf.writeBoolean(packetData.readBoolean());
            buf.writeBoolean(Util.isAnyRecipeViewerLoaded());  // Reply "yes I need those data"
            listener.send(createC2SPacket(buf));
            return true;
        }
        return false;
        //#endif
    }

    public static void onDisconnect() {
        FLUID_INTERACTION.disconnect();
    }

    private static ResourceLocation keyFromChannel(Channel channel) {
        if (Objects.requireNonNull(channel) == Channel.PING) {
            return SYNC_PING_CHANNEL;
        }
        return SYNC_CHANNEL;
    }

    private static ServerboundCustomPayloadPacket createC2SPacket(FriendlyByteBuf buf) {
        //#if MC<=1.20.1
        return new ServerboundCustomPayloadPacket(buf.readResourceLocation(), buf);
        //#else
        //$$ return new ServerboundCustomPayloadPacket(buf);
        //#endif
    }
}