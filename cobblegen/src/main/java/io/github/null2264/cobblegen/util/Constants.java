package io.github.null2264.cobblegen.util;

#if MC>=12005
import io.netty.buffer.ByteBuf;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.material.Fluid;
#endif

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import io.github.null2264.cobblegen.data.CGIdentifier;
import io.github.null2264.cobblegen.network.payload.*;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Block;

// FIXME: Move MC related consts to :mclib, for now don't use stuff from here inside "early state" of mod loading (e.g. MixinPlugin)
public class Constants
{
    public static final int SLOT_SIZE = 18;
    public static final int JEI_RECIPE_WIDTH = 136;
    public static final int JEI_RECIPE_HEIGHT = 36;
    public static final int JEI_RECIPE_HEIGHT_STONE = 56;
    public static final CGIdentifier JEI_UI_COMPONENT = CGIdentifier.of("textures/gui/jei.png");
    public static final CGIdentifier CG_PING = CGIdentifier.of("ping");
    public static final CGIdentifier CG_SYNC = CGIdentifier.of("sync");
    public static final CGIdentifier CG_PING_SERVER = CGIdentifier.of("ping_server");
    public static final CGIdentifier CG_SYNC_SERVER = CGIdentifier.of("sync_server");
    #if MC<12005
    public static final ImmutableMap<CGIdentifier, CGPayloadReader<? extends CGPacketPayload>> KNOWN_SERVER_PAYLOADS =
            ImmutableMap.of(
                CGPingC2SPayload.ID, CGPingC2SPayload::new,
                CGSyncC2SPayload.ID, CGSyncC2SPayload::new
            );
    public static final ImmutableMap<CGIdentifier, CGPayloadReader<? extends CGPacketPayload>> KNOWN_CLIENT_PAYLOADS =
            ImmutableMap.of(
                CGPingS2CPayload.ID, CGPingS2CPayload::new,
                CGSyncS2CPayload.ID, CGSyncS2CPayload::new
            );
    #else
    public static final ImmutableMap<CGIdentifier, net.minecraft.network.codec.StreamCodec<? super FriendlyByteBuf, ? extends CGPacketPayload>> KNOWN_PAYLOADS =
            ImmutableMap.of(
                CGPingC2SPayload.ID, CGPingC2SPayload.STREAM_CODEC,
                CGSyncC2SPayload.ID, CGSyncC2SPayload.STREAM_CODEC,
                CGPingS2CPayload.ID, CGPingS2CPayload.STREAM_CODEC,
                CGSyncS2CPayload.ID, CGSyncS2CPayload.STREAM_CODEC
            );
    #endif
    public static final int LAVA_FIZZ = 1501;
    //public static final int OP_LEVEL_PLAYER = 0;
    //public static final int OP_LEVEL_MODERATORS = 1;
    public static final int OP_LEVEL_GAMEMASTERS = 2;
    //public static final int OP_LEVEL_ADMINS = 3;
    //public static final int OP_LEVEL_OWNERS = 4;
    #if MC>=12005
    public static final net.minecraft.network.codec.StreamCodec<ByteBuf, Fluid> FLUID_CODEC =
        new net.minecraft.network.codec.StreamCodec<ByteBuf, Fluid>()
        {
            @Override
            public Fluid decode(ByteBuf buf) {
                FriendlyByteBuf compat = new FriendlyByteBuf(buf);
                return Util.getFluid(compat.readResourceLocation());
            }

            @Override
            public void encode(ByteBuf buf, Fluid fluid) {
                FriendlyByteBuf newBuf = FriendlyByteBuf.unpooled();
                newBuf.writeResourceLocation(Util.getFluidId(fluid));
                buf.writeBytes(newBuf);
            }
        };
    #endif

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
