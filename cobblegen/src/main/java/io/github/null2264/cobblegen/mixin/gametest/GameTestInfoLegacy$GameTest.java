#if MC<=11605
package io.github.null2264.cobblegen.mixin.gametest;

import net.minecraft.gametest.framework.GameTestInfo;
import net.minecraft.gametest.framework.GameTestSequence;
import net.minecraft.gametest.framework.TestFunction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Rotation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Collection;

@Mixin(GameTestInfo.class)
public abstract class GameTestInfoLegacy$GameTest extends GameTestInfo {


    public GameTestInfoLegacy$GameTest(TestFunction testFunction, Rotation rotation, ServerLevel serverLevel) {
        super(testFunction, rotation, serverLevel);
    }

    @Accessor(value = "sequences")
    abstract Collection<GameTestSequence> cobblegen$sequences();

    public GameTestSequence createSequence() {
        return null;
    }
}
#endif
