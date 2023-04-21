package io.github.null2264.cobblegen.util;


import net.minecraft.block.Block;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

import java.util.List;

public interface Compat
{
    MutableText translatableText(String string);

    MutableText translatableAppendingText(String string, List<Text> texts);

    MutableText text(String string);

    MutableText appendingText(String string, List<Text> texts);

    Block getBlock(Identifier id);

    Identifier getBlockId(Block block);

    List<Identifier> getTaggedBlockIds(Identifier tagId);

    String getDimension(World world);
}