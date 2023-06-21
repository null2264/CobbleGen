package io.github.null2264.cobblegen.compat;

import io.github.null2264.gradle.Pattern;
import net.minecraft.network.chat.MutableComponent;

public class TextCompat {
    private static final Object[] NO_ARGS = new Object[0];

    public static MutableComponent literal(String text) {
        //#if MC<=11802
        return new net.minecraft.network.chat.TextComponent(text);
        //#else
        //$$ return net.minecraft.network.chat.Component.literal(text);
        //#endif
    }

    public static MutableComponent translatable(String text) {
        return translatable(text, NO_ARGS);
    }

    public static MutableComponent translatable(String text, Object... args) {
        //#if MC<=11802
        return new net.minecraft.network.chat.TranslatableComponent(text, args);
        //#else
        //$$ return net.minecraft.network.chat.Component.translatable(text, args);
        //#endif
    }
}