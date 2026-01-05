package io.github.null2264.cobblegen.util;

import io.github.null2264.cobblegen.compat.LoaderCompat;
import io.github.null2264.cobblegen.compat.RegistryCompat;
import io.github.null2264.cobblegen.data.CGIdentifier;
import net.minecraft.core.BlockPos;
#if MC<=11902
import net.minecraft.core.Registry;
#endif
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Fluid;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static io.github.null2264.cobblegen.CobbleGen.MOD_ID;

#if MC>11605
import net.minecraft.core.HolderSet;
import net.minecraft.tags.TagKey;
#else
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.Tag;
#endif

public class Util
{
    public static ResourceLocation identifierOf(GeneratorType type) {
        return identifierOf(type.name().toLowerCase());
    }

    public static ResourceLocation identifierOf(String id) {
        return identifierOf(MOD_ID, id);
    }

    public static ResourceLocation identifierOf(String namespace, String id) {
        #if MC>=12100
        return ResourceLocation.fromNamespaceAndPath(namespace, id);
        #else
        return new ResourceLocation(namespace, id);
        #endif
    }

    @NotNull
    public static <T> T notNullOr(@Nullable T nullable, @NotNull T notNull) {
        if (nullable == null)
            return notNull;
        return nullable;
    }

    @NotNull
    public static <T> Optional<T> optional(@Nullable T nullable) {
        if (nullable == null)
            return Optional.empty();
        return Optional.of(nullable);
    }

    public static boolean isPortingLibLoaded() {
        return LoaderCompat.isModLoaded("porting_lib");
    }

    public static boolean isAnyRecipeViewerLoaded() {
        return LoaderCompat.isModLoaded("roughlyenoughitems") ||
               LoaderCompat.isModLoaded("jei") ||
               LoaderCompat.isModLoaded("emi");
    }

    public static Fluid getFluid(CGIdentifier id) {
        return RegistryCompat.fluid()
            #if MC>12101
            .getValue(id.toMC());
            #else
            .get(id.toMC());
            #endif
    }

    public static CGIdentifier getFluidId(Fluid fluid) {
        return CGIdentifier.fromMC(RegistryCompat.fluid().getKey(fluid));
    }

    public static Block getBlock(CGIdentifier id) {
        return RegistryCompat.block()
            #if MC>12101
            .getValue(id.toMC());
            #else
            .get(id.toMC());
            #endif
    }

    public static CGIdentifier getBlockId(Block block) {
        return CGIdentifier.fromMC(RegistryCompat.block().getKey(block));
    }

    public static List<CGIdentifier> getTaggedBlockIds(CGIdentifier tagId) {
        #if MC>11605
        final TagKey<Block> blockTag = TagKey.create(
                #if MC<=11902
                Registry.BLOCK_REGISTRY,
                #else
                net.minecraft.core.registries.Registries.BLOCK,
                #endif
                tagId.toMC()
        );
        #else
        final Tag<Block> blockTag = BlockTags.getAllTags().getTag(tagId.toMC());
        #endif

        #if MC>11605
        final Optional<HolderSet.Named<Block>> blockList = RegistryCompat.block()
            #if MC>=12102
            .get(blockTag);
            #else
            .getTag(blockTag);
            #endif
        #else
        final Optional<List<Block>> blockList = Optional.ofNullable(blockTag != null ? blockTag.getValues() : null);
        #endif

        ArrayList<CGIdentifier> blockIds = new ArrayList<>();
        blockList.ifPresent(t -> t.stream().forEach(taggedBlock -> {
            #if MC>11605
            Optional<ResourceKey<Block>> key = taggedBlock.unwrapKey();
            key.ifPresent(k -> blockIds.add(CGIdentifier.fromMC(k.location())));
            #else
            blockIds.add(getBlockId(taggedBlock));
            #endif
        }));
        return blockIds;
    }

    public static String getDimension(LevelAccessor level) {
        RegistryAccess access =
            #if FORGE && MC<12002
            level.registryAccessCompat();
            #else
            level.registryAccess();
            #endif
        CGIdentifier dim = CGIdentifier.fromMC(access
            #if MC>12101
            .lookupOrThrow(
            #else
            .registryOrThrow(
            #endif
                    #if MC<=11902
                    Registry.DIMENSION_TYPE_REGISTRY
                    #else
                    net.minecraft.core.registries.Registries.DIMENSION_TYPE
                    #endif
            ).getKey(level.dimensionType()));
        return dim != null ? dim.toString() : "minecraft:overworld";
    }

    @Nullable
    public static String getBiome(LevelAccessor level, BlockPos position) {
        RegistryAccess access =
            #if FORGE && MC<12002
            level.registryAccessCompat();
            #else
            level.registryAccess();
            #endif
        CGIdentifier biome = CGIdentifier.fromMC(access
            #if MC>12101
            .lookupOrThrow(
            #else
            .registryOrThrow(
            #endif
                    #if MC<=11902
                    Registry.BIOME_REGISTRY
                    #else
                    net.minecraft.core.registries.Registries.BIOME
                    #endif
            ).getKey(
                    level.getBiome(position)
                    #if MC>=11802
                        .value()
                    #endif
            ));
        return biome != null ? biome.toString() : null;
    }
}
