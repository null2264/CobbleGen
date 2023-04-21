package io.github.null2264.cobblegen.util;


import net.minecraft.block.Block;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

import java.util.List;

public abstract class Compat
{
    public abstract MutableText translatableText(String string);

    public abstract MutableText translatableTextWithFallback(String string, String fallback);

    public abstract MutableText text(String string);

    public abstract Block getBlock(Identifier id);

    public abstract Identifier getBlockId(Block block);

    public abstract List<Identifier> getTaggedBlockIds(Identifier tagId);

    public abstract String getDimension(World world);
}