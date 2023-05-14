package io.github.null2264.cobblegen;

import io.github.null2264.cobblegen.data.FluidInteractionHelper;
import io.github.null2264.cobblegen.util.Compat;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.util.Identifier;

public class CobbleGen implements ModInitializer
{
    public static final String MOD_ID = "cobblegen";
    public static final Identifier SYNC_CHANNEL = new Identifier(MOD_ID, "sync");
    public static final Identifier SYNC_PING_CHANNEL = new Identifier(MOD_ID, "sync_ping");
    public static final FluidInteractionHelper FLUID_INTERACTION = new FluidInteractionHelper();
    private static Compat compat;

    public static Compat getCompat() {
        if (compat == null) compat = FabricLoader.getInstance().getEntrypoints("cobblegen-compat", Compat.class).get(0);
        return compat;
    }

    @Override
    public void onInitialize() {
        //FLUID_INTERACTION.apply();
    }

    public enum Channel {
        PING,
        SYNC,
    }
}