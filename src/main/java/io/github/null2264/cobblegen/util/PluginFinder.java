package io.github.null2264.cobblegen.util;

import io.github.null2264.cobblegen.CobbleGenPlugin;
import org.jetbrains.annotations.ApiStatus;

import java.util.*;

import static io.github.null2264.cobblegen.compat.CollectionCompat.streamToList;

//#if FABRIC>=1
import net.fabricmc.loader.api.FabricLoader;
//#else
//$$ import java.lang.reflect.Constructor;
//$$ import io.github.null2264.cobblegen.CGPlugin;
    //#if FORGE>=2 && MC>=1.20.2
    //$$ import net.neoforged.fml.ModList;
    //$$ import net.neoforged.neoforgespi.language.IModInfo;
    //$$ import net.neoforged.neoforgespi.language.ModFileScanData;
    //#else
    //$$ import net.minecraftforge.fml.ModList;
    //$$ import net.minecraftforge.forgespi.language.IModInfo;
    //$$ import net.minecraftforge.forgespi.language.ModFileScanData;
    //#endif
//$$ import org.objectweb.asm.Type;
//#endif

public class PluginFinder
{
    public static List<PlugInContainer> getModPlugins() {
        //#if FABRIC>=1
        return streamToList(
                FabricLoader.getInstance()
                        .getEntrypointContainers("cobblegen_plugin", CobbleGenPlugin.class)
                        .stream()
                        .map(entrypoint -> new PlugInContainer(entrypoint.getProvider().getMetadata().getId(), entrypoint.getEntrypoint()))
        );
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
    //$$             List<String> modIds = streamToList(
    //$$                     data.getIModInfoData().stream()
    //$$                            .flatMap(info -> info.getMods().stream())
    //$$                            .map(IModInfo::getModId)
    //$$             );
    //$$             String modId = "[" + String.join(", ", modIds) + "]";
    //$$
    //$$             Iterable<ModFileScanData.AnnotationData> annotations = data.getAnnotations();
    //$$             for (ModFileScanData.AnnotationData a : annotations) {

    //$$                 if (!(Objects.equals(
                                 //#if MC>1.16.5
    //$$                         a.annotationType(),
                                 //#else
                                 //$$ a.getAnnotationType(),
                                 //#endif
    //$$                         annotationType)))
    //$$                     continue;

    //$$                 String className =
                                 //#if MC>1.16.5
    //$$                         a.memberName();
                                 //#else
                                 //$$ a.getMemberName();
                                 //#endif
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

    @ApiStatus.Internal
    public static class PlugInContainer {
        final String modId;
        final CobbleGenPlugin plugin;

        public PlugInContainer(String modId, CobbleGenPlugin plugin) {
            this.modId = modId;
            this.plugin = plugin;
        }

        public String getModId() {
            return modId;
        }

        public CobbleGenPlugin getPlugin() {
            return plugin;
        }
    }
}