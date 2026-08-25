#if MC>=12105
package io.github.null2264.cobblegen.mixin.gametest;

import com.google.common.collect.ImmutableList;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.datafixers.DataFixer;
import io.github.null2264.cobblegen.data.CGIdentifier;
import net.minecraft.core.HolderGetter;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.resources.FileToIdConverter;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;
import net.minecraft.world.level.storage.LevelStorageSource;
import org.apache.commons.io.IOUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.io.IOException;
import java.util.Optional;
import java.util.stream.Stream;

@Mixin(StructureTemplateManager.class)
public abstract class StructureTemplateManagerMixin$GameTest {

    @Shadow
    private ResourceManager resourceManager;

    @Shadow
    public abstract StructureTemplate readStructure(CompoundTag nbt);

    @Unique
    private static final FileToIdConverter GAMETEST_STRUCTURE_LISTER = new FileToIdConverter("gametest/structure", ".snbt");

    @Unique
    private Optional<StructureTemplate> cobblegen$loadSnbtFromResource(
        net.minecraft.resources.
        #if MC>=12111
        Identifier
        #else
        ResourceLocation
        #endif
        id
    ) {
        CGIdentifier path = CGIdentifier.fromMC(GAMETEST_STRUCTURE_LISTER.idToFile(id));
        Optional<Resource> resource = this.resourceManager.getResource(path.toMC());

        if (resource.isPresent()) {
            try {
                String snbt = IOUtils.toString(
                    resource.orElseThrow().openAsReader()
                );
                CompoundTag nbt = NbtUtils.snbtToStructure(snbt);
                return Optional.of(this.readStructure(nbt));
            } catch (IOException | CommandSyntaxException e) {
                throw new RuntimeException("Failed to load GameTest structure " + id, e);
            }
        }

        return Optional.empty();
    }

    @Unique
    private Stream<
        net.minecraft.resources.
        #if MC>=12111
        Identifier
        #else
        ResourceLocation
        #endif
    > cobblegen$listSnbtStructures() {
        return GAMETEST_STRUCTURE_LISTER.listMatchingResources(resourceManager).keySet().stream().map(GAMETEST_STRUCTURE_LISTER::fileToId);
    }

    @Inject(
        method = "<init>",
        at = @At(
            value = "INVOKE",
            target = "Lcom/google/common/collect/ImmutableList$Builder;add(Ljava/lang/Object;)Lcom/google/common/collect/ImmutableList$Builder;",
            ordinal = 2,
            shift = At.Shift.AFTER,
            remap = false
        )
    )
    private void addCobbleGenStructureProvider(ResourceManager resourceManager,
                                               LevelStorageSource.LevelStorageAccess levelStorageAccess,
                                               DataFixer dataFixer,
                                               HolderGetter
                                                   #if MC>=260100
                                                   <net.minecraft.world.level.block.Block>
                                                   #endif
                                                       holderGetter,
                                               CallbackInfo ci,
                                               @Local ImmutableList.Builder<
                                                   #if MC>=260100
                                                   net.minecraft.world.level.levelgen.structure.templatesystem.loader.TemplateSource
                                                   #else
                                                   StructureTemplateManager.Source
                                                   #endif
                                               > builder) {
        builder.add(
            #if MC>=260100
            new net.minecraft.world.level.levelgen.structure.templatesystem.loader.TemplateSource(dataFixer, holderGetter) {
                @Override
                public Optional<StructureTemplate> load(net.minecraft.resources.Identifier id) {
                    return cobblegen$loadSnbtFromResource(id);
                }

                @Override
                public Stream<net.minecraft.resources.Identifier> list() {
                    return cobblegen$listSnbtStructures();
                }
            }
            #else
            new StructureTemplateManager.Source(this::cobblegen$loadSnbtFromResource, this::cobblegen$listSnbtStructures)
            #endif
        );
    }
}
#endif
