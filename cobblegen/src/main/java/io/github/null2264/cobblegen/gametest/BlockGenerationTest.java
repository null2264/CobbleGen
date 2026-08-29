package io.github.null2264.cobblegen.gametest;

import io.github.null2264.cobblegen.data.CGIdentifier;
import io.github.null2264.cobblegen.mixin.gametest.GameTestHelperLegacy$GameTest;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.level.block.Blocks;

#if FORGE && FORGE==1
import net.minecraftforge.gametest.PrefixGameTestTemplate;
#else
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;
#endif

#if MC>=12105
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.core.Holder;
import net.minecraft.gametest.framework.FunctionGameTestInstance;
import net.minecraft.gametest.framework.GameTestEnvironments;
import net.minecraft.gametest.framework.GameTestInstance;
import net.minecraft.gametest.framework.TestData;
import net.minecraft.gametest.framework.TestEnvironmentDefinition;
import net.minecraft.resources.RegistryDataLoader;
import net.minecraft.resources.ResourceKey;

import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import static io.github.null2264.cobblegen.compat.CollectionCompat.listOf;
#endif

/**
 * GameTest holder for CobbleGen Tests.
 *
 * Mojang starts shipping GameTest around 1.17 but Forge didn't support it until 1.18.1.
 * In 1.21.5, Mojang completely overhauled GameTest from automatically register functions annotated with @GameTest,
 * Mojang gave a more abstract API for modders to use.
 *
 * Basically, after 1.21.5 you need 3 things to be registered:
 * - Test Function
 * - Test Instance
 *   -> Holds Test Data (which sorta replacing @GameTest annotation's job)
 *   -> Calls Test Function once the structure is placed
 * - Test Environment
 *   -> CobbleGen use 'minecraft:default', so no need to do this
 * But for whatever reason Mojang only provides a way to register Test Function, fun! :^)
 *
 * CobbleGen's 1.21.5 GameTest register flow:
 * - Minecraft is being launched by the launcher
 * - new CobbleGenPreLaunch() // CobbleGenMixinPlugin.onLoad()
 *   -> CobbleGenTestLoader is registered -> Gated by -Dnull2264.cobblegen.gametest=true
 * - TestFunctionLoader.runLoaders()
 *   -> ...
 *   -> BlockGenerationTest.registerFunctions(...)
 *   -> ...
 * - Minecraft is launched
 * - new CobbleGen()
 * - RegistryDataLoader loading its data
 *   -> Intercepted by CobbleGen's mixin -> Gated by -Dnull2264.cobblegen.gametest=true
 *   -> BlockGenerationTest.registerInstances(...)
 */
public class BlockGenerationTest {

    public static final CGIdentifier TEMPLATE = CGIdentifier.of("empty");
    public static final Integer TIMEOUT_TICKS = 120;

    #if MC>=12105
    private record TestHolder(
        CGIdentifier id,
        CGIdentifier structure,
        Integer timeoutTicks,
        Integer setupTicks,
        Boolean isRequired,
        Consumer<GameTestHelper> function
    ) {
        public GameTestInstance testInstance(
            Registry<
                TestEnvironmentDefinition
                #if MC>=260100
                <?>
                #endif
            > testEnvironmentRegistry
        ) {
            Holder.Reference<
                TestEnvironmentDefinition
                #if MC>=260100
                <?>
                #endif
            > testEnvironment = testEnvironmentRegistry.getOrThrow(GameTestEnvironments.DEFAULT_KEY);

            return new FunctionGameTestInstance(
                ResourceKey.create(Registries.TEST_FUNCTION, id.toMC()),
                new TestData<>(
                    testEnvironment,
                    structure().toMC(),
                    timeoutTicks(),
                    setupTicks(),
                    isRequired()
                )
            );
        }

        public ResourceKey<Consumer<GameTestHelper>> functionKey() {
            return ResourceKey.create(Registries.TEST_FUNCTION, id().toMC());
        }

        public void registerFunction(BiConsumer<ResourceKey<Consumer<GameTestHelper>>, Consumer<GameTestHelper>> registerer) {
            registerer.accept(functionKey(), function());
        }

        public void registerEnvironment(
            Registry<GameTestInstance> testInstances,
            Registry<
                TestEnvironmentDefinition
                #if MC>=260100
                <?>
                #endif
            > testEnvironmentRegistry
        ) {
            Registry.register(testInstances, id().toMC(), testInstance(testEnvironmentRegistry));
        }
    }

    private static final BlockGenerationTest testFunctionHolder = new BlockGenerationTest();
    private static final List<TestHolder> testHolders = listOf(
        new TestHolder(
            CGIdentifier.of("cobble_generation"),
            TEMPLATE,
            TIMEOUT_TICKS,
            0,
            true,
            testFunctionHolder::cobbleGenerationTest
        ),
        new TestHolder(
            CGIdentifier.of("cobble_generation_with_modifier"),
            TEMPLATE,
            TIMEOUT_TICKS,
            0,
            true,
            testFunctionHolder::cobbleGenerationWithModifierTest
        ),
        new TestHolder(
            CGIdentifier.of("basalt_generation"),
            TEMPLATE,
            TIMEOUT_TICKS,
            0,
            true,
            testFunctionHolder::basaltGenerationTest
        ),
        new TestHolder(
            CGIdentifier.of("basalt_generation_with_modifier"),
            TEMPLATE,
            TIMEOUT_TICKS,
            0,
            true,
            testFunctionHolder::basaltGenerationWithModifierTest
        ),
        new TestHolder(
            CGIdentifier.of("stone_generation"),
            TEMPLATE,
            TIMEOUT_TICKS,
            0,
            true,
            testFunctionHolder::stoneGenerationTest
        ),
        new TestHolder(
            CGIdentifier.of("stone_generation_with_modifier"),
            TEMPLATE,
            TIMEOUT_TICKS,
            0,
            true,
            testFunctionHolder::stoneGenerationWithModifierTest
        ),
        new TestHolder(
            CGIdentifier.of("obsidian_generation"),
            TEMPLATE,
            TIMEOUT_TICKS,
            0,
            true,
            testFunctionHolder::obsidianGenerationTest
        )/*,
        new TestHolder(
            CGIdentifier.of("obsidian_generation_with_modifier"),
            TEMPLATE,
            TIMEOUT_TICKS,
            0,
            true,
            testFunctionHolder::obsidianGenerationWithModifierTest
        )
         */
    );

    public static void registerFunctions(BiConsumer<ResourceKey<Consumer<GameTestHelper>>, Consumer<GameTestHelper>> registerer) {
        testHolders.forEach(holder -> holder.registerFunction(registerer));
    }

    @SuppressWarnings("unchecked")
    public static void registerInstances(
        #if MC>=260100
        List<net.minecraft.resources.RegistryLoadTask<?>> registriesList
        #else
        List<RegistryDataLoader.Loader<?>> registriesList
        #endif
    ) {
        Map<ResourceKey<? extends Registry<?>>, Registry<?>> registries = new IdentityHashMap<>(registriesList.size());

        #if MC>=260100
        for (net.minecraft.resources.RegistryLoadTask<?> entry : registriesList) {
        #else
        for (RegistryDataLoader.Loader<?> entry : registriesList) {
        #endif
            registries.put(
                entry.registry
                #if MC<260100
                ()
                #endif
                .key(),
                entry.registry
                #if MC<260100
                ()
                #endif
            );
        }

        Registry<GameTestInstance> testInstances =
            (Registry<GameTestInstance>) registries.get(Registries.TEST_INSTANCE);
        Registry<
            TestEnvironmentDefinition
            #if MC>=260100
            <?>
            #endif
        > testEnvironmentRegistry =
            (Registry<
                TestEnvironmentDefinition
                    #if MC>=260100
                    <?>
                    #endif
                >) Objects.requireNonNull(registries.get(Registries.TEST_ENVIRONMENT));

        testHolders.forEach(holder -> holder.registerEnvironment(testInstances, testEnvironmentRegistry));
    }
    #endif

    // Basically telling Forge to stop being weird
    @PrefixGameTestTemplate(false)
    @net.minecraft.gametest.framework.GameTest(
        #if FORGE
        templateNamespace = "cobblegen",
        template = "empty",
        #else
        template = "cobblegen:empty",
        #endif
        timeoutTicks = 120
    )
    public void cobbleGenerationTest(
        GameTestHelper
            #if MC<=11605
            ctx
            #else
            context
            #endif
    ) {
        #if MC<=11605
        GameTestHelperLegacy$GameTest context =
            (GameTestHelperLegacy$GameTest) ctx;
        #endif
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
                context.assertBlockPresent(Blocks.BARRIER, generatedPos);
            }
        );
    }

    @PrefixGameTestTemplate(false)
    @net.minecraft.gametest.framework.GameTest(
        #if FORGE
        templateNamespace = "cobblegen",
        template = "empty",
        #else
        template = "cobblegen:empty",
        #endif
        timeoutTicks = 120
    )
    public void cobbleGenerationWithModifierTest(
        GameTestHelper
            #if MC<=11605
            ctx
            #else
            context
            #endif
    ) {
        #if MC<=11605
        GameTestHelperLegacy$GameTest context =
            (GameTestHelperLegacy$GameTest) ctx;
        #endif
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
        context.setBlock(new BlockPos(1, 1, 3), Blocks.BEDROCK);  // Modifier block
        BlockPos generatedPos = new BlockPos(1, 2, 3);
        context.succeedWhen(
            () -> {
                context.assertBlockPresent(Blocks.BEDROCK, generatedPos);
            }
        );
    }

    @PrefixGameTestTemplate(false)
    @net.minecraft.gametest.framework.GameTest(
        #if FORGE
        templateNamespace = "cobblegen",
        template = "empty",
        #else
        template = "cobblegen:empty",
        #endif
        timeoutTicks = 120
    )
    public void basaltGenerationTest(
        GameTestHelper
            #if MC<=11605
            ctx
            #else
            context
            #endif
    ) {
        #if MC<=11605
        GameTestHelperLegacy$GameTest context =
            (GameTestHelperLegacy$GameTest) ctx;
        #endif
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
                context.assertBlockPresent(Blocks.BARRIER, generatedPos);
            }
        );
    }

    @PrefixGameTestTemplate(false)
    @net.minecraft.gametest.framework.GameTest(
        #if FORGE
        templateNamespace = "cobblegen",
        template = "empty",
        #else
        template = "cobblegen:empty",
        #endif
        timeoutTicks = 120
    )
    public void basaltGenerationWithModifierTest(
        GameTestHelper
            #if MC<=11605
            ctx
            #else
            context
            #endif
    ) {
        #if MC<=11605
        GameTestHelperLegacy$GameTest context =
            (GameTestHelperLegacy$GameTest) ctx;
        #endif
        context.setBlock(new BlockPos(1, 2, 2), Blocks.BLUE_ICE);
        // << Barrier wrapping the lava
        context.setBlock(new BlockPos(2, 2, 4), Blocks.BARRIER);
        context.setBlock(new BlockPos(0, 2, 4), Blocks.BARRIER);
        context.setBlock(new BlockPos(1, 2, 5), Blocks.BARRIER);
        // >>
        context.setBlock(new BlockPos(1, 1, 4), Blocks.BARRIER);  // Barrier under lava
        context.setBlock(new BlockPos(1, 2, 4), Blocks.LAVA);
        context.setBlock(new BlockPos(1, 1, 3), Blocks.BEDROCK);  // For custom basalt generators
        BlockPos generatedPos = new BlockPos(1, 2, 3);
        context.succeedWhen(
            () -> {
                context.assertBlockPresent(Blocks.BEDROCK, generatedPos);
            }
        );
    }

    @PrefixGameTestTemplate(false)
    @net.minecraft.gametest.framework.GameTest(
        #if FORGE
        templateNamespace = "cobblegen",
        template = "empty",
        #else
        template = "cobblegen:empty",
        #endif
        timeoutTicks = 120
    )
    public void stoneGenerationTest(
        GameTestHelper
            #if MC<=11605
            ctx
            #else
            context
            #endif
    ) {
        #if MC<=11605
        GameTestHelperLegacy$GameTest context =
            (GameTestHelperLegacy$GameTest) ctx;
        #endif
        // << Barrier wrapping the lava
        context.setBlock(new BlockPos(1, 2, 0), Blocks.BARRIER);
        context.setBlock(new BlockPos(0, 2, 1), Blocks.BARRIER);
        context.setBlock(new BlockPos(1, 2, 2), Blocks.BARRIER);
        context.setBlock(new BlockPos(2, 2, 1), Blocks.BARRIER);

        context.setBlock(new BlockPos(1, 1, 0), Blocks.BARRIER);
        context.setBlock(new BlockPos(0, 1, 1), Blocks.BARRIER);
        context.setBlock(new BlockPos(1, 1, 2), Blocks.BARRIER);
        context.setBlock(new BlockPos(2, 1, 1), Blocks.BARRIER);
        // >>
        context.setBlock(new BlockPos(1, 2, 1), Blocks.LAVA);
        // << Barrier wrapping the water
        context.setBlock(new BlockPos(0, 1, 2), Blocks.BARRIER);
        context.setBlock(new BlockPos(1, 1, 3), Blocks.BARRIER);
        context.setBlock(new BlockPos(2, 1, 2), Blocks.BARRIER);
        // >>
        context.setBlock(new BlockPos(1, 0, 2), Blocks.BARRIER);  // Barrier under the water
        context.setBlock(new BlockPos(1, 1, 2), Blocks.WATER);
        context.setBlock(new BlockPos(1, 0, 1), Blocks.BARRIER);  // Barrier under the generated block
        BlockPos generatedPos = new BlockPos(1, 1, 1);
        context.succeedWhen(
            () -> {
                context.assertBlockPresent(Blocks.BARRIER, generatedPos);
            }
        );
    }

    @PrefixGameTestTemplate(false)
    @net.minecraft.gametest.framework.GameTest(
        #if FORGE
        templateNamespace = "cobblegen",
        template = "empty",
        #else
        template = "cobblegen:empty",
        #endif
        timeoutTicks = 120
    )
    public void stoneGenerationWithModifierTest(
        GameTestHelper
            #if MC<=11605
            ctx
            #else
            context
            #endif
    ) {
        #if MC<=11605
        GameTestHelperLegacy$GameTest context =
            (GameTestHelperLegacy$GameTest) ctx;
        #endif
        // << Barrier wrapping the lava
        context.setBlock(new BlockPos(1, 2, 0), Blocks.BARRIER);
        context.setBlock(new BlockPos(0, 2, 1), Blocks.BARRIER);
        context.setBlock(new BlockPos(1, 2, 2), Blocks.BARRIER);
        context.setBlock(new BlockPos(2, 2, 1), Blocks.BARRIER);

        context.setBlock(new BlockPos(1, 1, 0), Blocks.BARRIER);
        context.setBlock(new BlockPos(0, 1, 1), Blocks.BARRIER);
        context.setBlock(new BlockPos(1, 1, 2), Blocks.BARRIER);
        context.setBlock(new BlockPos(2, 1, 1), Blocks.BARRIER);
        // >>
        context.setBlock(new BlockPos(1, 2, 1), Blocks.LAVA);
        // << Barrier wrapping the water
        context.setBlock(new BlockPos(0, 1, 2), Blocks.BARRIER);
        context.setBlock(new BlockPos(1, 1, 3), Blocks.BARRIER);
        context.setBlock(new BlockPos(2, 1, 2), Blocks.BARRIER);
        // >>
        context.setBlock(new BlockPos(1, 0, 2), Blocks.BARRIER);  // Barrier under the water
        context.setBlock(new BlockPos(1, 1, 2), Blocks.WATER);
        context.setBlock(new BlockPos(1, 0, 1), Blocks.BEDROCK);  // Barrier under the generated block
        BlockPos generatedPos = new BlockPos(1, 1, 1);
        context.succeedWhen(
            () -> {
                context.assertBlockPresent(Blocks.BEDROCK, generatedPos);
            }
        );
    }

    @PrefixGameTestTemplate(false)
    @net.minecraft.gametest.framework.GameTest(
        #if FORGE
        templateNamespace = "cobblegen",
        template = "empty",
        #else
        template = "cobblegen:empty",
        #endif
        timeoutTicks = 120
    )
    public void obsidianGenerationTest(
        GameTestHelper
            #if MC<=11605
            ctx
            #else
            context
            #endif
    ) {
        #if MC<=11605
        GameTestHelperLegacy$GameTest context =
            (GameTestHelperLegacy$GameTest) ctx;
        #endif
        context.setBlock(new BlockPos(1, 3, 3), Blocks.WATER);
        context.setBlock(new BlockPos(1, 2, 3), Blocks.LAVA);
        context.setBlock(new BlockPos(1, 1, 3), Blocks.BARRIER);  // Barrier under the generated block
        BlockPos generatedPos = new BlockPos(1, 2, 3);
        context.succeedWhen(
            () -> {
                context.assertBlockPresent(Blocks.SAND, generatedPos);
            }
        );
    }

    /*
    @PrefixGameTestTemplate(false)
    @net.minecraft.gametest.framework.GameTest(
        #if FORGE
        templateNamespace = "cobblegen",
        template = "empty",
        #else
        template = "cobblegen:empty",
        #endif
        timeoutTicks = 120
    )
    public void obsidianGenerationWithModifierTest(GameTestHelper context) {
        context.setBlock(new BlockPos(1, 3, 3), Blocks.WATER);
        context.setBlock(new BlockPos(1, 2, 3), Blocks.LAVA);
        context.setBlock(new BlockPos(1, 1, 3), Blocks.GLASS);  // Barrier under the generated block
        BlockPos generatedPos = new BlockPos(1, 2, 3);
        context.succeedWhen(
            () -> {
                context.assertBlockPresent(Blocks.GLASS, generatedPos);
            }
        );
    }
     */
}
