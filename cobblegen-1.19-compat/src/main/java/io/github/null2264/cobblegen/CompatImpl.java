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
    public Text translatableTextWithLiteral(String string, Text literal) {
        return Text.translatable(string).append(literal);
    }
}