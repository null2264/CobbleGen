package io.github.null2264.cobblegen.network;

import io.github.null2264.cobblegen.util.CGLog;
import io.github.null2264.cobblegen.util.Util;
import io.netty.buffer.Unpooled;
import lombok.val;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.game.ClientboundCustomPayloadPacket;
import net.minecraft.resources.ResourceLocation;

import static io.github.null2264.cobblegen.CobbleGen.*;

public class CGClientPlayNetworkHandler
{
    public static boolean handlePacket(ClientPacketListener listener, ClientboundCustomPayloadPacket packet) {
        if (packet.getIdentifier().equals(SYNC_CHANNEL)) {
            val packetData = packet.getData();
            val isReload = packetData.readBoolean();
            FLUID_INTERACTION.readGeneratorsFromPacket(packetData);

            val isSync = FLUID_INTERACTION.isSync();
            if (isSync)
                CGLog.info("CobbleGen config has been", isReload ? "re-synced" : "retrieved from the server");
            val buf = new FriendlyByteBuf(Unpooled.buffer());
            buf.writeBoolean(isSync);
            listener.send(createC2SPacket(Channel.SYNC, buf));
            return true;
        } if (packet.getIdentifier().equals(SYNC_PING_CHANNEL)) {
            val buf = new FriendlyByteBuf(Unpooled.buffer());
            buf.writeBoolean(packet.getData().readBoolean());
            buf.writeBoolean(Util.isAnyRecipeViewerLoaded());  // Reply "yes I need those data"
            listener.send(createC2SPacket(Channel.PING, buf));
            return true;
        }
        return false;
    }

    public static void onDisconnect() {
        FLUID_INTERACTION.disconnect();
    }

    private static ClientboundCustomPayloadPacket createC2SPacket(Channel channel, FriendlyByteBuf buf) {
        ResourceLocation channelId;
        switch (channel) {
            case PING -> channelId = SYNC_PING_CHANNEL;
            default -> channelId = SYNC_CHANNEL;
        }
        return new ClientboundCustomPayloadPacket(channelId, buf);
    }
}