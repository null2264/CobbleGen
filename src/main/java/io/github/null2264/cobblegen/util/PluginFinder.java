package io.github.null2264.cobblegen.util;

import io.github.null2264.cobblegen.CobbleGenPlugin;
import lombok.Data;
import org.jetbrains.annotations.ApiStatus;

import java.util.*;

//#if FABRIC>=1
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.entrypoint.EntrypointContainer;
//#else
//$$ import java.lang.reflect.Constructor;
//$$ import io.github.null2264.cobblegen.CGPlugin;
//$$ import net.minecraftforge.fml.ModList;
//$$ import net.minecraftforge.forgespi.language.IModInfo;
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
                .map(entrypoint -> new PlugInContainer(entrypoint.getProvider().getMetadata().getId(), entrypoint.getEntrypoint()))
                .toList();
        //#else
        //$$ return AnnotatedFinder.getInstances(CGPlugin.class, CobbleGenPlugin.class);
        //#endif
    }

    //#if FABRIC<=0
    //$$ static class AnnotatedFinder {
    //$$     public static <T> List<PlugInContainer> getInstances(Class<?> annotationClass, Class<T> instanceClass) {
    //$$         Type annotationType = Type.getType(annotationClass);
    //$$         List<ModFileScanData> allScanData = ModList.get().getAllScanData();
    //$$         List<PlugInContainer> instances = new ArrayList<>();
    //$$         for (ModFileScanData data : allScanData) {
    //$$             List<String> modIds = data.getIModInfoData().stream()
    //$$                     .flatMap(info -> info.getMods().stream())
    //$$                     .map(IModInfo::getModId)
    //$$                     .toList();
    //$$             String modId = "[" + String.join(", ", modIds) + "]";
    //$$
    //$$             Iterable<ModFileScanData.AnnotationData> annotations = data.getAnnotations();
    //$$             for (ModFileScanData.AnnotationData a : annotations) {
    //$$                 if (!(Objects.equals(a.annotationType(), annotationType)))
    //$$                     continue;
    //$$
    //$$                 String className = a.memberName();
    //$$                 try {
    //$$                     Class<?> asmClass = Class.forName(className);
    //$$                     Class<? extends T> asmInstanceClass = asmClass.asSubclass(instanceClass);
    //$$                     Constructor<? extends T> constructor = asmInstanceClass.getDeclaredConstructor();
    //$$                     T instance = constructor.newInstance();
    //$$                     instances.add(new PlugInContainer(modId, (CobbleGenPlugin) instance));
    //$$                 } catch (Throwable t) {
    //$$                     CGLog.error("Failed to load: " + className + " ", t);
    //$$                 }
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