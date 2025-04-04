#if MC>=12105
package io.github.null2264.cobblegen.gametest;

import io.github.null2264.cobblegen.data.CGIdentifier;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.registries.VanillaRegistries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.gametest.framework.*;
import net.minecraft.resources.ResourceKey;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * Mojang's new GameTest system
 *
 *  -> CobbleGen()
 *  -> CobbleGenTestFunctions.init()  # register the function
 *  -> ...
 *  -> GameTestInstances::bootstrap  # vanilla registering their instances
 *  -> GameTestInstancesMixin::injectInstance
 *  -> CobbleGenTestFunctions.injectInstance()  # inject our own instances
 */
public class CobbleGenTestFunctions extends TestFunctionLoader {
    public static final BlockGenerationTest TEST = new BlockGenerationTest();

    public static final String COBBLE_GENERATION_ID = "cobble_generation";
    public static final ResourceKey<Consumer<GameTestHelper>> COBBLE_GENERATION_TEST =
        ResourceKey.create(Registries.TEST_FUNCTION, CGIdentifier.of(COBBLE_GENERATION_ID).toMC());
    public static final Consumer<GameTestHelper> COBBLE_GENERATION_TEST_INSTANCE = TEST::cobbleGenerationTest;

    public static final String BASALT_GENERATION_ID = "basalt_generation";
    public static final ResourceKey<Consumer<GameTestHelper>> BASALT_GENERATION_TEST =
        ResourceKey.create(Registries.TEST_FUNCTION, CGIdentifier.of(BASALT_GENERATION_ID).toMC());
    public static final Consumer<GameTestHelper> BASALT_GENERATION_TEST_INSTANCE = TEST::basaltGenerationTest;

    /**
     * Mod Loader
     *  -> CobbleGenTestFunctions.init()  # register the function
     *  -> ...
     *  -> GameTestInstances::bootstrap  # vanilla registering their instances
     *  -> GameTestInstancesMixin::injectInstance
     *  -> CobbleGenTestFunctions.injectInstance()  # inject our own instances
     */
    public static void init() {
        registerLoader(new CobbleGenTestFunctions());
    }

    private static GameTestInstance create(ResourceKey<Consumer<GameTestHelper>> key) {
        ResourceKey<TestEnvironmentDefinition> envKey = ResourceKey.create(Registries.TEST_ENVIRONMENT, CGIdentifier.of("minecraft:default").toMC());
        Holder<TestEnvironmentDefinition> testEnvironment = VanillaRegistries.createLookup().getOrThrow(envKey);
        return new FunctionGameTestInstance(
            key,
            new TestData<>(
                testEnvironment,
                BlockGenerationTest.TEMPLATE.toMC(),
                BlockGenerationTest.TIMEOUT_TICKS,
                0,
                true
            )
        );
    }

    private static ResourceKey<GameTestInstance> createKey(String id) {
        return ResourceKey.create(Registries.TEST_INSTANCE, CGIdentifier.of(id).toMC());
    }

    public static void injectInstance(BootstrapContext<GameTestInstance> bootstrapContext) {
        bootstrapContext.register(createKey(COBBLE_GENERATION_ID), create(COBBLE_GENERATION_TEST));
        bootstrapContext.register(createKey(BASALT_GENERATION_ID), create(BASALT_GENERATION_TEST));
    }

    @Override
    public void load(BiConsumer<ResourceKey<Consumer<GameTestHelper>>, Consumer<GameTestHelper>> biConsumer) {
        biConsumer.accept(COBBLE_GENERATION_TEST, COBBLE_GENERATION_TEST_INSTANCE);
        biConsumer.accept(BASALT_GENERATION_TEST, BASALT_GENERATION_TEST_INSTANCE);
    }
}
#endif
