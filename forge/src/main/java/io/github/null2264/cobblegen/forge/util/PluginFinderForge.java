package io.github.null2264.cobblegen.forge.util;

import io.github.null2264.cobblegen.CGPlugin;
import io.github.null2264.cobblegen.CobbleGenPlugin;
import io.github.null2264.cobblegen.util.CGLog;
import io.github.null2264.cobblegen.util.PluginFinder;
import java.lang.reflect.Constructor;
import org.objectweb.asm.Type;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static io.github.null2264.cobblegen.compat.CollectionCompat.streamToList;

#if MC>=12002
import net.neoforged.fml.ModList;
import net.neoforged.neoforgespi.language.IModInfo;
import net.neoforged.neoforgespi.language.ModFileScanData;
#else
import net.minecraftforge.fml.ModList;
import net.minecraftforge.forgespi.language.IModInfo;
import net.minecraftforge.forgespi.language.ModFileScanData;
#endif

public class PluginFinderForge extends PluginFinder {

    @Override
    public List<PlugInContainer> getModPlugins() {
        return AnnotatedFinder.getInstances(CGPlugin.class, CobbleGenPlugin.class);
    }

    static class AnnotatedFinder {
        public static <T> List<PlugInContainer> getInstances(Class<?> annotationClass, Class<T> instanceClass) {
            Type annotationType = Type.getType(annotationClass);
            List<ModFileScanData> allScanData = ModList.get().getAllScanData();
            List<PlugInContainer> instances = new ArrayList<>();
            for (ModFileScanData data : allScanData) {
                List<String> modIds = streamToList(
                    data.getIModInfoData().stream()
                        .flatMap(info -> info.getMods().stream())
                        .map(IModInfo::getModId)
                );
                String modId = "[" + String.join(", ", modIds) + "]";

                Iterable<ModFileScanData.AnnotationData> annotations = data.getAnnotations();
                for (ModFileScanData.AnnotationData a : annotations) {
                    if (!(Objects.equals(
                            #if MC>11605
                        a.annotationType(),
                            #else
                            a.getAnnotationType(),
                            #endif
                        annotationType)))
                        continue;

                    String className =
                            #if MC>11605
                        a.memberName();
                            #else
                            a.getMemberName();
                            #endif
                    try {
                        Class<?> asmClass = Class.forName(className);
                        Class<? extends T> asmInstanceClass = asmClass.asSubclass(instanceClass);
                        Constructor<? extends T> constructor = asmInstanceClass.getDeclaredConstructor();
                        T instance = constructor.newInstance();
                        instances.add(new PlugInContainer(modId, (CobbleGenPlugin) instance));
                    } catch (Throwable t) {
                        CGLog.error("Failed to load: " + className + " ", t);
                    }
                }
            }
            return instances;
        }
    }
}
