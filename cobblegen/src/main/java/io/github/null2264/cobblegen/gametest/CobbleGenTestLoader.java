#if MC>=12105
package io.github.null2264.cobblegen.gametest;

import io.github.null2264.cobblegen.util.CGLog;
import io.github.null2264.cobblegen.util.Util;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.gametest.framework.TestFunctionLoader;
import net.minecraft.resources.ResourceKey;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class CobbleGenTestLoader extends TestFunctionLoader {
    private static final AtomicBoolean SHOULD_REGISTER = new AtomicBoolean(true);

    public static void init() {
        CGLog.info(() -> "Is CobbleGen GameTest enabled: " + CobbleGenTestConfig.ENABLED);
        if (CobbleGenTestConfig.ENABLED && SHOULD_REGISTER.getAndSet(false))
            TestFunctionLoader.registerLoader(new CobbleGenTestLoader());
    }

    @Override
    public void load(@NotNull BiConsumer<ResourceKey<Consumer<GameTestHelper>>, Consumer<GameTestHelper>> registerer) {
        BlockGenerationTest.registerFunctions(registerer);
    }
}
#endif
