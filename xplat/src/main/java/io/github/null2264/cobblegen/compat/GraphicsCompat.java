package io.github.null2264.cobblegen.compat;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.network.chat.Component;

public class GraphicsCompat {
    @SuppressWarnings("UnusedReturnValue")
    #if MC<12000
    public static int drawString(com.mojang.blaze3d.vertex.PoseStack pose, Component text, int x, int y, int colour) {
    #else
    public static
    #if MC<12111
    int
    #else
    void
    #endif
    drawString(net.minecraft.client.gui.GuiGraphics graphics, Component text, int x, int y, int colour) {
    #endif
        Font font = Minecraft.getInstance().font;
        #if MC<12000
        return font.draw(pose, text, x, y, colour);
        #else
        #if MC<12111
        return
        #endif
        graphics.drawString(font, text, x, y, colour, false);
        #endif
    }
}
