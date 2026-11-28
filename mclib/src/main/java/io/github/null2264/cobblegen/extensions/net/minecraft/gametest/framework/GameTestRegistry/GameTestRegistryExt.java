#if MC<=11605
package io.github.null2264.cobblegen.extensions.net.minecraft.gametest.framework.GameTestRegistry;

import manifold.ext.rt.api.Jailbreak;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.gametest.framework.GameTestRegistry;
import net.minecraft.gametest.framework.StructureUtils;
import net.minecraft.gametest.framework.TestFunction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Rotation;
import sun.misc.Unsafe;

import java.lang.reflect.Field;
import java.util.function.Consumer;

@manifold.ext.rt.api.Extension
public class GameTestRegistryExt {
    private static final Unsafe UNSAFE = getUnsafe();

    private static Unsafe getUnsafe() {
        try {
            Field f = Unsafe.class.getDeclaredField("theUnsafe");
            f.setAccessible(true);
            return (Unsafe) f.get(null);
        } catch (ReflectiveOperationException e) {
            throw new ExceptionInInitializerError(e);
        }
    }

    private static TestFunction createFunction(
        String batchName,
        String testName,
        String structureName,
        boolean required,
        Consumer<GameTestHelper> function,
        int maxTicks,
        long setupTicks,
        Rotation rotation
    ) throws ReflectiveOperationException {
        TestFunction instance = (TestFunction) UNSAFE.allocateInstance(TestFunction.class);
        setField(instance, "batchName", batchName);
        setField(instance, "testName", testName);
        setField(instance, "structureName", structureName);
        setBooleanField(instance, "required", required);
        setField(instance, "function", function);
        setIntField(instance, "maxTicks", maxTicks);
        setLongField(instance, "setupTicks", setupTicks);
        setField(instance, "rotation", rotation);
        return instance;
    }

    private static void setField(Object target, String name, Object value) throws ReflectiveOperationException {
        Field field = TestFunction.class.getDeclaredField(name);
        field.setAccessible(true);
        field.set(target, value);
    }

    private static void setBooleanField(Object target, String name, boolean value) throws ReflectiveOperationException {
        Field field = TestFunction.class.getDeclaredField(name);
        field.setAccessible(true);
        field.setBoolean(target, value);
    }

    private static void setIntField(Object target, String name, int value) throws ReflectiveOperationException {
        Field field = TestFunction.class.getDeclaredField(name);
        field.setAccessible(true);
        field.setInt(target, value);
    }

    private static void setLongField(Object target, String name, long value) throws ReflectiveOperationException {
        Field field = TestFunction.class.getDeclaredField(name);
        field.setAccessible(true);
        field.setLong(target, value);
    }

    @manifold.ext.rt.api.Extension
    public static void register(ResourceLocation id, Consumer<GameTestHelper> function) {
        TestFunction test = null;

        try {
            test = createFunction(
                id.getNamespace(),
                id.getPath(),
                "empty",
                true,
                function,
                120,
                0L,
                StructureUtils.getRotationForRotationSteps(0)
            );
        } catch (ReflectiveOperationException e) {
            return;
        }

        @Jailbreak GameTestRegistry clazz = null;
        clazz.testFunctions.add(test);
    }
}
#endif
