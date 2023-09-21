package io.github.null2264.cobblegen.network;

import io.github.null2264.cobblegen.util.CGLog;
import io.github.null2264.cobblegen.util.Util;
import io.netty.buffer.Unpooled;
import lombok.val;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.FriendlyByteBuf;
//#if MC<1.20.2
import net.minecraft.network.protocol.game.ClientboundCustomPayloadPacket;
import net.minecraft.network.protocol.game.ServerboundCustomPayloadPacket;
//#else
//$$ import net.minecraft.network.protocol.common.ClientboundCustomPayloadPacket;
//$$ import net.minecraft.network.protocol.common.ServerboundCustomPayloadPacket;
//#endif
import net.minecraft.resources.ResourceLocation;

import static io.github.null2264.cobblegen.CobbleGen.*;

public class CGClientPlayNetworkHandler
{
    //#if MC<=1.20.1
    public static boolean handlePacket(ClientPacketListener listener, ClientboundCustomPayloadPacket packet) {
        if (packet.getIdentifier().equals(SYNC_CHANNEL)) {
            val packetData = packet.getData();
            val isReload = packetData.readBoolean();
            FLUID_INTERACTION.readGeneratorsFromPacket(packetData);

            val isSync = FLUID_INTERACTION.isSync();
            if (isSync)
                CGLog.info("CobbleGen config has been", isReload ? "re-synced" : "retrieved from the server");
            val buf = new FriendlyByteBuf(Unpooled.buffer());
            buf.writeResourceLocation(keyFromChannel(Channel.SYNC));
            buf.writeBoolean(isSync);
            listener.send(createC2SPacket(buf));
            return true;
        } if (packet.getIdentifier().equals(SYNC_PING_CHANNEL)) {
            val buf = new FriendlyByteBuf(Unpooled.buffer());
            buf.writeResourceLocation(keyFromChannel(Channel.PING));
            buf.writeBoolean(packet.getData().readBoolean());
            buf.writeBoolean(Util.isAnyRecipeViewerLoaded());  // Reply "yes I need those data"
            listener.send(createC2SPacket(buf));
            return true;
        }
        return false;
    }

    public static void onDisconnect() {
        FLUID_INTERACTION.disconnect();
    }
    //#endif

    private static ResourceLocation keyFromChannel(Channel channel) {
        switch (channel) {
            case PING -> {
                return SYNC_PING_CHANNEL;
            }
            default -> {
                return SYNC_CHANNEL;
            }
        }
    }

    private static ServerboundCustomPayloadPacket createC2SPacket(FriendlyByteBuf buf) {
        //#if MC<=1.20.1
        return new ServerboundCustomPayloadPacket(buf.readResourceLocation(), buf);
        //#else
        //$$ return new ServerboundCustomPayloadPacket(buf);
        //#endif
    }
}