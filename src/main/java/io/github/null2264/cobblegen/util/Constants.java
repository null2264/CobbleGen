package io.github.null2264.cobblegen.util;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;

public class Constants
{
    public static final int SLOT_SIZE = 18;
    public static final int JEI_RECIPE_WIDTH = 136;
    public static final int JEI_RECIPE_HEIGHT = 36;
    public static final int JEI_RECIPE_HEIGHT_STONE = 56;
    public static final ResourceLocation JEI_UI_COMPONENT = Util.identifierOf("textures/gui/jei.png");

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

        public static String fromId(ResourceLocation id) {
            return id.toString();
        }

        public static String fromBlock(Block block) {
            return fromId(Util.getBlockId(block));
        }
    }
}