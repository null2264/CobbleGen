#if MC>=12105
package io.github.null2264.cobblegen.mixin.gametest;

#if MC>=260100
import net.minecraft.core.Registry;
import net.minecraft.gametest.framework.GameTestInstance;
import net.minecraft.gametest.framework.TestEnvironmentDefinition;
import net.minecraft.resources.ResourceKey;
#endif

import io.github.null2264.cobblegen.gametest.BlockGenerationTest;
import io.github.null2264.cobblegen.util.CGLog;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.RegistryDataLoader;
import net.minecraft.server.packs.resources.ResourceManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

@Mixin(RegistryDataLoader.class)
public abstract class RegistryDataLoaderMixin$GameTest {
    @Unique
    private static final AtomicBoolean LOADING_DYNAMIC_REGISTRIES = new AtomicBoolean(false);
    @Unique
    private static final AtomicBoolean SHOULD_LOAD_TESTS = new AtomicBoolean(true);

    @Inject(
        #if MC>=260100
        method = "load(Lnet/minecraft/server/packs/resources/ResourceManager;Ljava/util/List;Ljava/util/List;Ljava/util/concurrent/Executor;)Ljava/util/concurrent/CompletableFuture;",
        #else
        method = "load(Lnet/minecraft/server/packs/resources/ResourceManager;Ljava/util/List;Ljava/util/List;)Lnet/minecraft/core/RegistryAccess$Frozen;",
        #endif
        at = @At("HEAD")
    )
    private static void loadFromResources(
        ResourceManager resourceManager,
        List<HolderLookup.RegistryLookup<?>> registries,
        List<RegistryDataLoader.RegistryData<?>> entries,
        #if MC>=260100
        java.util.concurrent.Executor executor,
        #endif
        CallbackInfoReturnable<RegistryAccess.Frozen> cir
    ) {
        LOADING_DYNAMIC_REGISTRIES.set(entries.stream().anyMatch(entry -> entry.key() == Registries.TEST_INSTANCE));
    }

    @Inject(
        #if MC>=260100
        #if FABRIC
        method = "lambda$load$2(Ljava/util/List;Ljava/util/Map;Ljava/lang/Void;)Lnet/minecraft/core/RegistryAccess$Frozen;",
        #else
        method = "lambda$load$2(ZLjava/util/List;Ljava/util/Map;Ljava/lang/Void;)Lnet/minecraft/core/RegistryAccess$Frozen;",
        #endif
        #else
        #if FABRIC
        method = "load(Lnet/minecraft/resources/RegistryDataLoader$LoadingFunction;Ljava/util/List;Ljava/util/List;)Lnet/minecraft/core/RegistryAccess$Frozen;",
        #else
        method = "load(Lnet/minecraft/resources/RegistryDataLoader$LoadingFunction;Ljava/util/List;Ljava/util/List;Z)Lnet/minecraft/core/RegistryAccess$Frozen;",
        #endif
        #endif
        at = @At(
            #if MC>=260100
            value = "HEAD"
            #else
            value = "INVOKE",
            target = "Ljava/util/List;forEach(Ljava/util/function/Consumer;)V",
            ordinal = 1
            #endif
        )
    )
    private static void beforeFreeze(
        #if MC>=260100
        #if FORGE
        boolean fromResources,
        #endif
        List<net.minecraft.resources.RegistryLoadTask<?>> registriesList,
        Map<net.minecraft.resources.ResourceKey<?>, Exception> loadingErrors,
        Void ignored,
        CallbackInfoReturnable<RegistryAccess.Frozen> cir
        #else
        @org.spongepowered.asm.mixin.injection.Coerce Object loadable,
        List<HolderLookup.RegistryLookup<?>> wrappers,
        List<RegistryDataLoader.RegistryData<?>> entries,
        #if FORGE
        boolean fromResources,
        #endif
        CallbackInfoReturnable<RegistryAccess.Frozen> cir,
        @com.llamalad7.mixinextras.sugar.Local(ordinal = 2)
        List<RegistryDataLoader.Loader<?>> registriesList
        #endif
    ) {
        if (!SHOULD_LOAD_TESTS.getAndSet(false)) return;

        CGLog.debug("Registering test instances...");

        if (LOADING_DYNAMIC_REGISTRIES.getAndSet(false)) {
            CGLog.debug("Registering test instances using first method...");
            BlockGenerationTest.registerInstances(registriesList);
            return;
        }

        #if MC>=260100
        Map<? extends ResourceKey<? extends Registry<?>>, ? extends Registry<?>> byKeyMap = registriesList.stream().collect(Collectors.toMap((d) -> d.registry.key(), (d) -> d.registry));
        Registry<GameTestInstance> testsRegistry = (Registry<GameTestInstance>) byKeyMap.get(Registries.TEST_INSTANCE);
        Registry<TestEnvironmentDefinition<?>> testEnvironmentRegistry = (Registry<TestEnvironmentDefinition<?>>) byKeyMap.get(Registries.TEST_ENVIRONMENT);

        if (!LOADING_DYNAMIC_REGISTRIES.getAndSet(false) && testsRegistry != null && testEnvironmentRegistry != null) {
            CGLog.debug("Registering test instances using second method...");
            BlockGenerationTest.registerInstances(testsRegistry, testEnvironmentRegistry);
        }
        #endif
    }
}
#endif
