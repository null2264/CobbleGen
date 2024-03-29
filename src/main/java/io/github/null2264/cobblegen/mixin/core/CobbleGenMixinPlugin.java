package io.github.null2264.cobblegen.mixin.core;

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

    /**
     * Checks if loaded Create mod is patch F or newer
     * @return 1 for Patch F, 0 for not Patch E, -1 for always Patch E
     */
    private int isPatchFOrNewer() {
        //#if FABRIC<=0
        //$$ return -1;  // Always use Patch E mixin for Forge-alike
        //#else
        //#if MC>1.16.5
        try {
            String version =
                net.fabricmc.loader.api.FabricLoader.getInstance().getModContainer("create")
                    .orElseThrow().getMetadata().getVersion().getFriendlyString();
            if (version.startsWith("0.5.1-f") || version.startsWith("0.5.1.f")) return 1;
        } catch (java.util.NoSuchElementException exc) {
            return 0;
        }
        return 0;
        //#else
        //$$ return 0;  // We don't support create integration for MC1.16.5
        //#endif
        //#endif
    }

    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
        if (mixinClassName.contains("CreateFluidReactionsMixin")) {
            if (!LoaderCompat.isModLoaded("create")) return false;

            if (mixinClassName.endsWith("PatchF")) return isPatchFOrNewer() >= 1;
            if (mixinClassName.endsWith("PatchE")) return isPatchFOrNewer() <= 0;
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
