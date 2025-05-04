package io.github.null2264.cobblegen.network;

import io.github.null2264.cobblegen.network.payload.*;
import io.github.null2264.cobblegen.util.CGLog;
import io.github.null2264.cobblegen.util.Util;
import org.jetbrains.annotations.ApiStatus;

#if MC<12002
import io.github.null2264.cobblegen.data.CGIdentifier;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.game.ClientboundCustomPayloadPacket;
import net.minecraft.network.protocol.game.ServerboundCustomPayloadPacket;

import static io.github.null2264.cobblegen.util.Constants.KNOWN_CLIENT_PAYLOADS;
#else
import net.minecraft.client.multiplayer.ClientCommonPacketListenerImpl;
import net.minecraft.network.protocol.common.ServerboundCustomPayloadPacket;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
#endif

import static io.github.null2264.cobblegen.CobbleGen.FLUID_INTERACTION;

public class CGClientPlayNetworkHandler
{
    public static boolean handlePacket(
            #if MC<12002
            ClientPacketListener listener,
            ClientboundCustomPayloadPacket packet
            #else
            ClientCommonPacketListenerImpl listener,
            CustomPacketPayload payload
            #endif
    ) {
        // FIXME: Enable REI integration for 1.16.5
        #if MC<=11605
        return false;
        #else

        #if MC<12002
        CGIdentifier id = CGIdentifier.fromMC(packet.getIdentifier());
        FriendlyByteBuf packetData = packet.getData();

        CGPayloadReader<? extends CGPacketPayload> reader = KNOWN_CLIENT_PAYLOADS.get(id);
        if (reader == null) return false;
        CGPacketPayload payload = reader.apply(packetData);
        #endif

        if (payload instanceof CGSyncS2CPayload) {
            Boolean isReload = ((CGSyncS2CPayload) payload).isReload();
            FLUID_INTERACTION.readGeneratorsFromPayload((CGSyncS2CPayload) payload);

            boolean isSync = FLUID_INTERACTION.isSync();
            if (isSync)
                CGLog.info("CobbleGen config has been", isReload ? "re-synced" : "retrieved from the server");
            CGPacketPayload newPayload = new CGSyncC2SPayload(isSync);
            listener.send(createC2SPacket(newPayload));
            return true;
        }

        if (payload instanceof CGPingS2CPayload) {
            CGPacketPayload newPayload =
                new CGPingC2SPayload(((CGPingS2CPayload) payload).reload(), Util.isAnyRecipeViewerLoaded());
            listener.send(createC2SPacket(newPayload));
            return true;
        }

        return false;
        #endif
    }

    public static void onDisconnect() {
        FLUID_INTERACTION.disconnect();
    }

    @ApiStatus.Internal
    private static ServerboundCustomPayloadPacket createC2SPacket(CGPacketPayload payload) {
        #if MC<=12001
        FriendlyByteBuf buf = FriendlyByteBuf.unpooled();
        payload.write(buf);
        return new ServerboundCustomPayloadPacket(payload.id(), buf);
        #else
        return new ServerboundCustomPayloadPacket(payload);
        #endif
    }
}
