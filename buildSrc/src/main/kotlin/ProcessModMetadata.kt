import java.io.File
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import java.util.Locale

fun File.processModsToml(
    mcVersion: CGVer,
    /**
     * Possible values:
     * - 0 -> Not Forge
     * - 1 -> (Lex)Forge
     * - 2 -> NeoForge
     */
    forge: Int,
) {
    val modsTomlContent = readText(Charsets.UTF_8).let {
        when {
            mcVersion.code == 12001 -> it.replace("#==", "")
            forge == 2 -> it.replace("#<<", "")
            forge == 1 -> it.replace("#>>", "")
            else -> it
        }
    }
    writeText(modsTomlContent)
}

fun File.processMixinsJsonFabric(mcVersion: CGVer) {
    val both = buildList {
        if (mcVersion.code > 11605) {
            addJson("create.CreateFluidReactionsMixin\$PatchE")
            addJson("create.CreateFluidReactionsMixin\$PatchF")
        }
    }
    val mixinsJson = JsonObject(
        lenientJson.decodeFromString<JsonObject>(readText(Charsets.UTF_8)).toMutableMap().apply {
            set("compatibilityLevel", JsonPrimitive(if (mcVersion.code <= 11605) "JAVA_8" else "JAVA_17"))
            set("mixins", JsonArray(both))
            set("client", JsonArray(listOf()))
            set("server", JsonArray(listOf()))
        }
    )
}

fun File.processMixinsJsonForge(mcVersion: CGVer) {
    val both = buildList {
        if (mcVersion.code > 11605) {
            addJson("create.CreateFluidReactionsMixin\$PatchE")
        }
    }
    val server = buildList {
        if (mcVersion.code >= 12004) addJson("network.loader.neoforge.NetworkRegistryMixin")
    }
    val mixinsJson = JsonObject(
        lenientJson.decodeFromString<JsonObject>(readText(Charsets.UTF_8)).toMutableMap().apply {
            set("compatibilityLevel", JsonPrimitive(if (mcVersion.code <= 11605) "JAVA_8" else "JAVA_17"))
            set("mixins", JsonArray(both))
            set("client", JsonArray(listOf()))
            set("server", JsonArray(server))
        }
    )
}

fun File.processMixinsJson(mcVersion: CGVer, loaderName: String) {
    val both = buildList {
        if (mcVersion.code >= 12005) addJson("network.packet.CustomPacketPayloadMixin")
        addJson("CommandsMixin")
        addJson("MinecraftServerMixin")
        if (mcVersion.code > 11605) {
            addJson("create.CreateFluidReactionsMixin\$OFive")
        }
        addJson("fluid.FlowingFluidEventMixin")
        addJson("fluid.FluidEventMixin")
        addJson("fluid.LavaEventMixin")
        if (mcVersion.code >= 12105) {
            addJson("gametest.RegistryDataLoaderMixin\$GameTest")
            addJson("gametest.StructureTemplateManagerMixin\$GameTest")
        }
    }
    val client = buildList {
        if (mcVersion.code < 12005) addJson("network.packet.ClientboundCustomPayloadPacketMixin")
        addJson("network.ClientCommonPacketListenerMixin")
        addJson("network.ConnectionMixin")
    }
    val server = buildList {
        addJson("network.PlayerManagerMixin")
        if (mcVersion.code < 12002) addJson("network.ServerboundCustomPayloadPacketAccessor")
        else addJson("network.ServerConfigurationPacketListenerMixin")
        if (mcVersion.code < 12005) addJson("network.packet.ServerboundCustomPayloadPacketMixin")
        addJson("network.ServerCommonPacketListenerMixin")
    }
    val mixinsJson = JsonObject(
        lenientJson.decodeFromString<JsonObject>(readText(Charsets.UTF_8)).toMutableMap().apply {
            set("compatibilityLevel", JsonPrimitive(if (mcVersion.code <= 11605) "JAVA_8" else "JAVA_17"))
            set("mixins", JsonArray(both))
            set("client", JsonArray(client))
            set("server", JsonArray(server))
            set("plugin", JsonPrimitive("io.github.null2264.cobblegen${if (!loaderName.isEmpty()) ".$loaderName" else ""}.mixin.core.CobbleGenMixinPlugin${
                loaderName.replaceFirstChar {
                    if (it.isLowerCase()) it.titlecase(
                        Locale.getDefault()
                    ) else it.toString()
                }
            }"))
        }
    )
    writeText(prettyJson.encodeToString(JsonObject.serializer(), mixinsJson))
}

fun File.processFabricModJson(mcVersion: CGVer) {
    val jsonObject = JsonObject(
        lenientJson.decodeFromString<JsonObject>(readText(Charsets.UTF_8)).toMutableMap().apply {
            (get("entrypoints") as? JsonObject)?.toMutableMap()?.apply {
                if (mcVersion.code > 11605) {
                    set("jei_mod_plugin", JsonArray(listOf(JsonPrimitive("io.github.null2264.cobblegen.integration.viewer.jei.CGJEIPlugin"))))
                    if (mcVersion.code < 12111) {
                        // FIXME: Enable REI integration for 1.21.11 when REI is updated
                        // REF: https://github.com/shedaniel/RoughlyEnoughItems/pull/1989
                        set("rei_client", JsonArray(listOf(JsonPrimitive("io.github.null2264.cobblegen.integration.viewer.rei.CGREIPlugin"))))
                        // FIXME: Enable EMI integration for 1.21.11 when EMI is updated
                        set("emi", JsonArray(listOf(JsonPrimitive("io.github.null2264.cobblegen.integration.viewer.emi.CGEMIPlugin"))))
                    }
                }
                set(
                    "cobblegen_plugin",
                    JsonArray(buildList {
                        addJson("io.github.null2264.cobblegen.integration.BuiltInPlugin")
                        if (mcVersion.code > 11605) addJson("io.github.null2264.cobblegen.integration.CreatePlugin")
                    }),
                )
            }?.let {
                set("entrypoints", JsonObject(it))
            }
        }
    )

    writeText(prettyJson.encodeToString(JsonObject.serializer(), jsonObject))
}
