package io.github.null2264.cobblegen.network;

import io.github.null2264.cobblegen.data.CGIdentifier;
import io.github.null2264.cobblegen.network.payload.*;
import io.github.null2264.cobblegen.util.CGLog;
import io.github.null2264.cobblegen.util.Util;
import io.netty.buffer.Unpooled;
import lombok.val;
import net.minecraft.network.FriendlyByteBuf;
//#if MC<1.20.2
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.protocol.game.ClientboundCustomPayloadPacket;
import net.minecraft.network.protocol.game.ServerboundCustomPayloadPacket;
//#else
//$$ import net.minecraft.client.multiplayer.ClientCommonPacketListenerImpl;
//$$ import net.minecraft.network.protocol.common.ServerboundCustomPayloadPacket;
//$$ import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
//#endif

import static io.github.null2264.cobblegen.CobbleGen.*;
import static io.github.null2264.cobblegen.util.Constants.*;

public class CGClientPlayNetworkHandler
{
    public static boolean handlePacket(
            //#if MC<1.20.2
            ClientPacketListener listener,
            ClientboundCustomPayloadPacket packet
            //#else
            //$$ ClientCommonPacketListenerImpl listener,
            //$$ CustomPacketPayload payload
            //#endif
    ) {
        //#if MC<1.20.2
        CGIdentifier id = CGIdentifier.fromMC(packet.getIdentifier());
        val packetData = packet.getData();

        val reader = KNOWN_CLIENT_PAYLOADS.get(id);
        if (reader == null) return false;
        val payload = reader.apply(packetData);
        //#endif

        if (payload instanceof CGSyncS2CPayload) {
            val isReload = ((CGSyncS2CPayload) payload).isReload();
            FLUID_INTERACTION.readGeneratorsFromPayload((CGSyncS2CPayload) payload);

            val isSync = FLUID_INTERACTION.isSync();
            if (isSync)
                CGLog.info("CobbleGen config has been", isReload ? "re-synced" : "retrieved from the server");
            val buf = new FriendlyByteBuf(Unpooled.buffer());
            buf.writeResourceLocation(CG_SYNC.toMC());
            new CGSyncC2SPayload(isSync).write(buf);
            listener.send(createC2SPacket(buf));
            return true;
        }

        if (payload instanceof CGPingS2CPayload) {
            val buf = new FriendlyByteBuf(Unpooled.buffer());
            buf.writeResourceLocation(CG_PING.toMC());
            new CGPingC2SPayload(((CGPingS2CPayload) payload).isReload(), Util.isAnyRecipeViewerLoaded()).write(buf);
            listener.send(createC2SPacket(buf));
            return true;
        }

        return false;
    }

    public static void onDisconnect() {
        FLUID_INTERACTION.disconnect();
    }

    private static ServerboundCustomPayloadPacket createC2SPacket(FriendlyByteBuf buf) {
        //#if MC<=1.20.1
        return new ServerboundCustomPayloadPacket(buf.readResourceLocation(), buf);
        //#else
        //$$ return new ServerboundCustomPayloadPacket(buf);
        //#endif
    }
}