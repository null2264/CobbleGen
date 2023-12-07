package io.github.null2264.cobblegen.network;

import io.github.null2264.cobblegen.data.CGIdentifier;
import io.github.null2264.cobblegen.network.payload.*;
import io.github.null2264.cobblegen.util.CGLog;
import io.netty.buffer.Unpooled;
import lombok.val;
import net.minecraft.network.FriendlyByteBuf;
import org.jetbrains.annotations.ApiStatus;
//#if MC<1.20.2
import net.minecraft.network.protocol.game.ClientboundCustomPayloadPacket;
import net.minecraft.network.protocol.game.ServerboundCustomPayloadPacket;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
//#else
//$$ import net.minecraft.network.protocol.common.ClientboundCustomPayloadPacket;
//$$ import net.minecraft.server.network.ServerCommonPacketListenerImpl;
//$$ import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
//#endif

import static io.github.null2264.cobblegen.CobbleGen.*;
import static io.github.null2264.cobblegen.util.Constants.*;

public class CGServerPlayNetworkHandler
{
    public static void trySync(
            //#if MC<1.20.2
            ServerGamePacketListenerImpl listener
            //#else
            //$$ ServerCommonPacketListenerImpl listener
            //#endif
    ) {
        trySync(listener, false);
    }

    public static void trySync(
            //#if MC<1.20.2
            ServerGamePacketListenerImpl listener,
            //#else
            //$$ ServerCommonPacketListenerImpl listener,
            //#endif
            boolean isReload
    ) {
        if (isReload)
            CGLog.info("CobbleGen has been reloaded, trying to re-sync...");
        else
            CGLog.info("A player joined, checking for recipe viewer...");
        val buf = new FriendlyByteBuf(Unpooled.buffer());
        buf.writeResourceLocation(CG_PING.toMC());
        new CGPingS2CPayload(isReload).write(buf);
        listener.send(createS2CPacket(buf));
    }

    public static boolean handlePacket(
            //#if MC<1.20.2
            ServerGamePacketListenerImpl listener,
            ServerboundCustomPayloadPacket packet
            //#else
            //$$ ServerCommonPacketListenerImpl listener,
            //$$ CustomPacketPayload payload
            //#endif
    ) {
        //#if MC<1.20.2
        CGIdentifier id = CGIdentifier.fromMC(packet.getIdentifier());
        val packetData = packet.getData();

        val reader = KNOWN_SERVER_PAYLOADS.get(id);
        if (reader == null) return false;
        val payload = reader.apply(packetData);
        //#endif

        if (payload instanceof CGPingC2SPayload) {
            if (((CGPingC2SPayload) payload).hasRecipeViewer()) {
                if (!((CGPingC2SPayload) payload).isReload())
                    CGLog.info("Player has recipe viewer installed, sending CobbleGen config...");
                sync(listener, ((CGPingC2SPayload) payload).isReload());
            }
            return true;
        }

        if (payload instanceof CGSyncC2SPayload) {
            if (((CGSyncC2SPayload) payload).isSync())
                CGLog.info("Player has received the server's newest CobbleGen config");
            return true;
        }

        return false;
    }

    //#if MC<1.20.2
    public static void sync(ServerGamePacketListenerImpl handler, boolean isReload) {
    //#else
    //$$ public static void sync(ServerCommonPacketListenerImpl handler, boolean isReload) {
    //#endif
        val buf = new FriendlyByteBuf(Unpooled.buffer());
        buf.writeResourceLocation(CG_SYNC.toMC());
        new CGSyncS2CPayload(isReload, FLUID_INTERACTION.getLocalGenerators()).write(buf);
        handler.send(createS2CPacket(buf));
    }

    @ApiStatus.Internal
    public static ClientboundCustomPayloadPacket createS2CPacket(FriendlyByteBuf buf) {
        //#if MC<=1.20.1
        return new ClientboundCustomPayloadPacket(buf.readResourceLocation(), buf);
        //#else
        //$$ return new ClientboundCustomPayloadPacket(buf);
        //#endif
    }
}