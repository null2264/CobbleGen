package io.github.null2264.cobblegen;

import io.netty.buffer.Unpooled;
import lombok.val;
import net.fabricmc.api.DedicatedServerModInitializer;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.network.PacketByteBuf;

import static io.github.null2264.cobblegen.CobbleGen.FLUID_INTERACTION;
import static io.github.null2264.cobblegen.CobbleGen.SYNC_CHANNEL;

public class CobbleGenServer implements DedicatedServerModInitializer
{
    @Override
    public void onInitializeServer() {
        if (FabricLoader.getInstance().isModLoaded("fabric-api")) {
            ServerPlayConnectionEvents.INIT.register((handler, server) -> {
                val buf = new PacketByteBuf(Unpooled.buffer());
                FLUID_INTERACTION.writeGeneratorsToPacket(buf);
                ServerPlayNetworking.send(
                        handler.getPlayer(),
                        SYNC_CHANNEL,
                        buf
                );
            });

            ServerPlayNetworking.registerGlobalReceiver(SYNC_CHANNEL, (server, player, handler, buf, responseSender) -> {
                // TODO: Log when recipe is synced successfully
            });
        }
    }
}