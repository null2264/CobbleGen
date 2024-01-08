package io.github.null2264.cobblegen.network;

//#if MC<1.20.2
import io.github.null2264.cobblegen.mixin.network.ServerboundCustomPayloadPacketAccessor;
import net.minecraft.network.protocol.game.ClientboundCustomPayloadPacket;
import net.minecraft.network.protocol.game.ServerboundCustomPayloadPacket;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
//#else
//$$ import net.minecraft.network.protocol.common.ClientboundCustomPayloadPacket;
//$$ import net.minecraft.server.network.ServerCommonPacketListenerImpl;
//$$ import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
//#endif

import io.github.null2264.cobblegen.compat.ByteBufCompat;
import io.github.null2264.cobblegen.util.CGLog;
import io.netty.buffer.Unpooled;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

import java.util.Objects;

import static io.github.null2264.cobblegen.CobbleGen.*;

public class CGServerPlayNetworkHandler
{
    //#if MC<1.20.2
    public static void trySync(ServerGamePacketListenerImpl listener) {
    //#else
    //$$ public static void trySync(ServerCommonPacketListenerImpl listener) {
    //#endif
        trySync(listener, false);
    }

    //#if MC<1.20.2
    public static void trySync(ServerGamePacketListenerImpl listener, boolean isReload) {
    //#else
    //$$ public static void trySync(ServerCommonPacketListenerImpl listener, boolean isReload) {
    //#endif
        if (isReload)
            CGLog.info("CobbleGen has been reloaded, trying to re-sync...");
        else
            CGLog.info("A player joined, checking for recipe viewer...");
        final FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
        buf.writeResourceLocation(keyFromChannel(Channel.PING));
        buf.writeBoolean(isReload);
        buf.writeUtf("ping");  // Basically "do you want this?"
        listener.send(createS2CPacket(buf));
    }

    //#if MC<1.20.2
    public static boolean handlePacket(ServerGamePacketListenerImpl listener, ServerboundCustomPayloadPacket packet) {
    //#else
    //$$ @SuppressWarnings("UnstableApiUsage")
    //$$ public static boolean handlePacket(ServerCommonPacketListenerImpl listener, CustomPacketPayload packet) {
    //#endif
        // FIXME: Enable REI integration
        //#if MC<=1.16.5
        //$$ return false;
        //#endif

        //#if MC>=1.20.2
        //$$ if (!(packet instanceof PacketByteBufPayload)) return false;
        //$$ ResourceLocation id = ((PacketByteBufPayload) packet).id();
        //#else
        ResourceLocation id = ((ServerboundCustomPayloadPacketAccessor) packet).getResourceLocation();
        //#endif

        if (id.equals(SYNC_CHANNEL)) {
            final FriendlyByteBuf packetData =
                    //#if MC<1.20.2
                    ((ServerboundCustomPayloadPacketAccessor) packet).getByte();
                    //#else
                    //$$ ((PacketByteBufPayload) packet).data();
                    //#endif

            boolean received = packetData.readBoolean();
            if (received)
                CGLog.info("Player has received the server's newest CobbleGen config");
            return true;
        } else if (id.equals(SYNC_PING_CHANNEL)) {
            final FriendlyByteBuf packetData =
                    //#if MC<1.20.2
                    ((ServerboundCustomPayloadPacketAccessor) packet).getByte();
                    //#else
                    //$$ ((PacketByteBufPayload) packet).data();
                    //#endif

            boolean isReload = packetData.readBoolean();
            boolean isInstalled = packetData.readBoolean();
            if (isInstalled) {
                if (!isReload)
                    CGLog.info("Player has recipe viewer installed, sending CobbleGen config...");
                sync(listener, isReload);
            }
            return true;
        }
        return false;
    }

    //#if MC<1.20.2
    public static void sync(ServerGamePacketListenerImpl handler, boolean isReload) {
    //#else
    //$$ public static void sync(ServerCommonPacketListenerImpl handler, boolean isReload) {
    //#endif
        final ByteBufCompat buf = new ByteBufCompat(Unpooled.buffer());
        buf.writeResourceLocation(keyFromChannel(Channel.SYNC));
        buf.writeBoolean(isReload);
        FLUID_INTERACTION.write(buf);
        handler.send(createS2CPacket(buf));
    }

    private static ResourceLocation keyFromChannel(Channel channel) {
        if (Objects.requireNonNull(channel) == Channel.PING) {
            return SYNC_PING_CHANNEL;
        }
        return SYNC_CHANNEL;
    }

    private static ClientboundCustomPayloadPacket createS2CPacket(FriendlyByteBuf buf) {
        //#if MC<=1.20.1
        return new ClientboundCustomPayloadPacket(buf.readResourceLocation(), buf);
        //#else
        //$$ return new ClientboundCustomPayloadPacket(buf);
        //#endif
    }
}