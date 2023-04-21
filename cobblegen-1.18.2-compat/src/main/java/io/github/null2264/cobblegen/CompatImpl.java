package io.github.null2264.cobblegen;

import net.minecraft.text.LiteralText;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;

import java.util.List;

public class CompatImpl extends Compat118
{
    @Override
    public MutableText translatableText(String string) {
        return new TranslatableText(string);
    }

    @Override
    public MutableText translatableAppendingText(String string, List<Text> texts) {
        MutableText text = translatableText(string);
        for (Text appendText : texts) {
            text.append(appendText);
        }
        return text;
    }

    @Override
    public MutableText text(String string) {
        return new LiteralText(string);
    }

    @Override
    public MutableText appendingText(String string, List<Text> texts) {
        MutableText text = text(string);
        for (Text appendText : texts) {
            text.append(appendText);
        }
        return text;
    }
}