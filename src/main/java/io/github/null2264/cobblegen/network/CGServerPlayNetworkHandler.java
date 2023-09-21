package io.github.null2264.cobblegen.network;

import io.github.null2264.cobblegen.util.CGLog;
import io.netty.buffer.Unpooled;
import lombok.val;
import net.minecraft.network.FriendlyByteBuf;
//#if MC<1.20.2
import net.minecraft.network.protocol.game.ClientboundCustomPayloadPacket;
import net.minecraft.network.protocol.game.ServerboundCustomPayloadPacket;
//#else
//$$ import net.minecraft.network.protocol.common.ClientboundCustomPayloadPacket;
//$$ import net.minecraft.network.protocol.common.ServerboundCustomPayloadPacket;
//#endif
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.network.ServerGamePacketListenerImpl;

import static io.github.null2264.cobblegen.CobbleGen.*;

public class CGServerPlayNetworkHandler
{
    public static void trySync(ServerGamePacketListenerImpl listener) {
        trySync(listener, false);
    }

    public static void trySync(ServerGamePacketListenerImpl listener, boolean isReload) {
        if (isReload)
            CGLog.info("CobbleGen has been reloaded, trying to re-sync...");
        else
            CGLog.info("A player joined, checking for recipe viewer...");
        val buf = new FriendlyByteBuf(Unpooled.buffer());
        buf.writeResourceLocation(keyFromChannel(Channel.PING));
        buf.writeBoolean(isReload);
        buf.writeUtf("ping");  // Basically "do you want this?"
        listener.send(createS2CPacket(buf));
    }

    //#if MC<=1.20.1
    public static boolean handlePacket(ServerGamePacketListenerImpl listener, ServerboundCustomPayloadPacket packet) {
        if (packet.getIdentifier().equals(SYNC_CHANNEL)) {
            val received = packet.getData().readBoolean();
            if (received)
                CGLog.info("Player has received the server's newest CobbleGen config");
            return true;
        } else if (packet.getIdentifier().equals(SYNC_PING_CHANNEL)) {
            val isReload = packet.getData().readBoolean();
            val status = packet.getData().readBoolean();
            if (status) {
                if (!isReload)
                    CGLog.info("Player has recipe viewer installed, sending CobbleGen config...");
                sync(listener, isReload);
            }
            return true;
        }
        return false;
    }

    public static void sync(ServerGamePacketListenerImpl handler, boolean isReload) {
        val buf = new FriendlyByteBuf(Unpooled.buffer());
        buf.writeResourceLocation(keyFromChannel(Channel.SYNC));
        buf.writeBoolean(isReload);
        FLUID_INTERACTION.writeGeneratorsToPacket(buf);
        handler.send(createS2CPacket(buf));
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

    private static ClientboundCustomPayloadPacket createS2CPacket(FriendlyByteBuf buf) {
        //#if MC<=1.20.1
        return new ClientboundCustomPayloadPacket(buf.readResourceLocation(), buf);
        //#else
        //$$ return new ClientboundCustomPayloadPacket(buf);
        //#endif
    }
}