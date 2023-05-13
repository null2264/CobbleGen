package io.github.null2264.cobblegen.network;

import io.netty.buffer.Unpooled;
import lombok.val;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.c2s.play.CustomPayloadC2SPacket;
import net.minecraft.network.packet.s2c.play.CustomPayloadS2CPacket;
import net.minecraft.server.network.ServerPlayNetworkHandler;

import static io.github.null2264.cobblegen.CobbleGen.*;

public class CGServerPlayNetworkHandler
{
    private final ServerPlayNetworkHandler handler;

    public CGServerPlayNetworkHandler(ServerPlayNetworkHandler handler) {
        this.handler = handler;
    }

    public void sync() {
        LOGGER.info("A player joined, sending config...");
        val buf = new PacketByteBuf(Unpooled.buffer());
        FLUID_INTERACTION.writeGeneratorsToPacket(buf);
        handler.sendPacket(new CustomPayloadS2CPacket(SYNC_CHANNEL, buf));
    }

    public boolean handlePacket(CustomPayloadC2SPacket packet) {
        if (!packet.getChannel().equals(SYNC_CHANNEL)) return false;
        val received = packet.getData().readBoolean();
        if (received)
            LOGGER.info("A client has received the server's CobbleGen config");
        return true;
    }
}