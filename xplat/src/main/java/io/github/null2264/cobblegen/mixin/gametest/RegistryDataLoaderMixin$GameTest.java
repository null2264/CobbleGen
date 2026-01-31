#if MC>=12105
package io.github.null2264.cobblegen.mixin.gametest;

import com.llamalad7.mixinextras.sugar.Local;
import io.github.null2264.cobblegen.gametest.BlockGenerationTest;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.RegistryDataLoader;
import net.minecraft.server.packs.resources.ResourceManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Coerce;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

@Mixin(RegistryDataLoader.class)
public abstract class RegistryDataLoaderMixin$GameTest {
    @Unique
    private static final AtomicBoolean LOADING_DYNAMIC_REGISTRIES = new AtomicBoolean(false);

    @Inject(method = "load(Lnet/minecraft/server/packs/resources/ResourceManager;Ljava/util/List;Ljava/util/List;)Lnet/minecraft/core/RegistryAccess$Frozen;", at = @At("HEAD"))
    private static void loadFromResources(ResourceManager resourceManager, List<HolderLookup.RegistryLookup<?>> registries, List<RegistryDataLoader.RegistryData<?>> entries, CallbackInfoReturnable<RegistryAccess.Frozen> cir) {
        LOADING_DYNAMIC_REGISTRIES.set(entries.stream().anyMatch(entry -> entry.key() == Registries.TEST_INSTANCE));
    }

    @Inject(
        #if FABRIC
        method = "load(Lnet/minecraft/resources/RegistryDataLoader$LoadingFunction;Ljava/util/List;Ljava/util/List;)Lnet/minecraft/core/RegistryAccess$Frozen;",
        #else
        method = "load(Lnet/minecraft/resources/RegistryDataLoader$LoadingFunction;Ljava/util/List;Ljava/util/List;Z)Lnet/minecraft/core/RegistryAccess$Frozen;",
        #endif
        at = @At(
            value = "INVOKE",
            target = "Ljava/util/List;forEach(Ljava/util/function/Consumer;)V",
            ordinal = 1
        )
    )
    private static void beforeFreeze(
        @Coerce Object loadable,
        List<HolderLookup.RegistryLookup<?>> wrappers,
        List<RegistryDataLoader.RegistryData<?>> entries,
        #if FORGE
        boolean fromResources,
        #endif
        CallbackInfoReturnable<RegistryAccess.Frozen> cir,
        @Local(ordinal = 2) List<RegistryDataLoader.Loader<?>> registriesList
    ) {
        if (LOADING_DYNAMIC_REGISTRIES.getAndSet(false)) {
            BlockGenerationTest.registerInstances(registriesList);
        }
    }
}
#endif
