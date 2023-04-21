package io.github.null2264.cobblegen;

import net.minecraft.text.MutableText;
import net.minecraft.text.Text;

public class CompatImpl extends Compat118
{
    @Override
    public MutableText translatableText(String string) {
        return Text.translatable(string);
    }

    @Override
    public MutableText translatableTextWithFallback(String string, String fallback) {
        return Text.translatable(string, fallback);
    }

    @Override
    public MutableText text(String string) {
        return Text.literal(string);
    }
}