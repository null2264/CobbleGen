package io.github.null2264.cobblegen;

import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.minecraft.block.Blocks;
import net.minecraft.test.GameTest;
import net.minecraft.test.TestContext;
import net.minecraft.util.math.BlockPos;

public class CobbleGenTests
{
    @GameTest(structureName = FabricGameTest.EMPTY_STRUCTURE, tickLimit = 20)
    public void generatorTest(TestContext context) {
        BlockPos pos = new BlockPos(0, 2, 0);
        context.setBlockState(pos, Blocks.WATER);
        BlockPos pos1 = new BlockPos(1, 1, 0);
        context.setBlockState(pos1, Blocks.AIR);
        BlockPos pos2 = new BlockPos(3, 2, 0);
        context.setBlockState(pos2, Blocks.AIR);
        BlockPos generatedPos = new BlockPos(3, 2, 0);
        context.dontExpectBlock(Blocks.COBBLESTONE, generatedPos);
    }
}