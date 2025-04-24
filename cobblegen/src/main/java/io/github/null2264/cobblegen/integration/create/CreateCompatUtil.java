package io.github.null2264.cobblegen.integration.create;

import io.github.null2264.cobblegen.compat.ModContainerCompat;
import io.github.null2264.cobblegen.data.config.ConfigHolder;
import io.github.null2264.cobblegen.data.config.ConfigMetaData;
import io.github.null2264.cobblegen.util.CGLog;
import org.jetbrains.annotations.ApiStatus;

import java.util.ArrayList;
import java.util.regex.PatternSyntaxException;

public class CreateCompatUtil {

    /**
     * Check Create mod version. Returns integer, in case Create introduced yet another breaking change
     */
    @ApiStatus.Internal
    public static CreateSupport getCreateSupport() {
        if (!ConfigHolder.META.create.loadIntegration) return CreateSupport.NONE;

        #if MC<=11605
        return CreateSupport.NONE;  // We don't support create integration for MC1.16.5
        #else
        try {
            String version = ModContainerCompat.fromLoader("create").getVersionString();
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
            if (split.get(0) == 0) {  // 0.x.x
                #if FORGE
                return CreateSupport.O_FIVE_ONE_E;  // Always use Patch E mixin for Forge-alike
                #else
                if (split.get(1) == 5 && split.get(2) == 0 || split.get(1) < 5) return CreateSupport.O_FIVE_ONE_E;
                if (split.get(1) > 5 || split.get(2) > 1) return CreateSupport.O_FIVE_ONE_F;  // Assume they don't introduce breaking changes on version bump

                int compare = patch.compareToIgnoreCase("e");
                if (compare <= 0) return CreateSupport.O_FIVE_ONE_E;
                return CreateSupport.O_FIVE_ONE_F;
                #endif
            } else if (split.get(0) == 6) {  // 6.x.x
                #if FORGE
                return CreateSupport.O_FIVE_ONE_E;
                #else
                return CreateSupport.O_FIVE_ONE_F;
                #endif
            }
            // They're definitely going to introduce breaking changes on major version bump
            return CreateSupport.NONE;
        } catch (java.util.NoSuchElementException | PatternSyntaxException | NumberFormatException exc) {
            CGLog.error(exc);
            return CreateSupport.NONE;
        }
        #endif
    }
}
