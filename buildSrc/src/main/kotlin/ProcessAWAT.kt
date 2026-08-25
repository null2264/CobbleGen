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

        if (mcVersion < 260100) {
            appendLine("accessible class net/minecraft/resources/RegistryDataLoader\$Loader")
        }

        if (mcVersion >= 260100) {
            appendLine("accessible field net/minecraft/resources/RegistryLoadTask registry Lnet/minecraft/core/WritableRegistry;")
        } else {
            appendLine($$"accessible class net/minecraft/world/level/levelgen/structure/templatesystem/StructureTemplateManager$Source")
            appendLine($$"accessible method net/minecraft/world/level/levelgen/structure/templatesystem/StructureTemplateManager$Source <init> (Ljava/util/function/Function;Ljava/util/function/Supplier;)V")
        }
    }
    writeText(content)
    return this
}

fun File.processAT(
    mcVersion: Int,
): File {
    val content = buildString {
        if (mcVersion < 260100) {
            appendLine("public net.minecraft.resources.RegistryDataLoader\$Loader")
        }

        if (mcVersion >= 260100) {
            appendLine("public net.minecraft.resources.RegistryLoadTask registry")
        } else {
            appendLine($$"public net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager$Source")
            appendLine($$"public net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager$Source <init>(Ljava/util/function/Function;Ljava/util/function/Supplier;)V")
        }
    }
    writeText(content)
    return this
}
