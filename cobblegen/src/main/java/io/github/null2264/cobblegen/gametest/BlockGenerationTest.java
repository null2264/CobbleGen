package io.github.null2264.cobblegen.gametest;

import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.level.block.Blocks;

public class BlockGenerationTest {
    // FIXME: Forge can't find this template, workaround is needed (mixin?)
    @GameTest(template = "cobblegen:empty", timeoutTicks = 120)
    public void generationTest(GameTestHelper context) {
        // << Barrier wrapping the water
        context.setBlock(new BlockPos(1, 2, 0), Blocks.BARRIER);
        context.setBlock(new BlockPos(0, 2, 1), Blocks.BARRIER);
        context.setBlock(new BlockPos(0, 2, 2), Blocks.BARRIER);
        context.setBlock(new BlockPos(2, 2, 1), Blocks.BARRIER);
        context.setBlock(new BlockPos(2, 2, 2), Blocks.BARRIER);

        context.setBlock(new BlockPos(0, 1, 2), Blocks.BARRIER);
        context.setBlock(new BlockPos(2, 1, 2), Blocks.BARRIER);
        // >>
        context.setBlock(new BlockPos(1, 1, 1), Blocks.BARRIER);  // Barrier under still water
        context.setBlock(new BlockPos(1, 0, 2), Blocks.BARRIER);  // Barrier under flowing water
        context.setBlock(new BlockPos(1, 2, 1), Blocks.WATER);
        // << Barrier wrapping the lava
        context.setBlock(new BlockPos(2, 2, 4), Blocks.BARRIER);
        context.setBlock(new BlockPos(0, 2, 4), Blocks.BARRIER);
        context.setBlock(new BlockPos(1, 2, 5), Blocks.BARRIER);
        // >>
        context.setBlock(new BlockPos(1, 1, 4), Blocks.BARRIER);  // Barrier under lava
        context.setBlock(new BlockPos(1, 2, 4), Blocks.LAVA);
        context.setBlock(new BlockPos(1, 1, 3), Blocks.BARRIER);  // Barrier under the generated block
        BlockPos generatedPos = new BlockPos(1, 2, 3);
        context.runAtTickTime(
            50,  // Lava flows 30 ticks per block while Water is 8 ticks per block
            () -> {
                // A special config is needed for this test
                context.assertBlockPresent(Blocks.BEDROCK, generatedPos);
            }
        );
    }
}
