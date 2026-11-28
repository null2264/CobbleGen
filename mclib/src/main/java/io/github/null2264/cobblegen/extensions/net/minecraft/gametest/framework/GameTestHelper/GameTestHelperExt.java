#if MC<=11605
package io.github.null2264.cobblegen.extensions.net.minecraft.gametest.framework.GameTestHelper;

import manifold.ext.rt.api.This;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTestAssertPosException;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.gametest.framework.GameTestSequence;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import sun.misc.Unsafe;

import java.lang.reflect.Field;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;

@manifold.ext.rt.api.Extension
public class GameTestHelperExt {
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

    private static GameTestSequence createTestSequence(GameTestHelper parent) throws ReflectiveOperationException {
        GameTestSequence instance = (GameTestSequence) UNSAFE.allocateInstance(GameTestSequence.class);
        setField(instance, "parent", parent);
        setField(instance, "lastTick", parent.jailbreak().testInfo.jailbreak().tickCount);
        return instance;
    }

    private static void setField(GameTestSequence target, String name, Object value) throws ReflectiveOperationException {
        Field field = GameTestAssertPosException.class.getDeclaredField(name);
        field.setAccessible(true);
        field.set(target, value);
    }

    private static RuntimeException createException(
        BlockPos absolutePos,
        BlockPos relativePos,
        Long tick
    ) {
        try {
            GameTestAssertPosException instance = (GameTestAssertPosException) UNSAFE.allocateInstance(GameTestAssertPosException.class);
            setField(instance, "absolutePos", absolutePos);
            setField(instance, "relativePos", relativePos);
            setField(instance, "tick", tick);
            return instance;
        } catch (ReflectiveOperationException e) {
            return new RuntimeException(e);
        }
    }

    private static void setField(GameTestAssertPosException target, String name, Object value) throws ReflectiveOperationException {
        Field field = GameTestAssertPosException.class.getDeclaredField(name);
        field.setAccessible(true);
        field.set(target, value);
    }

    public static BlockPos absolutePos(@This GameTestHelper thiz, BlockPos pos) {
        BlockPos structureBlockPos = thiz.jailbreak().testInfo.getStructureBlockPos();
        assert structureBlockPos != null;
        BlockPos actualPos = structureBlockPos.offset(pos);
        return StructureTemplate.transform(actualPos, Mirror.NONE, thiz.jailbreak().testInfo.getRotation(), structureBlockPos);
    }

    public static void setBlock(@This GameTestHelper thiz, BlockPos pos, Block block) {
        setBlock(thiz, pos, block.defaultBlockState());
    }

    public static void setBlock(@This GameTestHelper thiz, BlockPos pos, BlockState blockState) {
        thiz.jailbreak().testInfo.getLevel().setBlock(absolutePos(thiz, pos), blockState, 3);
    }

    public static void succeedWhen(@This GameTestHelper thiz, Runnable runnable) {
        try {
            createTestSequence(thiz).tickAndFailIfNotComplete(0);
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
    }

    public static void assertBlockPresent(@This GameTestHelper thiz, Block block, BlockPos blockPos) {
        BlockState blockState = getBlockState(thiz, blockPos);
        assertBlock(
            thiz, blockPos, block2 -> blockState.is(block), () -> "Expected " + block.getName().getString() + ", got " + blockState.getBlock().getName().getString()
        );
    }

    public static void assertBlock(@This GameTestHelper thiz, BlockPos blockPos, Predicate<Block> predicate, Supplier<String> supplier) {
        assertBlockState(thiz, blockPos, blockState -> predicate.test(blockState.getBlock()), supplier);
    }

    public static void assertBlockState(@This GameTestHelper thiz, BlockPos blockPos, Predicate<BlockState> predicate, Supplier<String> supplier) {
        BlockState blockState = getBlockState(thiz, blockPos);
        if (!predicate.test(blockState)) {
            throw createException(absolutePos(thiz, blockPos), blockPos, thiz.jailbreak().testInfo.jailbreak().tickCount);
        }
    }

    public static BlockState getBlockState(@This GameTestHelper thiz, BlockPos blockPos) {
        return thiz.jailbreak().testInfo.getLevel().getBlockState(absolutePos(thiz, blockPos));
    }
}
#endif
