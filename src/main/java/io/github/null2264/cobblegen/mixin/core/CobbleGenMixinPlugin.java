package io.github.null2264.cobblegen.mixin.core;

import io.github.null2264.cobblegen.CobbleGen;
import io.github.null2264.cobblegen.compat.LoaderCompat;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import java.util.List;
import java.util.Set;

public class CobbleGenMixinPlugin implements IMixinConfigPlugin
{
    @Override
    public void onLoad(String mixinPackage) {

    }

    @Override
    public String getRefMapperConfig() {
        return null;
    }

    private boolean isPatchFOrNewer() {
        //#if FABRIC<=0
        //$$ return false;
        //#else
        try {
            String version =
                net.fabricmc.loader.api.FabricLoader.getInstance().getModContainer("create")
                    .orElseThrow().getMetadata().getVersion().getFriendlyString();
            return version.startsWith("0.5.1-f") || version.startsWith("0.5.1.f");
        } catch (java.util.NoSuchElementException exc) {
            return false;
        }
        //#endif
    }

    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
        if (mixinClassName.startsWith("CreateFluidReactionsMixin")) {
            if (!LoaderCompat.isModLoaded("create")) return false;

            if (mixinClassName.endsWith("PatchF")) return isPatchFOrNewer();
            if (mixinClassName.endsWith("PatchE")) return !isPatchFOrNewer();

            return !CobbleGen.META_CONFIG.create.disablePipe;
        }
        return true;
    }

    @Override
    public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {

    }

    @Override
    public List<String> getMixins() {
        return null;
    }

    @Override
    public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {

    }

    @Override
    public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {

    }
}