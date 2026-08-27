package io.github.null2264.cobblegen.mixin.core;

import io.github.null2264.cobblegen.CobbleGen;
import io.github.null2264.cobblegen.compat.LoaderCompat;
import io.github.null2264.cobblegen.integration.create.CreateCompatUtil;
import io.github.null2264.cobblegen.integration.create.CreateSupport;
import io.github.null2264.cobblegen.util.CGLog;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.regex.PatternSyntaxException;

public class CobbleGenMixinPlugin implements IMixinConfigPlugin {

    @Override
    public void onLoad(String mixinPackage) {
        #if FORGE && MC>=12105
        // This is the earliest entrypoint for Forge that I know of...
        io.github.null2264.cobblegen.gametest.CobbleGenTestLoader.init();
        #endif
    }

    @Override
    public String getRefMapperConfig() {
        return null;
    }

    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
        if (mixinClassName.contains("CreateFluidReactionsMixin")) {
            if (!LoaderCompat.isModLoaded("create")) return false;

            CreateSupport createSupport = CreateCompatUtil.getCreateSupport();
            if (createSupport == CreateSupport.NONE) return false;
            if (mixinClassName.endsWith("$OFive"))
                return createSupport == CreateSupport.O_FIVE_ONE_F || createSupport == CreateSupport.O_FIVE_ONE_E;
            if (mixinClassName.endsWith("$PatchE")) return createSupport == CreateSupport.O_FIVE_ONE_E;
            if (mixinClassName.endsWith("$PatchF")) return createSupport == CreateSupport.O_FIVE_ONE_F;
        }

        #if MC>=12105
        if (mixinClassName.endsWith("$GameTest") && io.github.null2264.cobblegen.gametest.Constants.IS_GAMETEST_ENABLED) {
            // Datapack will not register automatically in Fabric without FAPI.
            // I usually prefer not depending on FAPI, but I'll make this one an exception...
            // because I ain't dealing with Resource Pack loading ever again
            if (LoaderCompat.isFabricLike() && !(LoaderCompat.isModLoaded("fabric-resource-loader-v0") || LoaderCompat.isModLoaded("fabric-resource-loader-v1"))) {
                CGLog.warn(() -> "Fabric API is required to load CobbleGen's GameTests!");
                return false;
            }
            return true;
        }
        #endif
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
