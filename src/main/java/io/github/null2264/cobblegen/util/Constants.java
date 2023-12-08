package io.github.null2264.cobblegen.util;

import blue.endless.jankson.Jankson;
import blue.endless.jankson.JsonObject;
import blue.endless.jankson.JsonPrimitive;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import io.github.null2264.cobblegen.config.AdvancedGen;
import io.github.null2264.cobblegen.data.CGIdentifier;
import io.github.null2264.cobblegen.network.payload.*;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Block;

public class Constants
{
    public static final int SLOT_SIZE = 18;
    public static final int JEI_RECIPE_WIDTH = 136;
    public static final int JEI_RECIPE_HEIGHT = 36;
    public static final int JEI_RECIPE_HEIGHT_STONE = 56;
    public static final CGIdentifier JEI_UI_COMPONENT = CGIdentifier.of("textures/gui/jei.png");
    public static final CGIdentifier CG_PING = CGIdentifier.of("ping");
    public static final CGIdentifier CG_SYNC = CGIdentifier.of("sync");
    //#if MC<1.20.2
    public static final ImmutableMap<CGIdentifier, CGPayloadReader<? extends CGPacketPayload>> KNOWN_SERVER_PAYLOADS =
    //#else
    //$$ public static final ImmutableMap<CGIdentifier, CGPayloadReader<? extends net.minecraft.network.protocol.common.custom.CustomPacketPayload>> KNOWN_SERVER_PAYLOADS =
    //#endif
            ImmutableMap.of(
                    CGPingC2SPayload.ID, CGPingC2SPayload::new,
                    CGSyncC2SPayload.ID, CGSyncC2SPayload::new
            );
    //#if MC<1.20.2
    public static final ImmutableMap<CGIdentifier, CGPayloadReader<? extends CGPacketPayload>> KNOWN_CLIENT_PAYLOADS =
    //#else
    //$$ public static final ImmutableMap<CGIdentifier, CGPayloadReader<? extends net.minecraft.network.protocol.common.custom.CustomPacketPayload>> KNOWN_CLIENT_PAYLOADS =
    //#endif
            ImmutableMap.of(
                    CGPingS2CPayload.ID, CGPingS2CPayload::new,
                    CGSyncS2CPayload.ID, CGSyncS2CPayload::new
            );
    public static final Jankson JANKSON = Jankson.builder()
            .registerSerializer(CGIdentifier.class, (it, m) -> it.toJson())
            .registerDeserializer(JsonPrimitive .class, CGIdentifier.class, (json, m) -> CGIdentifier.fromJson(json))
            .registerDeserializer(String.class, CGIdentifier.class, (str, m) -> CGIdentifier.of(str))
            .build();

    /**
     * Just a helper class to make the code more "readable"
     */
    public enum CGBlocks {
        WILDCARD("*");

        private final String text;

        CGBlocks(final String text) {
            this.text = text;
        }

        @Override
        public String toString() {
            return text;
        }

        public static String fromId(net.minecraft.resources.ResourceLocation id) {
            return id.toString();
        }

        public static String fromBlock(Block block) {
            return fromId(Util.getBlockId(block));
        }
    }

    public static final ImmutableList<Direction> FLOW_DIRECTIONS = ImmutableList.of(
            Direction.DOWN, Direction.SOUTH, Direction.NORTH, Direction.EAST, Direction.WEST
    );
}