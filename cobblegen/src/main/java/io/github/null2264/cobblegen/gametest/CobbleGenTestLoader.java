#if MC>=12105
package io.github.null2264.cobblegen.gametest;

import io.github.null2264.cobblegen.util.CGLog;
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
    public static final boolean ENABLED = boolFromString(
        System.getProperty("null2264.cobblegen.gametest", System.getenv("ENABLE_NULL2264_COBBLEGEN_GAMETEST"))
    );

    /**
     * Basically {@link Boolean#parseBoolean(String string)} but with support for other boolean representation
     * to be more user-friendly.
     *
     * @param string the String containing the boolean representation
     * @return the boolean represented by the string argument
     */
    private static boolean boolFromString(String string) {
        if (string == null) return false;

        List<String> yes = List.of("yes", "y", "true", "t", "1", "enable", "on");
        List<String> no = List.of("no", "n", "false", "f", "0", "disable", "off");

        if (yes.contains(string.toLowerCase())) return true;
        else if (no.contains(string.toLowerCase())) return false;

        // We supposed to throw an exception here, but we'll fallback to false instead
        return false;
    }

    public static void init() {
        CGLog.info(() -> "Is CobbleGen GameTest enabled: " + ENABLED);
        if (ENABLED && SHOULD_REGISTER.getAndSet(false))
            TestFunctionLoader.registerLoader(new CobbleGenTestLoader());
    }

    @Override
    public void load(@NotNull BiConsumer<ResourceKey<Consumer<GameTestHelper>>, Consumer<GameTestHelper>> registerer) {
        BlockGenerationTest.registerFunctions(registerer);
    }
}
#endif
