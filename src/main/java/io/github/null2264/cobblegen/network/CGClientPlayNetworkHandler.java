package io.github.null2264.cobblegen.network;

import io.github.null2264.cobblegen.util.Util;
import io.netty.buffer.Unpooled;
import lombok.val;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.c2s.play.CustomPayloadC2SPacket;
import net.minecraft.network.packet.s2c.play.CustomPayloadS2CPacket;
import net.minecraft.util.Identifier;

import static io.github.null2264.cobblegen.CobbleGen.*;

public class CGClientPlayNetworkHandler
{
    private final ClientPlayNetworkHandler handler;

    public CGClientPlayNetworkHandler(ClientPlayNetworkHandler handler) {
        this.handler = handler;
    }

    public boolean handlePacket(CustomPayloadS2CPacket packet) {
        if (packet.getChannel().equals(SYNC_CHANNEL)) {
            FLUID_INTERACTION.readGeneratorsFromPacket(packet.getData());

            val isSync = FLUID_INTERACTION.isSync();
            if (isSync)
                LOGGER.info("CobbleGen config has been retrieved from the server");
            val buf = new PacketByteBuf(Unpooled.buffer());
            buf.writeBoolean(isSync);
            handler.sendPacket(createC2SPacket(Channel.SYNC, buf));
            return true;
        } if (packet.getChannel().equals(SYNC_PING_CHANNEL)) {
            val buf = new PacketByteBuf(Unpooled.buffer());
            buf.writeBoolean(Util.isAnyRecipeViewerLoaded());  // Reply "yes I need those data"
            handler.sendPacket(createC2SPacket(Channel.PING, buf));
            return true;
        }
        return false;
    }

    public void onDisconnect() {
        FLUID_INTERACTION.disconnect();
    }

    private CustomPayloadC2SPacket createC2SPacket(Channel channel, PacketByteBuf buf) {
        Identifier channelId;
        switch (channel) {
            case PING -> channelId = SYNC_PING_CHANNEL;
            default -> channelId = SYNC_CHANNEL;
        }
        return new CustomPayloadC2SPacket(channelId, buf);
    }
}