package io.github.null2264.cobblegen.util;


import net.minecraft.block.Block;
import net.minecraft.fluid.Fluid;
import net.minecraft.text.MutableText;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

import java.util.List;

public interface Compat
{
    MutableText translatableText(String string);

    MutableText translatableAppendingText(String string, Text... texts);

    MutableText text(String string);

    MutableText appendingText(String string, Text... texts);

    OrderedText toOrderedText(Text text);

    Block getBlock(Identifier id);

    Identifier getBlockId(Block block);

    List<Identifier> getTaggedBlockIds(Identifier tagId);

    Identifier getFluidId(Fluid fluid);

    String getDimension(World world);
}