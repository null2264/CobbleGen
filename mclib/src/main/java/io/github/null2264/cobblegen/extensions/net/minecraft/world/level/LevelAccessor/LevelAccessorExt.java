package io.github.null2264.cobblegen.extensions.net.minecraft.world.level.LevelAccessor;

import manifold.ext.rt.api.Extension;
import manifold.ext.rt.api.This;
import net.minecraft.core.RegistryAccess;
import net.minecraft.world.level.LevelAccessor;

@Extension
public final class LevelAccessorExt {
    public static RegistryAccess registryAccessCompat(@This LevelAccessor thiz) {
        //noinspection UnusedAssignment
        RegistryAccess access = null;
        #if FABRIC || (FORGE && !(MC>11902 && MC<=11904))
        access = thiz.registryAccess();
        #else
        // Pre-runtime-mojmap forge pain
        String func = "";
        net.minecraft. @manifold.ext.rt.api.Jailbreak DetectedVersion mcVersion =
            new net.minecraft. @manifold.ext.rt.api.Jailbreak DetectedVersion();
        String version = "";
        try {
            java.lang.reflect.Field field = mcVersion.getClass().getDeclaredField("f_132479_");
            field.setAccessible(true);
            version = (String) field.get(mcVersion);
        } catch (NoSuchFieldException | IllegalAccessException ignored) {
            // Fallback
            version = mcVersion.getName();
        }
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
        #endif
        return access;
    }
}
