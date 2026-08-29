#if MC<=11605
package io.github.null2264.cobblegen.mixin.gametest;

import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.gametest.framework.GameTestInfo;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(GameTestHelper.class)
public abstract class GameTestHelperLegacy$GameTest extends GameTestHelper {

    public GameTestHelperLegacy$GameTest(GameTestInfo gameTestInfo) {
        super(gameTestInfo);
    }

    @Accessor(value = "testInfo")
    abstract GameTestInfo cobblegen$testInfo();

    public BlockPos absolutePos(BlockPos pos) {
        BlockPos structureBlockPos = cobblegen$testInfo().getStructureBlockPos();
        BlockPos actualPos = structureBlockPos.offset(pos);
        return StructureTemplate.transform(actualPos, Mirror.NONE, cobblegen$testInfo().getRotation(), structureBlockPos);
    }

    public void setBlock(BlockPos pos, Block block) {
        setBlock(pos, block.defaultBlockState());
    }

    public void setBlock(BlockPos pos, BlockState blockState) {
        cobblegen$testInfo().getLevel().setBlock(this.absolutePos(pos), blockState, 3);
    }

    public void succeedWhen(Runnable runnable) {
    }

    public void assertBlockPresent(Block block, BlockPos pos) {
    }
}
#endif
