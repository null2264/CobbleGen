package io.github.null2264.cobblegen;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.loader.api.FabricLoader;

import static io.github.null2264.cobblegen.CobbleGen.FLUID_INTERACTION;
import static io.github.null2264.cobblegen.CobbleGen.SYNC_CHANNEL;

public class CobbleGenClient implements ClientModInitializer
{
    @Override
    public void onInitializeClient() {
        if (
            FabricLoader.getInstance().isModLoaded("roughlyenoughitems") ||
            FabricLoader.getInstance().isModLoaded("jei") ||
            FabricLoader.getInstance().isModLoaded("emi")
        ) {
            ClientPlayConnectionEvents.INIT.register((handler, client) -> {
                ClientPlayNetworking.registerReceiver(SYNC_CHANNEL, (client1, handler1, buf, responseSender) -> {
                    FLUID_INTERACTION.readGeneratorsFromPacket(buf);
                    // TODO: Send sync state to server
                });
            });

            ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> {
                FLUID_INTERACTION.disconnect();
            });
        }
    }
}