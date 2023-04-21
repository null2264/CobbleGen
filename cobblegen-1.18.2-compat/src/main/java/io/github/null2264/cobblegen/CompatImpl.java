package io.github.null2264.cobblegen;

import net.minecraft.text.LiteralText;
import net.minecraft.text.MutableText;
import net.minecraft.text.TranslatableText;

public class CompatImpl extends Compat118
{
    @Override
    public MutableText translatableText(String string) {
        return new TranslatableText(string);
    }

    @Override
    public MutableText translatableTextWithFallback(String string, String fallback) {
        return new TranslatableText(string, fallback);
    }

    @Override
    public MutableText text(String string) {
        return new LiteralText(string);
    }
}