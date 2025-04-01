#if MC>=11801
package io.github.null2264.cobblegen.gametest;

import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.level.block.Blocks;

// NOTE: Mojang didn't ship GameTest until 1.17.0 but Forge didn't support GameTest until 1.18.1
public class BlockGenerationTest {
    // Basically telling Forge to stop being weird
    #if FORGE && MC<12105
        #if FORGE==2
    @net.neoforged.neoforge.gametest.PrefixGameTestTemplate(false)
        #elif FORGE==1
    @net.minecraftforge.gametest.PrefixGameTestTemplate(false)
        #endif
    #endif
    @GameTest(
        #if FORGE && MC<12105
        templateNamespace = "cobblegen",
        template = "empty",
        #else
        template = "cobblegen:empty",
        #endif
        timeoutTicks = 120
    )
    public void cobbleGenerationTest(GameTestHelper context) {
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
        context.succeedWhen(
            () -> {
                // A special config is needed for this test
                context.assertBlockPresent(Blocks.BEDROCK, generatedPos);
            }
        );
    }

    #if FORGE && MC<12105
        #if FORGE==2
    @net.neoforged.neoforge.gametest.PrefixGameTestTemplate(false)
        #elif FORGE==1
    @net.minecraftforge.gametest.PrefixGameTestTemplate(false)
        #endif
    #endif
    @GameTest(
        #if FORGE && MC<12105
        templateNamespace = "cobblegen",
        template = "empty",
        #else
        template = "cobblegen:empty",
        #endif
        timeoutTicks = 120
    )
    public void basaltGenerationTest(GameTestHelper context) {
        context.setBlock(new BlockPos(1, 2, 2), Blocks.BLUE_ICE);
        // << Barrier wrapping the lava
        context.setBlock(new BlockPos(2, 2, 4), Blocks.BARRIER);
        context.setBlock(new BlockPos(0, 2, 4), Blocks.BARRIER);
        context.setBlock(new BlockPos(1, 2, 5), Blocks.BARRIER);
        // >>
        context.setBlock(new BlockPos(1, 1, 4), Blocks.BARRIER);  // Barrier under lava
        context.setBlock(new BlockPos(1, 2, 4), Blocks.LAVA);
        context.setBlock(new BlockPos(1, 1, 3), Blocks.SOUL_SOIL);  // For basalt generators
        BlockPos generatedPos = new BlockPos(1, 2, 3);
        context.succeedWhen(
            () -> {
                // A special config is needed for this test
                context.assertBlockPresent(Blocks.BEDROCK, generatedPos);
            }
        );
    }
}
#endif
