package io.github.null2264.cobblegen.compat;

import io.github.null2264.gradle.Pattern;
import net.minecraft.network.chat.MutableComponent;

public class TextCompat {
    public static MutableComponent literal(String text) {
        //#if MC<=11802
        return new net.minecraft.network.chat.TextComponent(text);
        //#else
        //$$ return net.minecraft.network.chat.Component.literal(text);
        //#endif
    }

    public static MutableComponent translatable(String text) {
        //#if MC<=11802
        return new net.minecraft.network.chat.TranslatableComponent(text);
        //#else
        //$$ return net.minecraft.network.chat.Component.translatable(text);
        //#endif
    }
}