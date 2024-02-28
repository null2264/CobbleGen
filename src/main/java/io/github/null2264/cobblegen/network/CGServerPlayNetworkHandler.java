package io.github.null2264.cobblegen.network;

//#if MC<1.20.2
import net.minecraft.network.protocol.game.ClientboundCustomPayloadPacket;
import net.minecraft.network.protocol.game.ServerboundCustomPayloadPacket;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
//#else
//$$ import net.minecraft.network.protocol.common.ClientboundCustomPayloadPacket;
//$$ import net.minecraft.server.network.ServerCommonPacketListenerImpl;
//$$ import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
//#endif

import io.github.null2264.cobblegen.data.CGIdentifier;
import io.github.null2264.cobblegen.network.payload.*;
import io.github.null2264.cobblegen.util.CGLog;
import net.minecraft.network.FriendlyByteBuf;
import org.jetbrains.annotations.ApiStatus;

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
            CGLog.debug("A player joined, checking for recipe viewer...");
        CGPacketPayload payload = new CGPingS2CPayload(isReload);
        listener.send(createS2CPacket(payload));
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
        // FIXME: Enable REI integration
        //#if MC<=1.16.5
        //$$ return false;
        //#else

        //#if MC<1.20.2
        CGIdentifier id = CGIdentifier.fromMC(packet.getIdentifier());
        FriendlyByteBuf packetData = packet.getData();

        CGPayloadReader<? extends CGPacketPayload> reader = KNOWN_SERVER_PAYLOADS.get(id);
        if (reader == null) return false;
        CGPacketPayload payload = reader.apply(packetData);
        //#endif

        if (payload instanceof CGPingC2SPayload) {
            if (((CGPingC2SPayload) payload).hasRecipeViewer()) {
                if (!((CGPingC2SPayload) payload).reload())
                    CGLog.info("Player has recipe viewer installed, sending CobbleGen config...");
                sync(listener, ((CGPingC2SPayload) payload).reload());
            }
            return true;
        }

        if (payload instanceof CGSyncC2SPayload) {
            if (((CGSyncC2SPayload) payload).sync())
                CGLog.info("Player has received the server's newest CobbleGen config");
            return true;
        }

        return false;
        //#endif
    }

    //#if MC<1.20.2
    public static void sync(ServerGamePacketListenerImpl handler, boolean isReload) {
    //#else
    //$$ public static void sync(ServerCommonPacketListenerImpl handler, boolean isReload) {
    //#endif
        CGPacketPayload payload = new CGSyncS2CPayload(isReload, FLUID_INTERACTION.getLocalGenerators());
        handler.send(createS2CPacket(payload));
    }

    @ApiStatus.Internal
    public static ClientboundCustomPayloadPacket createS2CPacket(CGPacketPayload payload) {
        //#if MC<=1.20.1
        FriendlyByteBuf buf = FriendlyByteBuf.unpooled();
        payload.write(buf);
        return new ClientboundCustomPayloadPacket(payload.id(), buf);
        //#else
        //$$ return new ClientboundCustomPayloadPacket(payload);
        //#endif
    }
}