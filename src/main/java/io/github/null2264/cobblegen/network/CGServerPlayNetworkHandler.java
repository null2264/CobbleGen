package io.github.null2264.cobblegen.network;

import io.netty.buffer.Unpooled;
import lombok.val;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.c2s.play.CustomPayloadC2SPacket;
import net.minecraft.network.packet.s2c.play.CustomPayloadS2CPacket;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.util.Identifier;

import static io.github.null2264.cobblegen.CobbleGen.*;

public class CGServerPlayNetworkHandler
{
    private final ServerPlayNetworkHandler handler;

    public CGServerPlayNetworkHandler(ServerPlayNetworkHandler handler) {
        this.handler = handler;
    }

    public void trySync() {
        LOGGER.info("A player joined, checking for recipe viewer...");
        val buf = new PacketByteBuf(Unpooled.buffer());
        buf.writeString("ping");  // Basically "do you want this?"
        handler.sendPacket(createS2CPacket(Channel.PING, buf));
    }

    public boolean handlePacket(CustomPayloadC2SPacket packet) {
        if (packet.getChannel().equals(SYNC_CHANNEL)) {
            val received = packet.getData().readBoolean();
            if (received)
                LOGGER.info("A client has received the server's CobbleGen config");
            return true;
        } else if (packet.getChannel().equals(SYNC_PING_CHANNEL)) {
            val status = packet.getData().readBoolean();
            if (status) {
                LOGGER.info("Player has recipe viewer installed, sending CobbleGen config...");
                sync();
            }
            return true;
        }
        return false;
    }

    public void sync() {
        val buf = new PacketByteBuf(Unpooled.buffer());
        FLUID_INTERACTION.writeGeneratorsToPacket(buf);
        handler.sendPacket(createS2CPacket(Channel.SYNC, buf));
    }

    private CustomPayloadS2CPacket createS2CPacket(Channel channel, PacketByteBuf buf) {
        Identifier channelId;
        switch (channel) {
            case PING -> channelId = SYNC_PING_CHANNEL;
            default -> channelId = SYNC_CHANNEL;
        }
        return new CustomPayloadS2CPacket(channelId, buf);
    }
}