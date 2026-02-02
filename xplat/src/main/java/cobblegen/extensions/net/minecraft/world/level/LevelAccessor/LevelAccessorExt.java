#if FORGE && MC<12002
package xplat.extensions.net.minecraft.world.level.LevelAccessor;

import net.minecraft.core.RegistryAccess;
import net.minecraft.world.level.LevelAccessor;

@manifold.ext.rt.api.Extension
public final class LevelAccessorExt {

    public static RegistryAccess registryAccessCompat(@manifold.ext.rt.api.This LevelAccessor thiz) {
        RegistryAccess access = null;
        // Pre-runtime-mojmap forge pain (which introduced on NeoForge around MC 1.20.2 and Forge around 1.20.6, be it partially)
        String func = "";
        String version = getVersion();
        // SRG moment
        switch (version) {
            case "1.19.3":
                func = "m_8891_"; break;
            case "1.19.4":
                func = "m_9598_"; break;
            default:  // Leave it empty
                break;
        }

        if (!func.isEmpty()) {
            java.lang.reflect.Method method;
            try {
                method = thiz.getClass().getMethod(func);
                access = (RegistryAccess) method.invoke(thiz);
            } catch (NoSuchMethodException | java.lang.reflect.InvocationTargetException | IllegalAccessException ignored) {
            }
        }

        // Fallback
        if (access == null) {
            access = thiz.registryAccess();
        }
        return access;
    }

    private static String getVersion() {
        net.minecraft. @manifold.ext.rt.api.Jailbreak DetectedVersion mcVersion =
            new net.minecraft. @manifold.ext.rt.api.Jailbreak DetectedVersion();
        String version;
        try {
            java.lang.reflect.Field field = mcVersion.getClass().getDeclaredField("f_132479_");
            field.setAccessible(true);
            version = (String) field.get(mcVersion);
        } catch (NoSuchFieldException | IllegalAccessException ignored) {
            // Fallback
            version = mcVersion.getName();
        }
        return version;
    }
}
#endif
