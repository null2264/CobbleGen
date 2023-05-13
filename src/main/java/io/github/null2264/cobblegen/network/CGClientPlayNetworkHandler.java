package io.github.null2264.cobblegen.network;

import io.netty.buffer.Unpooled;
import lombok.val;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.c2s.play.CustomPayloadC2SPacket;
import net.minecraft.network.packet.s2c.play.CustomPayloadS2CPacket;

import static io.github.null2264.cobblegen.CobbleGen.*;

public class CGClientPlayNetworkHandler
{
    private final ClientPlayNetworkHandler handler;

    public CGClientPlayNetworkHandler(ClientPlayNetworkHandler handler) {
        this.handler = handler;
    }

    public boolean handlePacket(CustomPayloadS2CPacket packet) {
        if (!packet.getChannel().equals(SYNC_CHANNEL)) return false;
        FLUID_INTERACTION.readGeneratorsFromPacket(packet.getData());
        reply(true);
        return true;
    }

    public void reply(boolean isRetrieved) {
        if (isRetrieved)
            LOGGER.info("CobbleGen config has been retrieved from the server");
        val buf = new PacketByteBuf(Unpooled.buffer());
        buf.writeBoolean(isRetrieved && FLUID_INTERACTION.isSync());
        handler.sendPacket(new CustomPayloadC2SPacket(SYNC_CHANNEL, buf));
    }

    public void onDisconnect() {
        FLUID_INTERACTION.disconnect();
    }
}