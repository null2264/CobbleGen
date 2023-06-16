package io.github.null2264.cobblegen.util;

import io.github.null2264.cobblegen.CobbleGenPlugin;
import lombok.Data;
import org.jetbrains.annotations.ApiStatus;

import java.util.*;

import static io.github.null2264.cobblegen.CobbleGen.MOD_ID;

//#if FABRIC>=1
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.entrypoint.EntrypointContainer;
//#else
//$$ import java.lang.reflect.Constructor;
//$$ import io.github.null2264.cobblegen.CGPlugin;
//$$ import lombok.val;
//$$ import net.minecraftforge.fml.ModList;
//$$ import net.minecraftforge.forgespi.language.ModFileScanData;
//$$ import org.objectweb.asm.Type;
//#endif

public class PluginFinder
{
    public static List<PlugInContainer> getModPlugins() {
        //#if FABRIC>=1
        return FabricLoader.getInstance()
                .getEntrypointContainers("cobblegen_plugin", CobbleGenPlugin.class)
                .stream()
                .sorted(Comparator.comparingInt(PluginFinder::priorityEntrypoint))
                .map(entrypoint -> new PlugInContainer(entrypoint.getProvider().getMetadata().getId(), entrypoint.getEntrypoint()))
                .toList();
        //#else
        //$$ val plugins = AnnotatedFinder.getInstances(CGPlugin.class, CobbleGenPlugin.class);
        //$$ List<PlugInContainer> rt = new ArrayList<>();
        //$$ for (val plugin : plugins) {
        //$$     rt.add(new PlugInContainer("Placeholder", plugin));
        //$$ }
        //$$ return rt;
        //#endif
    }

    //#if FABRIC>=1
    private static int priorityEntrypoint(EntrypointContainer<CobbleGenPlugin> plugin) {
        return plugin.getProvider().getMetadata().getId().equals(MOD_ID) ? 0 : 1;
    }
    //#else
    //#endif

    //#if FABRIC<=0
    //$$ public class AnnotatedFinder {
    //$$     public static <T> List<T> getInstances(Class<?> annotationClass, Class<T> instanceClass) {
    //$$         Type annotationType = Type.getType(annotationClass);
    //$$         List<ModFileScanData> allScanData = ModList.get().getAllScanData();
    //$$         Set<String> pluginClassNames = new LinkedHashSet<>();
    //$$         for (ModFileScanData scanData : allScanData) {
    //$$             Iterable<ModFileScanData.AnnotationData> annotations = scanData.getAnnotations();
    //$$             for (ModFileScanData.AnnotationData a : annotations) {
    //$$                 if (Objects.equals(a.annotationType(), annotationType)) {
    //$$                     String memberName = a.memberName();
    //$$                     pluginClassNames.add(memberName);
    //$$                 }
    //$$             }
    //$$         }
    //$$         List<T> instances = new ArrayList<>();
    //$$         for (String className : pluginClassNames) {
    //$$             try {
    //$$                 Class<?> asmClass = Class.forName(className);
    //$$                 Class<? extends T> asmInstanceClass = asmClass.asSubclass(instanceClass);
    //$$                 Constructor<? extends T> constructor = asmInstanceClass.getDeclaredConstructor();
    //$$                 T instance = constructor.newInstance();
    //$$                 instances.add(instance);
    //$$             } catch (ReflectiveOperationException | LinkageError e) {
    //$$                 CGLog.error("Failed to load: ", className, e.getMessage());
    //$$             }
    //$$         }
    //$$         return instances;
    //$$     }
    //$$ }
    //#endif

    @Data
    @ApiStatus.Internal
    public static class PlugInContainer {
        final String modId;
        final CobbleGenPlugin plugin;
    }
}