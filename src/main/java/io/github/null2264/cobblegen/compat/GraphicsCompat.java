package io.github.null2264.cobblegen.compat;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.network.chat.Component;

public class GraphicsCompat {
    @SuppressWarnings("UnusedReturnValue")
    //#if MC<12000
    public static int drawString(PoseStack pose, Component text, int x, int y, int colour) {
    //#else
    //$$ public static int drawString(net.minecraft.client.gui.GuiGraphics graphics, Component text, int x, int y, int colour) {
    //#endif
        Font font = Minecraft.getInstance().font;
        //#if MC<12000
        return font.draw(pose, text, x, y, colour);
        //#else
        //$$ return graphics.drawString(font, graphics, text, x, y, colour, false);
        //#endif
    }
}