package io.github.null2264.cobblegen.mixin.core;

import io.github.null2264.cobblegen.compat.LoaderCompat;
import io.github.null2264.cobblegen.util.CGLog;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.regex.PatternSyntaxException;

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
     * Check Create mod version. Returns integer, in case Create introduced yet another breaking change
     *
     * @return -1 for Unsupported, 0 for Patch E or older, 1 for Patch F or newer
     */
    private int getPatchVersion() {
        //#if FABRIC<=0
        //$$ return 0;  // Always use Patch E mixin for Forge-alike
        //#else
        //#if MC>1.16.5
        try {
            String version =
                net.fabricmc.loader.api.FabricLoader.getInstance().getModContainer("create")
                    .orElseThrow().getMetadata().getVersion().getFriendlyString();
            ArrayList<Integer> split = new ArrayList<>();
            String patch = "a";
            // version = 0.5.1-a-build.69
            for (String s : version.split("\\.")) {
                try {
                    // 0.5.
                    split.add(Integer.valueOf(s));
                } catch (NumberFormatException exc) {
                    // 1a or 1-a-build -> 1
                    split.add(Integer.valueOf(s.substring(0, 1)));
                    // -a-build -> '', a, build | a-build -> a, build
                    String[] patchSplit = s.substring(1).split("-");
                    if (patchSplit[0].equalsIgnoreCase("")) {
                        patch = patchSplit[1];
                    } else {
                        // For older version that use 0.5.0a format instead of 0.5.0-a
                        patch = patchSplit[0];
                    }
                    break;
                }
            }
            if (split.get(0) == 0) {
                if (split.get(1) == 5 && split.get(2) == 0 || split.get(1) < 5) return 0;
                if (split.get(1) > 5 || split.get(2) > 1) return 1;  // Assume they don't introduce breaking changes on version bump

                int compare = patch.compareToIgnoreCase("e");
                if (compare <= 0) return 0;
                return 1;
            }
            // They're definitely going to introduce breaking changes on major version bump
            return -1;
        } catch (java.util.NoSuchElementException | PatternSyntaxException | NumberFormatException exc) {
            CGLog.error(exc);
            return -1;
        }
        //#else
        //$$ return 0;  // We don't support create integration for MC1.16.5
        //#endif
        //#endif
    }

    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
        if (mixinClassName.contains("CreateFluidReactionsMixin")) {
            if (!LoaderCompat.isModLoaded("create")) return false;

            int patchVersion = getPatchVersion();
            if (patchVersion == -1) return false;
            if (mixinClassName.endsWith("PatchF")) return patchVersion >= 1;
            if (mixinClassName.endsWith("PatchE")) return patchVersion <= 0;
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
