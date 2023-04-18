package io.github.null2264.cobblegen.util;


import net.minecraft.block.Block;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

import java.util.List;

public abstract class Compat
{
    public abstract Block getBlock(Identifier id);

    public abstract List<Identifier> getTaggedBlockIds(Identifier tagId);

    public abstract String getDimension(World world);
}