import java.io.File
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlin.text.appendLine

fun File.processAW(
    mcVersion: Int,
): File {
    val content = buildString {
        val mappingType = if (mcVersion >= 260100) "official" else "named"
        appendLine("classTweaker v1 ${mappingType}")

        appendLine("accessible class net/minecraft/resources/RegistryDataLoader\$Loader")

        val structureProviderName = if (mcVersion >= 260100) "\$Provider" else "\$Source"
        appendLine("accessible class net/minecraft/world/level/levelgen/structure/templatesystem/StructureTemplateManager$structureProviderName")
        appendLine("accessible method net/minecraft/world/level/levelgen/structure/templatesystem/StructureTemplateManager$structureProviderName <init> (Ljava/util/function/Function;Ljava/util/function/Supplier;)V")
        if (mcVersion >= 260100) {
            appendLine("accessible field net/minecraft/resources/RegistryLoadTask registry Lnet/minecraft/core/WritableRegistry;")
        }
    }
    writeText(content)
    return this
}

fun File.processAT(
    mcVersion: Int,
): File {
    val content = buildString {
        appendLine("public net.minecraft.resources.RegistryDataLoader\$Loader")

        val structureProviderName = if (mcVersion >= 260100) "\$Provider" else "\$Source"
        appendLine("public net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager$structureProviderName")
        appendLine("public net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager$structureProviderName <init>(Ljava/util/function/Function;Ljava/util/function/Supplier;)V")
        if (mcVersion >= 260100) {
            appendLine("public net.minecraft.resources.RegistryLoadTask registry")
        }
    }
    writeText(content)
    return this
}
