package io.github.null2264.cobblegen;

import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;

public class CompatImpl extends Compat118
{
    @Override
    public Text translatableText(String string) {
        return new TranslatableText(string);
    }

    @Override
    public Text translatableTextWithLiteral(String string, Text literal) {
        return new TranslatableText(string).append(literal);
    }
}