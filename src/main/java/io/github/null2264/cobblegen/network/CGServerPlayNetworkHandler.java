package io.github.null2264.cobblegen.network;

import io.github.null2264.cobblegen.util.CGLog;
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
    public static void trySync(ServerPlayNetworkHandler handler) {
        trySync(handler, false);
    }

    public static void trySync(ServerPlayNetworkHandler handler, boolean isReload) {
        if (isReload)
            CGLog.info("CobbleGen has been reloaded, trying to re-sync...");
        else
            CGLog.info("A player joined, checking for recipe viewer...");
        val buf = new PacketByteBuf(Unpooled.buffer());
        buf.writeBoolean(isReload);
        buf.writeString("ping");  // Basically "do you want this?"
        handler.sendPacket(createS2CPacket(Channel.PING, buf));
    }

    public static boolean handlePacket(ServerPlayNetworkHandler handler, CustomPayloadC2SPacket packet) {
        if (packet.getChannel().equals(SYNC_CHANNEL)) {
            val received = packet.getData().readBoolean();
            if (received)
                CGLog.info("Player has received the server's newest CobbleGen config");
            return true;
        } else if (packet.getChannel().equals(SYNC_PING_CHANNEL)) {
            val isReload = packet.getData().readBoolean();
            val status = packet.getData().readBoolean();
            if (status) {
                if (!isReload)
                    CGLog.info("Player has recipe viewer installed, sending CobbleGen config...");
                sync(handler, isReload);
            }
            return true;
        }
        return false;
    }

    public static void sync(ServerPlayNetworkHandler handler, boolean isReload) {
        val buf = new PacketByteBuf(Unpooled.buffer());
        buf.writeBoolean(isReload);
        FLUID_INTERACTION.writeGeneratorsToPacket(buf);
        handler.sendPacket(createS2CPacket(Channel.SYNC, buf));
    }

    private static CustomPayloadS2CPacket createS2CPacket(Channel channel, PacketByteBuf buf) {
        Identifier channelId;
        switch (channel) {
            case PING -> channelId = SYNC_PING_CHANNEL;
            default -> channelId = SYNC_CHANNEL;
        }
        return new CustomPayloadS2CPacket(channelId, buf);
    }
}