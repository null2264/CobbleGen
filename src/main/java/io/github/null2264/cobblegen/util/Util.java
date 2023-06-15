package io.github.null2264.cobblegen.util;

import io.github.null2264.cobblegen.compat.RegistryCompat;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Fluid;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static io.github.null2264.cobblegen.CobbleGen.MOD_ID;

public class Util
{
    public static ResourceLocation identifierOf(GeneratorType type) {
        return new ResourceLocation(MOD_ID, type.name().toLowerCase());
    }

    public static ResourceLocation identifierOf(String id) {
        return new ResourceLocation(MOD_ID, id);
    }

    @NotNull
    public static <T> T notNullOr(@Nullable T nullable, @NotNull T notNull) {
        if (nullable == null)
            return notNull;
        return nullable;
    }

    public static boolean isPortingLibLoaded() {
        return FabricLoader.getInstance().isModLoaded("porting_lib");
    }

    public static boolean isAnyRecipeViewerLoaded() {
        return FabricLoader.getInstance().isModLoaded("roughlyenoughitems") ||
               FabricLoader.getInstance().isModLoaded("jei") ||
               FabricLoader.getInstance().isModLoaded("emi");
    }

    public static Fluid getFluid(ResourceLocation id) {
        return RegistryCompat.fluid().get(id);
    }

    public static ResourceLocation getFluidId(Fluid fluid) {
        return RegistryCompat.fluid().getKey(fluid);
    }

    public static Block getBlock(ResourceLocation id) {
        return RegistryCompat.block().get(id);
    }

    public static ResourceLocation getBlockId(Block block) {
        return RegistryCompat.block().getKey(block);
    }

    public static List<ResourceLocation> getTaggedBlockIds(ResourceLocation tagId) {
        TagKey<Block> blockTag = TagKey.create(
                //#if MC<=11902
                Registry.BLOCK_REGISTRY,
                //#else
                //$$ net.minecraft.core.registries.Registries.BLOCK,
                //#endif
                tagId
        );

        ArrayList<ResourceLocation> blockIds = new ArrayList<>();
        RegistryCompat.block().getTag(blockTag).ifPresent(t -> t.stream().forEach(taggedBlock -> {
            Optional<ResourceKey<Block>> key = taggedBlock.unwrapKey();
            if (key.isPresent()) {
                ResourceKey<Block> actualKey = key.get();
                blockIds.add(actualKey.registry());
            }
        }));
        return blockIds;
    }

    public static String getDimension(LevelAccessor level) {
        ResourceLocation dim = level.registryAccess().registryOrThrow(
                //#if MC<=11902
                Registry.DIMENSION_TYPE_REGISTRY
                //#else
                //$$ net.minecraft.core.registries.Registries.DIMENSION_TYPE
                //#endif
        ).getKey(level.dimensionType());
        return dim != null ? dim.toString() : "minecraft:overworld";
    }
}